package com.financheck.presentation;

import com.financheck.domain.entity.MetaFinanceira;
import com.financheck.presentation.dto.MetaDTO;
import com.financheck.usecase.perfil.ResolverPerfilFinanceiroUseCase;
import com.financheck.usecase.meta.AtualizarMetaUseCase;
import com.financheck.usecase.meta.CalcularProgressoMetaUseCase;
import com.financheck.usecase.meta.CriarMetaUseCase;
import com.financheck.usecase.meta.ListarMetasUseCase;
import com.financheck.usecase.meta.RemoverMetaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetaController {

    private final CriarMetaUseCase criarMetaUseCase;
    private final AtualizarMetaUseCase atualizarMetaUseCase;
    private final RemoverMetaUseCase removerMetaUseCase;
    private final ListarMetasUseCase listarMetasUseCase;
    private final CalcularProgressoMetaUseCase calcularProgressoMetaUseCase;
    private final ResolverPerfilFinanceiroUseCase resolverPerfilFinanceiroUseCase;

    @PostMapping
    public ResponseEntity<MetaFinanceira> criar(@RequestBody MetaDTO dto) {
        MetaFinanceira meta = new MetaFinanceira();
        meta.setNome(resolverNome(dto));
        meta.setValorAlvo(resolverValorAlvo(dto));
        meta.setValorAcumulado(resolverValorAcumulado(dto)); // ← valor inicial depositado
        meta.setPrazo(dto.getPrazo() != null ? dto.getPrazo()
                : LocalDate.of(LocalDate.now().getYear(), 12, 31));
        meta.setPerfilId(resolverPerfilFinanceiroUseCase.execute(dto.getPerfilId(), dto.getUsuarioId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(criarMetaUseCase.execute(meta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaFinanceira> atualizar(@PathVariable Long id, @RequestBody MetaDTO dto) {
        MetaFinanceira dados = new MetaFinanceira();
        dados.setNome(resolverNome(dto));
        dados.setValorAlvo(resolverValorAlvo(dto));
        dados.setValorAcumulado(resolverValorAcumulado(dto));
        dados.setPrazo(dto.getPrazo() != null ? dto.getPrazo()
                : LocalDate.of(LocalDate.now().getYear(), 12, 31));
        dados.setPerfilId(resolverPerfilFinanceiroUseCase.execute(dto.getPerfilId(), dto.getUsuarioId()));
        return ResponseEntity.ok(atualizarMetaUseCase.execute(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        removerMetaUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MetaFinanceira>> listar(
            @RequestParam(required = false) Long perfilId,
            @RequestParam(required = false) Long usuarioId) {
        Long perfilResolvidoId = resolverPerfilFinanceiroUseCase.execute(perfilId, usuarioId);
        return ResponseEntity.ok(listarMetasUseCase.execute(perfilResolvidoId));
    }

    @GetMapping("/{id}/progresso")
    public ResponseEntity<Double> calcularProgresso(@PathVariable Long id) {
        return ResponseEntity.ok(calcularProgressoMetaUseCase.execute(id));
    }

    @PatchMapping("/{id}/adicionar")
    public ResponseEntity<MetaFinanceira> adicionarValor(
            @PathVariable Long id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(atualizarMetaUseCase.adicionarValor(id, valor));
    }

    @PatchMapping("/{id}/remover")
    public ResponseEntity<MetaFinanceira> removerValor(
            @PathVariable Long id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(atualizarMetaUseCase.removerValor(id, valor));
    }

    private String resolverNome(MetaDTO dto) {
        if (dto.getNome() != null) return dto.getNome();
        if (dto.getName() != null) return dto.getName();
        throw new IllegalArgumentException("Campo 'nome' ou 'name' é obrigatório");
    }

    private BigDecimal resolverValorAlvo(MetaDTO dto) {
        if (dto.getValorAlvo() != null) return dto.getValorAlvo();
        if (dto.getTarget()    != null) return dto.getTarget();
        throw new IllegalArgumentException("Campo 'valorAlvo' ou 'target' é obrigatório");
    }

    private BigDecimal resolverValorAcumulado(MetaDTO dto) {
        if (dto.getValorAcumulado() != null) return dto.getValorAcumulado();
        if (dto.getCurrent()        != null) return dto.getCurrent();
        return BigDecimal.ZERO;
    }
}