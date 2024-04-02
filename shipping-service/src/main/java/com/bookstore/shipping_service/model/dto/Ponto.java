package com.bookstore.shipping_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ponto {

  private String id;
  private String rua;
  private String casa;
  private double[] coordenadas;
}
