package com.financheck.usecase.limite;

import com.financheck.domain.entity.Categoria;
import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.LimiteGastoRepository;
import com.financheck.usecase.categoria.ResolverCategoriaPorNomeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AtualizarLimiteGastoUseCase - Testes Unitarios")
class AtualizarLimiteGastoUseCaseTest {

    private static final LocalDate DATA_ANTIGA = LocalDate.of(2026, 1, 5);

    @Mock
    private LimiteGastoRepository limiteGastoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ResolverCategoriaPorNomeUseCase resolverCategoriaPorNomeUseCase;

    @InjectMocks
    private AtualizarLimiteGastoUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(limiteGastoRepository.buscarPorId(1L)).thenReturn(Optional.of(limiteExistente()));
        when(limiteGastoRepository.atualizar(any(LimiteGasto.class))).thenAnswer(i -> i.getArgument(0));
    }

    private LimiteGasto limiteExistente() {
        LimiteGasto l = new LimiteGasto();
        l.setId(1L);
        l.setNome("Delivery");
        l.setCategoriaId(7L);
        l.setPerfilId(3L);
        l.setValorLimite(new BigDecimal("300.00"));
        l.setDataCadastro(DATA_ANTIGA);
        return l;
    }

    @Test
    @DisplayName("Deve atualizar nome e valor mantendo categoria e data de cadastro")
    void deveAtualizarNomeEValor() {
        LimiteGasto r = useCase.execute(1L, "Novo nome", null, null, new BigDecimal("500.00"));

        assertEquals("Novo nome", r.getNome());
        assertEquals(new BigDecimal("500.00"), r.getValorLimite());
        assertEquals(7L, r.getCategoriaId());
        assertEquals(DATA_ANTIGA, r.getDataCadastro());
        verify(resolverCategoriaPorNomeUseCase, never()).execute(any(), any(), any());
    }

    @Test
    @DisplayName("Ao trocar a categoria, deve reiniciar a data de cadastro para hoje")
    void trocarCategoriaReiniciaData() {
        when(resolverCategoriaPorNomeUseCase.execute(null, "Lazer", 3L)).thenReturn(9L);
        when(categoriaRepository.buscarPorId(9L)).thenReturn(Optional.of(new Categoria()));

        LimiteGasto r = useCase.execute(1L, null, null, "Lazer", null);

        assertEquals(9L, r.getCategoriaId());
        assertEquals(LocalDate.now(), r.getDataCadastro());
        assertEquals("Delivery", r.getNome());
    }

    @Test
    @DisplayName("Informar a mesma categoria nao deve alterar a data de cadastro")
    void mesmaCategoriaMantemData() {
        when(resolverCategoriaPorNomeUseCase.execute(null, "Alimentação", 3L)).thenReturn(7L);
        when(categoriaRepository.buscarPorId(7L)).thenReturn(Optional.of(new Categoria()));

        LimiteGasto r = useCase.execute(1L, null, null, "Alimentação", null);

        assertEquals(7L, r.getCategoriaId());
        assertEquals(DATA_ANTIGA, r.getDataCadastro());
    }

    @Test
    @DisplayName("Deve rejeitar valor zero ou negativo")
    void deveRejeitarValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, null, null, null, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, null, null, null, new BigDecimal("-5")));
        verify(limiteGastoRepository, never()).atualizar(any());
    }
}
