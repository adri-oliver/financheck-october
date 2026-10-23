package com.financheck.usecase.limite;

import com.financheck.domain.entity.Categoria;
import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CalcularGastoLimiteUseCase - Testes Unitarios")
class CalcularGastoLimiteUseCaseTest {

    private static final LocalDate DATA_CADASTRO = LocalDate.of(2026, 10, 10);

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CalcularGastoLimiteUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private LimiteGasto limite(String valorLimite) {
        LimiteGasto l = new LimiteGasto();
        l.setId(1L);
        l.setNome("Delivery");
        l.setCategoriaId(7L);
        l.setPerfilId(3L);
        l.setValorLimite(new BigDecimal(valorLimite));
        l.setDataCadastro(DATA_CADASTRO);
        return l;
    }

    private void mockCategoria() {
        when(categoriaRepository.buscarPorId(7L))
                .thenReturn(Optional.of(new Categoria(7L, "Alimentação", "Alimentação", 1L)));
    }

    @Test
    @DisplayName("Deve buscar o gasto da categoria a partir da data de cadastro do limite")
    void deveUsarDataDeCadastroComoFiltro() {
        mockCategoria();
        when(transacaoRepository.somarDespesasPorCategoriaDesde(3L, 7L, DATA_CADASTRO))
                .thenReturn(new BigDecimal("250.00"));

        LimiteGastoResumo resumo = useCase.execute(limite("1000.00"));

        verify(transacaoRepository).somarDespesasPorCategoriaDesde(3L, 7L, DATA_CADASTRO);
        assertEquals("Alimentação", resumo.categoria());
        assertEquals(new BigDecimal("250.00"), resumo.valorGasto());
        assertEquals(new BigDecimal("750.00"), resumo.valorRestante());
        assertEquals(25.0, resumo.percentual(), 0.001);
        assertFalse(resumo.excedido());
    }

    @Test
    @DisplayName("Deve marcar como excedido quando o gasto passa do limite")
    void deveMarcarComoExcedido() {
        mockCategoria();
        when(transacaoRepository.somarDespesasPorCategoriaDesde(3L, 7L, DATA_CADASTRO))
                .thenReturn(new BigDecimal("1200.00"));

        LimiteGastoResumo resumo = useCase.execute(limite("1000.00"));

        assertTrue(resumo.excedido());
        assertEquals(new BigDecimal("-200.00"), resumo.valorRestante());
        assertEquals(120.0, resumo.percentual(), 0.001);
    }

    @Test
    @DisplayName("Gasto exatamente igual ao limite nao e considerado excedido")
    void gastoIgualAoLimiteNaoExcede() {
        mockCategoria();
        when(transacaoRepository.somarDespesasPorCategoriaDesde(3L, 7L, DATA_CADASTRO))
                .thenReturn(new BigDecimal("1000.00"));

        LimiteGastoResumo resumo = useCase.execute(limite("1000.00"));

        assertFalse(resumo.excedido());
        assertEquals(100.0, resumo.percentual(), 0.001);
    }

    @Test
    @DisplayName("Sem despesas na categoria o gasto deve ser zero")
    void semDespesasGastoZero() {
        mockCategoria();
        when(transacaoRepository.somarDespesasPorCategoriaDesde(3L, 7L, DATA_CADASTRO))
                .thenReturn(BigDecimal.ZERO);

        LimiteGastoResumo resumo = useCase.execute(limite("500.00"));

        assertEquals(0, resumo.valorGasto().compareTo(BigDecimal.ZERO));
        assertEquals(0.0, resumo.percentual(), 0.001);
        assertFalse(resumo.excedido());
    }
}
