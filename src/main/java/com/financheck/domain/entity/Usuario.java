package com.financheck.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private boolean ativo;
    private Boolean controleFamiliar = false;

    public Usuario(Long id, String nome, String email, String senha, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        this.controleFamiliar = false;
    }
}
