package com.financheck.usecase.limite;

import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.LimiteGastoRepository;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CriarLimiteGastoUseCase {

    private final LimiteGastoRepository limiteGastoRepository;
    private final PerfilFinanceiroRepository perfilFinanceiroRepository;
    private final CategoriaRepository categoriaRepository;

    public LimiteGasto execute(LimiteGasto limite) {
        perfilFinanceiroRepository.buscarPorId(limite.getPerfilId())
                .orElseThrow(() -> new RuntimeException("Perfil financeiro nao encontrado: " + limite.getPerfilId()));

        categoriaRepository.buscarPorId(limite.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + limite.getCategoriaId()));

        if (limite.getValorLimite() == null || limite.getValorLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do limite deve ser maior que zero");
        }

        // A data de cadastro é o marco inicial: só despesas a partir dela entram no cálculo
        limite.setDataCadastro(LocalDate.now());

        return limiteGastoRepository.salvar(limite);
    }
}
