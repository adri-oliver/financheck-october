package com.financheck.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Limite de gastos para uma categoria.
 * Só as despesas da categoria lançadas a partir de {@code dataCadastro} contam para o limite.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LimiteGasto {
    private Long id;
    private String nome;
    private Long categoriaId;
    private BigDecimal valorLimite;
    private LocalDate dataCadastro;
    private Long perfilId;

    /** Percentual consumido do limite (pode passar de 100 quando excedido). */
    public double calcularPercentual(BigDecimal valorGasto) {
        if (valorLimite == null || valorLimite.compareTo(BigDecimal.ZERO) == 0 || valorGasto == null) {
            return 0.0;
        }
        return valorGasto.multiply(BigDecimal.valueOf(100))
                .divide(valorLimite, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /** Quanto ainda resta do limite. Fica negativo quando o limite foi excedido. */
    public BigDecimal calcularRestante(BigDecimal valorGasto) {
        BigDecimal gasto = valorGasto != null ? valorGasto : BigDecimal.ZERO;
        return valorLimite.subtract(gasto);
    }

    public boolean excedeu(BigDecimal valorGasto) {
        return valorGasto != null && valorGasto.compareTo(valorLimite) > 0;
    }
}
