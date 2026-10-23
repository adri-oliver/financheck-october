package com.financheck.usecase.usuario;

import com.financheck.domain.entity.Usuario;
import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PerfilFinanceiroRepository perfilFinanceiroRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario execute(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivo(true);
        Usuario salvo = usuarioRepository.salvar(usuario);

        PerfilFinanceiro perfilResponsavel = new PerfilFinanceiro();
        perfilResponsavel.setNome(salvo.getNome());
        perfilResponsavel.setTipo(TipoPerfilFinanceiro.RESPONSAVEL);
        perfilResponsavel.setUsuarioId(salvo.getId());
        perfilFinanceiroRepository.salvar(perfilResponsavel);

        return salvo;
    }
}
