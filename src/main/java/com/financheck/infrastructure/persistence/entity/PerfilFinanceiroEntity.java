package com.financheck.infrastructure.persistence.entity;

import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TB_PERFIL_FINANCEIRO")
public class PerfilFinanceiroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfilFinanceiro tipo;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
}
