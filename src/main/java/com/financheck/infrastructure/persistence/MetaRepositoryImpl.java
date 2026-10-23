package com.financheck.infrastructure.persistence;

import com.financheck.domain.entity.MetaFinanceira;
import com.financheck.domain.repository.MetaRepository;
import com.financheck.infrastructure.persistence.entity.MetaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

interface MetaJpaRepository extends JpaRepository<MetaEntity, Long> {
    List<MetaEntity> findByPerfilId(Long perfilId);
}

@Component
@RequiredArgsConstructor
public class MetaRepositoryImpl implements MetaRepository {

    private final MetaJpaRepository jpaRepository;

    @Override
    public MetaFinanceira salvar(MetaFinanceira meta) {
        MetaEntity entity = toEntity(meta);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<MetaFinanceira> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MetaFinanceira> buscarPorPerfil(Long perfilId) {
        return jpaRepository.findByPerfilId(perfilId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public MetaFinanceira atualizar(MetaFinanceira meta) {
        MetaEntity entity = toEntity(meta);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public void remover(Long id) {
        jpaRepository.deleteById(id);
    }

    private MetaEntity toEntity(MetaFinanceira domain) {
        MetaEntity entity = new MetaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setValorAlvo(domain.getValorAlvo());
        entity.setValorAcumulado(domain.getValorAcumulado());
        entity.setPrazo(domain.getPrazo());
        entity.setPerfilId(domain.getPerfilId());
        return entity;
    }

    private MetaFinanceira toDomain(MetaEntity entity) {
        MetaFinanceira domain = new MetaFinanceira();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setValorAlvo(entity.getValorAlvo());
        domain.setValorAcumulado(entity.getValorAcumulado());
        domain.setPrazo(entity.getPrazo());
        domain.setPerfilId(entity.getPerfilId());
        return domain;
    }
}
