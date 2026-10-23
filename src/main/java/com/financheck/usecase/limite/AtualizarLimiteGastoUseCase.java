package com.financheck.usecase.limite;

import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.LimiteGastoRepository;
import com.financheck.usecase.categoria.ResolverCategoriaPorNomeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AtualizarLimiteGastoUseCase {

    private final LimiteGastoRepository limiteGastoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ResolverCategoriaPorNomeUseCase resolverCategoriaPorNomeUseCase;

    /**
     * Atualiza nome, categoria e/ou valor do limite. Campos nulos/vazios são mantidos.
     * Se a categoria mudar, o acompanhamento recomeça: a data de cadastro passa a ser hoje,
     * para que gastos antigos da nova categoria não contem para este limite.
     */
    public LimiteGasto execute(Long id, String nome, Long categoriaId, String categoria, BigDecimal valorLimite) {
        LimiteGasto limite = limiteGastoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Limite de gasto não encontrado: " + id));

        if (nome != null && !nome.isBlank()) {
            limite.setNome(nome.trim());
        }

        if (valorLimite != null) {
            if (valorLimite.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor do limite deve ser maior que zero");
            }
            limite.setValorLimite(valorLimite);
        }

        boolean informouCategoria = categoriaId != null || (categoria != null && !categoria.isBlank());
        if (informouCategoria) {
            Long novaCategoriaId = resolverCategoriaPorNomeUseCase.execute(categoriaId, categoria, limite.getPerfilId());

            categoriaRepository.buscarPorId(novaCategoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + novaCategoriaId));

            if (!novaCategoriaId.equals(limite.getCategoriaId())) {
                limite.setCategoriaId(novaCategoriaId);
                limite.setDataCadastro(LocalDate.now());
            }
        }

        return limiteGastoRepository.atualizar(limite);
    }
}
