package com.financheck.domain.entity;

import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerfilFinanceiro {
    private Long id;
    private String nome;
    private TipoPerfilFinanceiro tipo;
    private Long usuarioId;
}
