package com.financheck.presentation.dto;

import com.financheck.domain.entity.enums.TipoTransacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoDTO {
    private Long id;

    private TipoTransacao tipo;

    private BigDecimal valor;
    private BigDecimal amount;

    private LocalDate data;

    private String descricao;
    private String nome;
    private String name;

    private Long categoriaId;
    private String categoria;

    private Long perfilId;
    private Long usuarioId;
}