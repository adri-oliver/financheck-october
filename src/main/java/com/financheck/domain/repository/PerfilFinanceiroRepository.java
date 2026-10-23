package com.financheck.domain.repository;

import com.financheck.domain.entity.PerfilFinanceiro;

import java.util.List;
import java.util.Optional;

public interface PerfilFinanceiroRepository {
    PerfilFinanceiro salvar(PerfilFinanceiro perfil);
    Optional<PerfilFinanceiro> buscarPorId(Long id);
    List<PerfilFinanceiro> buscarPorUsuario(Long usuarioId);
    Optional<PerfilFinanceiro> buscarResponsavelPorUsuario(Long usuarioId);
    void remover(Long id);
}
