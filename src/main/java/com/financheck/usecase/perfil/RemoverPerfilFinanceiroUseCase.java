package com.financheck.usecase.perfil;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverPerfilFinanceiroUseCase {

    private final PerfilFinanceiroRepository perfilFinanceiroRepository;

    public void execute(Long id, Long perfilSolicitanteId) {
        PerfilFinanceiro perfil = perfilFinanceiroRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Perfil financeiro nao encontrado: " + id));

        if (perfilSolicitanteId != null) {
            PerfilFinanceiro solicitante = perfilFinanceiroRepository.buscarPorId(perfilSolicitanteId)
                    .orElseThrow(() -> new RuntimeException("Perfil solicitante nao encontrado: " + perfilSolicitanteId));
            if (solicitante.getTipo() == TipoPerfilFinanceiro.FILHO
                    && perfil.getTipo() == TipoPerfilFinanceiro.RESPONSAVEL) {
                throw new RuntimeException("Perfil FILHO nao pode excluir perfil RESPONSAVEL");
            }
        }

        perfilFinanceiroRepository.remover(id);
    }
}
