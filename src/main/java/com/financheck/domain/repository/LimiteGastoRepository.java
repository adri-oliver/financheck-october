package com.financheck.domain.repository;

import com.financheck.domain.entity.LimiteGasto;

import java.util.List;
import java.util.Optional;

public interface LimiteGastoRepository {
    LimiteGasto salvar(LimiteGasto limite);
    Optional<LimiteGasto> buscarPorId(Long id);
    List<LimiteGasto> buscarPorPerfil(Long perfilId);
    LimiteGasto atualizar(LimiteGasto limite);
    void remover(Long id);
}
