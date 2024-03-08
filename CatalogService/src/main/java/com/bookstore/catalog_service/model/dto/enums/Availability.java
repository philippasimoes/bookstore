package com.bookstore.catalog_service.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Book availability status.
 *
 * @author Filipa Simões
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Availability {
  TO_BE_LAUNCHED,
  ON_PRE_ORDER,
  ON_ORDER,
  AVAILABLE,
  SOLD_OUT
}
