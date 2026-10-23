package com.financheck.usecase.transacao;

import com.financheck.domain.entity.Transacao;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.domain.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarTransacaoUseCase {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PerfilFinanceiroRepository perfilFinanceiroRepository;

    public Transacao execute(Transacao transacao) {
        perfilFinanceiroRepository.buscarPorId(transacao.getPerfilId())
                .orElseThrow(() -> new RuntimeException("Perfil financeiro nao encontrado: " + transacao.getPerfilId()));
        categoriaRepository.buscarPorId(transacao.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + transacao.getCategoriaId()));
        return transacaoRepository.salvar(transacao);
    }
}
