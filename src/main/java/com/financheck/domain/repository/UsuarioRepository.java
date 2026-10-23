package com.financheck.domain.repository;

import com.financheck.domain.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(Long id);

    Usuario salvar(Usuario usuario);

    Usuario atualizar(Usuario usuario);

    void desativar(Long id);
}