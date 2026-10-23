package com.financheck.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "TB_LIMITE_GASTO")
public class LimiteGastoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(name = "valor_limite", nullable = false)
    private BigDecimal valorLimite;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "perfil_id", nullable = false)
    private Long perfilId;
}
