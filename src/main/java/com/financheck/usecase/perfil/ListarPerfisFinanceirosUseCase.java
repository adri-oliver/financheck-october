package com.financheck.usecase.perfil;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarPerfisFinanceirosUseCase {

    private final PerfilFinanceiroRepository perfilFinanceiroRepository;

    public List<PerfilFinanceiro> execute(Long usuarioId) {
        return perfilFinanceiroRepository.buscarPorUsuario(usuarioId);
    }
}
