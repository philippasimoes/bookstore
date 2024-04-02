package com.bookstore.shipping_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoApiObject {

  @JsonProperty("CP")
  private String cp;

  @JsonProperty("CP4")
  private String cp4;
  @JsonProperty("CP3")
  private String cp3;

  @JsonProperty("Distrito")
  private String distrito;

  @JsonProperty("Concelho")
  private String concelho;

  @JsonProperty("Localidade")
  private String localidade;

  @JsonProperty("Designação Postal")
  private String designacaoPostal;

  private Object[] partes;

  private Ponto[] pontos;

  private String[] ruas;

  private double[] centro;

  private double[][] poligono;

  private double[] centroide;
  private double[] centroDeMassa;

  private String municipio;

  private String codigoineDistrito;
  private String codigoineMunicipio;
}
