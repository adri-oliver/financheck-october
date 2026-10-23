package com.financheck.infrastructure.persistence;

import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.repository.LimiteGastoRepository;
import com.financheck.infrastructure.persistence.entity.LimiteGastoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

interface LimiteGastoJpaRepository extends JpaRepository<LimiteGastoEntity, Long> {
    List<LimiteGastoEntity> findByPerfilId(Long perfilId);
}

@Component
@RequiredArgsConstructor
public class LimiteGastoRepositoryImpl implements LimiteGastoRepository {

    private final LimiteGastoJpaRepository jpaRepository;

    @Override
    public LimiteGasto salvar(LimiteGasto limite) {
        return toDomain(jpaRepository.save(toEntity(limite)));
    }

    @Override
    public Optional<LimiteGasto> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<LimiteGasto> buscarPorPerfil(Long perfilId) {
        return jpaRepository.findByPerfilId(perfilId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public LimiteGasto atualizar(LimiteGasto limite) {
        return toDomain(jpaRepository.save(toEntity(limite)));
    }

    @Override
    public void remover(Long id) {
        jpaRepository.deleteById(id);
    }

    private LimiteGastoEntity toEntity(LimiteGasto domain) {
        LimiteGastoEntity entity = new LimiteGastoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setCategoriaId(domain.getCategoriaId());
        entity.setValorLimite(domain.getValorLimite());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setPerfilId(domain.getPerfilId());
        return entity;
    }

    private LimiteGasto toDomain(LimiteGastoEntity entity) {
        LimiteGasto domain = new LimiteGasto();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setCategoriaId(entity.getCategoriaId());
        domain.setValorLimite(entity.getValorLimite());
        domain.setDataCadastro(entity.getDataCadastro());
        domain.setPerfilId(entity.getPerfilId());
        return domain;
    }
}
