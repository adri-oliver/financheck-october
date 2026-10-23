package com.financheck.usecase.meta;

import com.financheck.domain.entity.MetaFinanceira;
import com.financheck.domain.repository.MetaRepository;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CriarMetaUseCase {

    private final MetaRepository metaRepository;
    private final PerfilFinanceiroRepository perfilFinanceiroRepository;

    public MetaFinanceira execute(MetaFinanceira meta) {
        perfilFinanceiroRepository.buscarPorId(meta.getPerfilId())
                .orElseThrow(() -> new RuntimeException("Perfil financeiro nao encontrado: " + meta.getPerfilId()));

        // Preserva o valorAcumulado enviado; usa zero apenas se não informado
        if (meta.getValorAcumulado() == null) {
            meta.setValorAcumulado(BigDecimal.ZERO);
        }

        return metaRepository.salvar(meta);
    }
}