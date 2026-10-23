package com.financheck.presentation.dto.usuario;

import com.financheck.presentation.dto.PerfilFinanceiroResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private boolean ativo;
    private boolean controleFamiliar;
    private List<PerfilFinanceiroResponseDTO> perfis;

    public UsuarioResponseDTO(Long id, String nome, String email, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ativo = ativo;
        this.controleFamiliar = false;
        this.perfis = List.of();
    }

    public UsuarioResponseDTO(Long id, String nome, String email, boolean ativo, boolean controleFamiliar) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ativo = ativo;
        this.controleFamiliar = controleFamiliar;
        this.perfis = List.of();
    }
}
