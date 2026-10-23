package com.financheck.domain.repository;

import com.financheck.domain.entity.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository {
    Transacao salvar(Transacao transacao);
    Optional<Transacao> buscarPorId(Long id);
    List<Transacao> buscarPorPeriodo(Long perfilId, LocalDate inicio, LocalDate fim);
    Transacao atualizar(Transacao transacao);
    void remover(Long id);
    List<Transacao> buscarComFiltros(Long perfilId, LocalDate inicio, LocalDate fim, Long categoriaId, String descricao);

    /** Soma das DESPESAS do perfil na categoria, de {@code inicio} em diante (inclusive). Retorna ZERO se não houver. */
    BigDecimal somarDespesasPorCategoriaDesde(Long perfilId, Long categoriaId, LocalDate inicio);
}
