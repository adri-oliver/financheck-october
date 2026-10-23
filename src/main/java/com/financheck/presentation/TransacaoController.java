package com.financheck.presentation;

import com.financheck.domain.entity.Categoria;
import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.Transacao;
import com.financheck.domain.entity.enums.TipoTransacao;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.presentation.dto.TransacaoDTO;
import com.financheck.usecase.perfil.ResolverPerfilFinanceiroUseCase;
import com.financheck.usecase.transacao.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final RegistrarTransacaoUseCase registrarTransacaoUseCase;
    private final AtualizarTransacaoUseCase atualizarTransacaoUseCase;
    private final RemoverTransacaoUseCase removerTransacaoUseCase;
    private final BuscarTransacoesPorPeriodoUseCase buscarTransacoesPorPeriodoUseCase;
    private final CalcularSaldoUseCase calcularSaldoUseCase;
    private final ResolverPerfilFinanceiroUseCase resolverPerfilFinanceiroUseCase;
    private final CategoriaRepository categoriaRepository;
    private final PerfilFinanceiroRepository perfilFinanceiroRepository;

    @PostMapping
    public ResponseEntity<Transacao> registrar(@RequestBody TransacaoDTO dto) {
        Long perfilResolvidoId = resolverPerfilFinanceiroUseCase.execute(dto.getPerfilId(), dto.getUsuarioId());

        Transacao transacao = new Transacao();
        transacao.setValor(resolverValorAbsoluto(dto));
        transacao.setTipo(resolverTipo(dto));
        transacao.setData(dto.getData() != null ? dto.getData() : LocalDate.now());
        transacao.setDescricao(resolverDescricao(dto));
        transacao.setCategoriaId(resolverOuCriarCategoria(dto, perfilResolvidoId));
        transacao.setPerfilId(perfilResolvidoId);

        return ResponseEntity.status(HttpStatus.CREATED).body(registrarTransacaoUseCase.execute(transacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizar(@PathVariable Long id, @RequestBody TransacaoDTO dto) {
        Long perfilResolvidoId = resolverPerfilFinanceiroUseCase.execute(dto.getPerfilId(), dto.getUsuarioId());

        Transacao dados = new Transacao();
        dados.setValor(resolverValorAbsoluto(dto));
        dados.setTipo(resolverTipo(dto));
        dados.setData(dto.getData() != null ? dto.getData() : LocalDate.now());
        dados.setDescricao(resolverDescricao(dto));
        dados.setCategoriaId(resolverOuCriarCategoria(dto, perfilResolvidoId));
        dados.setPerfilId(perfilResolvidoId);

        return ResponseEntity.ok(atualizarTransacaoUseCase.execute(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        removerTransacaoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> buscarPorPeriodo(
            @RequestParam(required = false) Long perfilId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        Long perfilResolvidoId = resolverPerfilFinanceiroUseCase.execute(perfilId, usuarioId);
        LocalDate dataInicio = inicio != null ? inicio : LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate dataFim    = fim   != null ? fim   : LocalDate.of(LocalDate.now().getYear(), 12, 31);
        return ResponseEntity.ok(buscarTransacoesPorPeriodoUseCase.execute(perfilResolvidoId, dataInicio, dataFim));
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> saldo(
            @RequestParam(required = false) Long perfilId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        Long perfilResolvidoId = resolverPerfilFinanceiroUseCase.execute(perfilId, usuarioId);
        LocalDate dataInicio = inicio != null ? inicio : LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate dataFim    = fim   != null ? fim   : LocalDate.of(LocalDate.now().getYear(), 12, 31);
        return ResponseEntity.ok(calcularSaldoUseCase.execute(perfilResolvidoId, dataInicio, dataFim));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Valor sempre positivo no banco; o tipo carrega a semântica de débito/crédito. */
    private BigDecimal resolverValorAbsoluto(TransacaoDTO dto) {
        BigDecimal v = dto.getValor() != null ? dto.getValor() : dto.getAmount();
        if (v == null) throw new IllegalArgumentException("Campo 'valor' ou 'amount' é obrigatório");
        return v.abs();
    }

    /** Se o tipo não vier explícito, infere pelo sinal do valor original. */
    private TipoTransacao resolverTipo(TransacaoDTO dto) {
        if (dto.getTipo() != null) return dto.getTipo();
        BigDecimal v = dto.getValor() != null ? dto.getValor() : dto.getAmount();
        return (v != null && v.compareTo(BigDecimal.ZERO) < 0) ? TipoTransacao.DESPESA : TipoTransacao.RECEITA;
    }

    private String resolverDescricao(TransacaoDTO dto) {
        if (dto.getDescricao() != null) return dto.getDescricao();
        if (dto.getNome()     != null) return dto.getNome();
        return dto.getName();
    }

    /**
     * Resolve categoriaId pelo nome.
     * - Se veio categoriaId direto, usa ele.
     * - Caso contrário, busca pelo nome entre as categorias do usuário dono do perfil.
     * - Se não existir, cria a categoria automaticamente (evita erro 500).
     */
    private Long resolverOuCriarCategoria(TransacaoDTO dto, Long perfilResolvidoId) {
        if (dto.getCategoriaId() != null) return dto.getCategoriaId();

        String nomeCategoria = dto.getCategoria();
        if (nomeCategoria == null || nomeCategoria.isBlank()) {
            throw new IllegalArgumentException("Campo 'categoriaId' ou 'categoria' é obrigatório");
        }

        // Obtém o usuarioId a partir do perfil
        Long usuarioId = perfilFinanceiroRepository.buscarPorId(perfilResolvidoId)
                .map(com.financheck.domain.entity.PerfilFinanceiro::getUsuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + perfilResolvidoId));

        // Procura categoria existente pelo nome (case-insensitive)
        return categoriaRepository.buscarPorUsuario(usuarioId).stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nomeCategoria))
                .map(Categoria::getId)
                .findFirst()
                .orElseGet(() -> {
                    // Cria a categoria automaticamente se não existir
                    Categoria nova = new Categoria();
                    nova.setNome(nomeCategoria);
                    nova.setDescricao(nomeCategoria);
                    nova.setUsuarioId(usuarioId);
                    return categoriaRepository.salvar(nova).getId();
                });
    }
}