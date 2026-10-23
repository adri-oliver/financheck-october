package com.financheck.usecase.limite;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Limite de gasto já combinado com o valor efetivamente gasto na categoria. */
public record LimiteGastoResumo(
        Long id,
        String nome,
        Long categoriaId,
        String categoria,
        BigDecimal valorLimite,
        BigDecimal valorGasto,
        BigDecimal valorRestante,
        double percentual,
        boolean excedido,
        LocalDate dataCadastro,
        Long perfilId
) {}
