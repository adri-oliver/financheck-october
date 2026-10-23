package com.financheck.usecase.limite;

import com.financheck.domain.entity.Categoria;
import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Calcula o quanto já foi gasto em um limite: soma das despesas da categoria do limite,
 * no perfil do limite, lançadas a partir da data de cadastro do limite.
 */
@Component
@RequiredArgsConstructor
public class CalcularGastoLimiteUseCase {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    public LimiteGastoResumo execute(LimiteGasto limite) {
        BigDecimal gasto = transacaoRepository.somarDespesasPorCategoriaDesde(
                limite.getPerfilId(), limite.getCategoriaId(), limite.getDataCadastro());

        String nomeCategoria = categoriaRepository.buscarPorId(limite.getCategoriaId())
                .map(Categoria::getNome)
                .orElse("Sem categoria");

        return new LimiteGastoResumo(
                limite.getId(),
                limite.getNome(),
                limite.getCategoriaId(),
                nomeCategoria,
                limite.getValorLimite(),
                gasto,
                limite.calcularRestante(gasto),
                limite.calcularPercentual(gasto),
                limite.excedeu(gasto),
                limite.getDataCadastro(),
                limite.getPerfilId()
        );
    }
}
