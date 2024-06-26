package com.bookstore.return_service.service;

import com.bookstore.return_service.exception.ResourceNotFoundException;
import com.bookstore.return_service.model.dto.ReturnDto;
import com.bookstore.return_service.model.dto.ReturnItemDto;
import com.bookstore.return_service.model.dto.enums.RefundType;
import com.bookstore.return_service.model.dto.enums.ReturnStatus;
import com.bookstore.return_service.model.entity.Return;
import com.bookstore.return_service.model.entity.ReturnItem;
import com.bookstore.return_service.model.mapper.ReturnItemMapper;
import com.bookstore.return_service.model.mapper.ReturnMapper;
import com.bookstore.return_service.repository.ReturnItemRepository;
import com.bookstore.return_service.repository.ReturnRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class ReturnService {

    private static final Logger LOGGER = LogManager.getLogger(ReturnService.class);
    private static final String DUMMY_CTT_URL = "http://dummy-ctt:10008/";
    private static final String ORDER_URL = "http://order-service:10003/order/";
    private static final String PAYMENTS_URL = "http://payment-service:10004/payment/order/";
    private static final String CB = "circuitbreaker";
    private static final String RETURN_ID = "returnId";

    @Autowired
    ReturnRepository returnRepository;
    @Autowired
    ReturnItemRepository returnItemRepository;
    @Autowired
    ReturnMapper returnMapper;
    @Autowired
    ReturnItemMapper returnItemMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CircuitBreakerFactory circuitBreakerFactory;

    /**
     * Create a new return.
     *
     * @param returnDto the return dto.
     * @return the created return entry.
     */
    public Map<String, String> createReturn(ReturnDto returnDto) throws ResourceNotFoundException {
        Return returnEntity = returnRepository.save(returnMapper.toEntity(returnDto));

        List<ReturnItem> returnItems = new ArrayList<>();
        Map<String, String> responseMap = new HashMap<>();

        double amount = 0;

        if (!returnDto.getReturnItems().isEmpty()) {
            for (ReturnItemDto returnItemDto : returnDto.getReturnItems()) {
                Map<String, String> map =
                        getOriginalOrderInfo(returnDto.getOrderId(), returnItemDto.getBookId());

                if (map != null && !map.isEmpty()) {
                    amount = getAmount(returnItemDto, map, returnEntity, amount, returnItems);
                } else {
                    throw new ResourceNotFoundException(
                            String.format("Item from order %s does not exist.", returnDto.getOrderId()));
                }
            }
        }

        Map<String, String> map = getPaymentDetails(returnDto.getOrderId());

        if (map != null && !map.isEmpty()) {
            switch (map.get("method")) {
                case "STRIPE" -> returnEntity.setRefundType(RefundType.STRIPE);
                case "PAYPAL" -> returnEntity.setRefundType(RefundType.PAYPAL);
                case "CREDIT_CARD" -> returnEntity.setRefundType(RefundType.CREDIT_CARD);
                default -> {
                }
            }

            returnEntity.setExternalPaymentId(map.get("paymentDetails"));
            responseMap.put("method", returnEntity.getRefundType().name());
            responseMap.put("externalPaymentId", map.get("paymentDetails"));
        }

        returnEntity.setCustomerId(returnDto.getCustomerId());
        returnEntity.setAmountToRefund(amount);
        returnEntity.setReturnStatus(ReturnStatus.OPEN);
        returnEntity.setReturnItems(returnItems);
        Return savedReturn = returnRepository.save(returnEntity);

        responseMap.put("customerId", String.valueOf(returnDto.getCustomerId()));
        responseMap.put(RETURN_ID, String.valueOf(savedReturn.getId()));
        responseMap.put("amount", String.valueOf(amount));

        return responseMap;
    }

    @RabbitListener(queues = "${rabbitmq.queue.event.refunded.name}")
    public void consumeRefundedEvents(String message) {

        LOGGER.log(Level.INFO, "Received message in refund events queue: {}", message);

        updateReturnAfterRefund(message);
    }

    private Map<String, String> getOriginalOrderInfo(int orderId, int bookId) {

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(CB);

        return circuitBreaker.run(
                () -> restTemplate.getForObject(ORDER_URL + orderId + "/item-details/" + bookId, Map.class),
                throwable -> {
                    LOGGER.log(Level.WARN, "Error connecting to Order Service.", throwable);
                    return null;
                });
    }

    private double getAmount(
            ReturnItemDto returnItemDto,
            Map<String, String> map,
            Return returnEntity,
            double amount,
            List<ReturnItem> returnItems) {

        if (Integer.parseInt(map.get("quantity")) >= returnItemDto.getQuantity()) {

            ReturnItem returnItem = returnItemMapper.toEntity(returnItemDto);
            returnItem.setReturnEntity(returnEntity);
            returnItem.setOrderUnitPrice(Double.parseDouble(map.get("unitPrice")));
            returnItem.setUnitWeight(Double.parseDouble(map.get("unitWeight")));
            amount = amount + (returnItem.getOrderUnitPrice() * returnItemDto.getQuantity());
            returnItems.add(returnItemRepository.save(returnItem));
        } else {
            throw new RuntimeException("Quantity to return is higher than quantity from the order");
        }
        return amount;
    }

    /**
     * Update the {@link ReturnStatus} to REFUNDED and add the tracking code.
     *
     * @param message the message from payment service.
     */
    private void updateReturnAfterRefund(String message) {
        Map<Integer, Integer> map;
        try {
            map = objectMapper.readValue(message, Map.class);

            Optional<Return> returnEntity = returnRepository.findById(map.get(RETURN_ID));

            if (returnEntity.isPresent()) {
                returnEntity.get().setReturnStatus(ReturnStatus.REFUNDED);
                returnEntity.get().setTrackingCode(getTrackingCode(map.get(RETURN_ID)));
                returnRepository.save(returnEntity.get());
            } else throw new ResourceNotFoundException("Return entry not found");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Connect to dummy ctt to get the tracking code.
     *
     * @param returnId the return identifier.
     * @return the tracking code.
     */
    private String getTrackingCode(int returnId) {

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(CB);

        return circuitBreaker.run(
                () ->
                        restTemplate.getForObject(
                                DUMMY_CTT_URL + "return-tracking-code?return_id=" + returnId, String.class),
                throwable -> {
                    LOGGER.log(Level.WARN, "Error connecting to dummy ctt.", throwable);
                    return null;
                });
    }

    private Map<String, String> getPaymentDetails(int orderId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(CB);

        return circuitBreaker.run(
                () -> restTemplate.getForObject(PAYMENTS_URL + orderId, Map.class),
                throwable -> {
                    LOGGER.log(Level.WARN, "Error connecting to Payment Service.", throwable);
                    return null;
                });
    }
}
