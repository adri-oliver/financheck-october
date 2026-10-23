package com.financheck.presentation;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.Usuario;
import com.financheck.presentation.dto.PerfilFinanceiroResponseDTO;
import com.financheck.presentation.dto.usuario.*;
import com.financheck.usecase.perfil.ListarPerfisFinanceirosUseCase;
import com.financheck.usecase.perfil.ResolverPerfilFinanceiroUseCase;
import com.financheck.usecase.usuario.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final CadastrarUsuarioUseCase cadastrarUsuarioUseCase;
    private final LoginUsuarioUseCase loginUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final DesativarUsuarioUseCase desativarUsuarioUseCase;
    private final ListarPerfisFinanceirosUseCase listarPerfisFinanceirosUseCase;
    private final ResolverPerfilFinanceiroUseCase resolverPerfilFinanceiroUseCase;

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return toResponseDTO(usuario, listarPerfisFinanceirosUseCase.execute(usuario.getId()));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario, java.util.List<PerfilFinanceiro> perfis) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.isAtivo(),
                Boolean.TRUE.equals(usuario.getControleFamiliar()),
                perfis.stream()
                        .map(perfil -> new PerfilFinanceiroResponseDTO(
                                perfil.getId(),
                                perfil.getNome(),
                                perfil.getTipo()
                        ))
                        .toList()
        );
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @RequestBody UsuarioCadastroDTO dto
    ) {

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setControleFamiliar(dto.isControleFamiliar());

        Usuario salvo = cadastrarUsuarioUseCase.execute(usuario);

        return ResponseEntity.status(201).body(toResponseDTO(salvo));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(
            @RequestBody UsuarioLoginDTO dto
    ) {

        Usuario usuario = loginUsuarioUseCase.execute(
                dto.getEmail(),
                dto.getSenha()
        );
        resolverPerfilFinanceiroUseCase.execute(null, usuario.getId());

        return ResponseEntity.ok(toResponseDTO(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioAtualizacaoDTO dto
    ) {

        Usuario usuario = atualizarUsuarioUseCase.execute(
                id,
                dto.getNome(),
                dto.getEmail()
        );

        return ResponseEntity.ok(toResponseDTO(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id
    ) {
        desativarUsuarioUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
