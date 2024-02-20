package com.bookstore.catalog_service.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Availability {
  TO_BE_LAUNCHED,
  ON_PRE_ORDER,
  ON_ORDER,
  AVAILABLE
}
