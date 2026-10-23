package com.financheck.presentation.dto;

import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PerfilFinanceiroDTO {
    private Long id;

    @NotBlank
    private String nome;

    @NotNull
    private TipoPerfilFinanceiro tipo;

    @NotNull
    private Long usuarioId;
}
