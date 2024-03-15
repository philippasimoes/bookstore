package com.bookstore.shipping_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shipment")
@Tag(name = "Shipment endpoints")
public class ShipmentController {}
