package com.financheck.infrastructure.persistence;

import com.financheck.domain.entity.Transacao;
import com.financheck.domain.entity.enums.TipoTransacao;
import com.financheck.domain.repository.TransacaoRepository;
import com.financheck.infrastructure.persistence.entity.TransacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, Long> {
    List<TransacaoEntity> findByPerfilIdAndDataBetween(Long perfilId, LocalDate inicio, LocalDate fim);

    @Query("""
        SELECT t FROM TransacaoEntity t
        WHERE t.perfilId = :perfilId
        AND t.data BETWEEN :inicio AND :fim
        AND (:categoriaId IS NULL OR t.categoriaId = :categoriaId)
        AND (:descricao IS NULL OR LOWER(t.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')))
    """)
    List<TransacaoEntity> buscarComFiltros(
            @Param("perfilId") Long perfilId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("categoriaId") Long categoriaId,
            @Param("descricao") String descricao
    );

    @Query("""
        SELECT SUM(t.valor) FROM TransacaoEntity t
        WHERE t.perfilId = :perfilId
        AND t.categoriaId = :categoriaId
        AND t.tipo = :tipo
        AND t.data >= :inicio
    """)
    BigDecimal somarPorCategoriaDesde(
            @Param("perfilId") Long perfilId,
            @Param("categoriaId") Long categoriaId,
            @Param("tipo") TipoTransacao tipo,
            @Param("inicio") LocalDate inicio
    );
}


@Component
@RequiredArgsConstructor
public class TransacaoRepositoryImpl implements TransacaoRepository {

    private final TransacaoJpaRepository jpaRepository;

    @Override
    public Transacao salvar(Transacao transacao) {
        TransacaoEntity entity = toEntity(transacao);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Transacao> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Transacao> buscarPorPeriodo(Long perfilId, LocalDate inicio, LocalDate fim) {
        return jpaRepository.findByPerfilIdAndDataBetween(perfilId, inicio, fim)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Transacao atualizar(Transacao transacao) {
        TransacaoEntity entity = toEntity(transacao);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public void remover(Long id) {
        jpaRepository.deleteById(id);
    }

    private TransacaoEntity toEntity(Transacao domain) {
        TransacaoEntity entity = new TransacaoEntity();
        entity.setId(domain.getId());
        entity.setTipo(domain.getTipo());
        entity.setValor(domain.getValor());
        entity.setData(domain.getData());
        entity.setDescricao(domain.getDescricao());
        entity.setCategoriaId(domain.getCategoriaId());
        entity.setPerfilId(domain.getPerfilId());
        return entity;
    }

    private Transacao toDomain(TransacaoEntity entity) {
        Transacao domain = new Transacao();
        domain.setId(entity.getId());
        domain.setTipo(entity.getTipo());
        domain.setValor(entity.getValor());
        domain.setData(entity.getData());
        domain.setDescricao(entity.getDescricao());
        domain.setCategoriaId(entity.getCategoriaId());
        domain.setPerfilId(entity.getPerfilId());
        return domain;
    }

    public List<Transacao> buscarComFiltros(
            Long perfilId,
            LocalDate inicio,
            LocalDate fim,
            Long categoriaId,
            String descricao
    ) {
        return jpaRepository.buscarComFiltros(perfilId, inicio, fim, categoriaId, descricao)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public BigDecimal somarDespesasPorCategoriaDesde(Long perfilId, Long categoriaId, LocalDate inicio) {
        BigDecimal total = jpaRepository.somarPorCategoriaDesde(perfilId, categoriaId, TipoTransacao.DESPESA, inicio);
        return total != null ? total : BigDecimal.ZERO;
    }
}
