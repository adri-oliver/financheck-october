package com.financheck.presentation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MetaDTO {
    private Long id;

    private String nome;
    private String name;

    private BigDecimal valorAlvo;
    private BigDecimal target;

    private BigDecimal valorAcumulado;

    private BigDecimal current;

    private LocalDate prazo;

    private Long perfilId;
    private Long usuarioId;

    private double progresso;

    private String categoria;
    private String category;
}