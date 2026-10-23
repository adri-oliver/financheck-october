package com.financheck.presentation.dto;

import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerfilFinanceiroResponseDTO {
    private Long id;
    private String nome;
    private TipoPerfilFinanceiro tipo;
}
