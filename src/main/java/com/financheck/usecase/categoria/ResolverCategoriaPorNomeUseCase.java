package com.financheck.usecase.categoria;

import com.financheck.domain.entity.Categoria;
import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolve o id da categoria a partir do id informado ou do nome (case-insensitive) entre as categorias
 * do usuário dono do perfil. Se o nome ainda não existir, a categoria é criada - mesmo comportamento
 * usado ao registrar transações, garantindo que limite e transações apontem para a mesma categoria.
 */
@Component
@RequiredArgsConstructor
public class ResolverCategoriaPorNomeUseCase {

    private final CategoriaRepository categoriaRepository;
    private final PerfilFinanceiroRepository perfilFinanceiroRepository;

    public Long execute(Long categoriaId, String nomeCategoria, Long perfilId) {
        if (categoriaId != null) return categoriaId;

        if (nomeCategoria == null || nomeCategoria.isBlank()) {
            throw new IllegalArgumentException("Campo 'categoriaId' ou 'categoria' é obrigatório");
        }
        String nome = nomeCategoria.trim();

        Long usuarioId = perfilFinanceiroRepository.buscarPorId(perfilId)
                .map(PerfilFinanceiro::getUsuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + perfilId));

        return categoriaRepository.buscarPorUsuario(usuarioId).stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .map(Categoria::getId)
                .findFirst()
                .orElseGet(() -> {
                    Categoria nova = new Categoria();
                    nova.setNome(nome);
                    nova.setDescricao(nome);
                    nova.setUsuarioId(usuarioId);
                    return categoriaRepository.salvar(nova).getId();
                });
    }
}
