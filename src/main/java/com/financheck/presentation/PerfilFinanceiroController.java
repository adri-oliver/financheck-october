package com.financheck.presentation;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.presentation.dto.PerfilFinanceiroDTO;
import com.financheck.usecase.perfil.CriarPerfilFinanceiroUseCase;
import com.financheck.usecase.perfil.ListarPerfisFinanceirosUseCase;
import com.financheck.usecase.perfil.RemoverPerfilFinanceiroUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfis")
@RequiredArgsConstructor
public class PerfilFinanceiroController {

    private final CriarPerfilFinanceiroUseCase criarPerfilFinanceiroUseCase;
    private final ListarPerfisFinanceirosUseCase listarPerfisFinanceirosUseCase;
    private final RemoverPerfilFinanceiroUseCase removerPerfilFinanceiroUseCase;

    @PostMapping
    public ResponseEntity<PerfilFinanceiro> criar(@Valid @RequestBody PerfilFinanceiroDTO dto) {
        PerfilFinanceiro perfil = new PerfilFinanceiro();
        perfil.setNome(dto.getNome());
        perfil.setTipo(dto.getTipo());
        perfil.setUsuarioId(dto.getUsuarioId());
        PerfilFinanceiro salvo = criarPerfilFinanceiroUseCase.execute(perfil);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PerfilFinanceiro>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(listarPerfisFinanceirosUseCase.execute(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long id,
            @RequestParam(required = false) Long perfilSolicitanteId
    ) {
        removerPerfilFinanceiroUseCase.execute(id, perfilSolicitanteId);
        return ResponseEntity.noContent().build();
    }
}
