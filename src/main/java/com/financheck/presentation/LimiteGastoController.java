package com.financheck.presentation;

import com.financheck.domain.entity.LimiteGasto;
import com.financheck.presentation.dto.LimiteGastoDTO;
import com.financheck.usecase.categoria.ResolverCategoriaPorNomeUseCase;
import com.financheck.usecase.limite.AtualizarLimiteGastoUseCase;
import com.financheck.usecase.limite.CalcularGastoLimiteUseCase;
import com.financheck.usecase.limite.CriarLimiteGastoUseCase;
import com.financheck.usecase.limite.LimiteGastoResumo;
import com.financheck.usecase.limite.ListarLimitesGastoUseCase;
import com.financheck.usecase.limite.RemoverLimiteGastoUseCase;
import com.financheck.usecase.perfil.ResolverPerfilFinanceiroUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/limites-gasto")
@RequiredArgsConstructor
public class LimiteGastoController {

    private final CriarLimiteGastoUseCase criarLimiteGastoUseCase;
    private final AtualizarLimiteGastoUseCase atualizarLimiteGastoUseCase;
    private final RemoverLimiteGastoUseCase removerLimiteGastoUseCase;
    private final ListarLimitesGastoUseCase listarLimitesGastoUseCase;
    private final CalcularGastoLimiteUseCase calcularGastoLimiteUseCase;
    private final ResolverPerfilFinanceiroUseCase resolverPerfilFinanceiroUseCase;
    private final ResolverCategoriaPorNomeUseCase resolverCategoriaPorNomeUseCase;

    @PostMapping
    public ResponseEntity<LimiteGastoResumo> criar(@RequestBody LimiteGastoDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Campo 'nome' é obrigatório");
        }
        if (dto.getValorLimite() == null) {
            throw new IllegalArgumentException("Campo 'valorLimite' é obrigatório");
        }

        Long perfilId = resolverPerfilFinanceiroUseCase.execute(dto.getPerfilId(), dto.getUsuarioId());

        LimiteGasto limite = new LimiteGasto();
        limite.setNome(dto.getNome().trim());
        limite.setValorLimite(dto.getValorLimite());
        limite.setCategoriaId(resolverCategoriaPorNomeUseCase.execute(dto.getCategoriaId(), dto.getCategoria(), perfilId));
        limite.setPerfilId(perfilId);

        LimiteGasto criado = criarLimiteGastoUseCase.execute(limite);
        return ResponseEntity.status(HttpStatus.CREATED).body(calcularGastoLimiteUseCase.execute(criado));
    }

    @GetMapping
    public ResponseEntity<List<LimiteGastoResumo>> listar(
            @RequestParam(required = false) Long perfilId,
            @RequestParam(required = false) Long usuarioId) {
        Long perfilResolvidoId = resolverPerfilFinanceiroUseCase.execute(perfilId, usuarioId);
        return ResponseEntity.ok(listarLimitesGastoUseCase.execute(perfilResolvidoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LimiteGastoResumo> atualizar(@PathVariable Long id, @RequestBody LimiteGastoDTO dto) {
        LimiteGasto atualizado = atualizarLimiteGastoUseCase.execute(
                id, dto.getNome(), dto.getCategoriaId(), dto.getCategoria(), dto.getValorLimite());
        return ResponseEntity.ok(calcularGastoLimiteUseCase.execute(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        removerLimiteGastoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
