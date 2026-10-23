package com.financheck.infrastructure.persistence;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.infrastructure.persistence.entity.PerfilFinanceiroEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

interface PerfilFinanceiroJpaRepository extends JpaRepository<PerfilFinanceiroEntity, Long> {
    List<PerfilFinanceiroEntity> findByUsuarioId(Long usuarioId);
    Optional<PerfilFinanceiroEntity> findFirstByUsuarioIdAndTipo(Long usuarioId, TipoPerfilFinanceiro tipo);
}

@Component
@RequiredArgsConstructor
public class PerfilFinanceiroRepositoryImpl implements PerfilFinanceiroRepository {

    private final PerfilFinanceiroJpaRepository jpaRepository;

    @Override
    public PerfilFinanceiro salvar(PerfilFinanceiro perfil) {
        PerfilFinanceiroEntity entity = toEntity(perfil);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<PerfilFinanceiro> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PerfilFinanceiro> buscarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<PerfilFinanceiro> buscarResponsavelPorUsuario(Long usuarioId) {
        return jpaRepository.findFirstByUsuarioIdAndTipo(usuarioId, TipoPerfilFinanceiro.RESPONSAVEL)
                .map(this::toDomain);
    }

    @Override
    public void remover(Long id) {
        jpaRepository.deleteById(id);
    }

    private PerfilFinanceiroEntity toEntity(PerfilFinanceiro domain) {
        PerfilFinanceiroEntity entity = new PerfilFinanceiroEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setTipo(domain.getTipo());
        entity.setUsuarioId(domain.getUsuarioId());
        return entity;
    }

    private PerfilFinanceiro toDomain(PerfilFinanceiroEntity entity) {
        PerfilFinanceiro domain = new PerfilFinanceiro();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setTipo(entity.getTipo());
        domain.setUsuarioId(entity.getUsuarioId());
        return domain;
    }
}
