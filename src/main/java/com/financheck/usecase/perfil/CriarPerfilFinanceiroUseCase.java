package com.financheck.usecase.perfil;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarPerfilFinanceiroUseCase {

    private final PerfilFinanceiroRepository perfilFinanceiroRepository;
    private final UsuarioRepository usuarioRepository;

    public PerfilFinanceiro execute(PerfilFinanceiro perfil) {
        usuarioRepository.buscarPorId(perfil.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + perfil.getUsuarioId()));
        return perfilFinanceiroRepository.salvar(perfil);
    }
}
