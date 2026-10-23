package com.financheck.usecase.perfil;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.Usuario;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResolverPerfilFinanceiroUseCase {

    private final PerfilFinanceiroRepository perfilFinanceiroRepository;
    private final UsuarioRepository usuarioRepository;

    public Long execute(Long perfilId, Long usuarioId) {
        if (perfilId != null) {
            perfilFinanceiroRepository.buscarPorId(perfilId)
                    .orElseThrow(() -> new RuntimeException("Perfil financeiro nao encontrado: " + perfilId));
            return perfilId;
        }

        if (usuarioId == null) {
            throw new RuntimeException("Informe perfilId ou usuarioId");
        }

        return perfilFinanceiroRepository.buscarResponsavelPorUsuario(usuarioId)
                .or(() -> perfilFinanceiroRepository.buscarPorUsuario(usuarioId).stream().findFirst())
                .orElseGet(() -> criarPerfilResponsavel(usuarioId))
                .getId();
    }

    private PerfilFinanceiro criarPerfilResponsavel(Long usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + usuarioId));

        PerfilFinanceiro perfil = new PerfilFinanceiro();
        perfil.setNome(usuario.getNome());
        perfil.setTipo(TipoPerfilFinanceiro.RESPONSAVEL);
        perfil.setUsuarioId(usuarioId);
        return perfilFinanceiroRepository.salvar(perfil);
    }
}
