package com.financheck.presentation.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LimiteGastoDTO {
    private Long id;

    private String nome;

    /** Nome da categoria (resolvido/criado no backend) - alternativa a categoriaId. */
    private String categoria;
    private Long categoriaId;

    private BigDecimal valorLimite;

    private Long perfilId;
    private Long usuarioId;
}
