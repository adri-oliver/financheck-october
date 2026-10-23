package com.financheck.usecase.transacao;

import com.financheck.domain.entity.Transacao;
import com.financheck.domain.entity.enums.TipoTransacao;
import com.financheck.domain.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CalcularSaldoUseCase - Testes Unitarios")
class CalcularSaldoUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private CalcularSaldoUseCase calcularSaldoUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve calcular saldo por perfil")
    void testCalcularSaldoPorPerfil() {
        // Arrange
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        Transacao receita = new Transacao(1L, TipoTransacao.RECEITA, new BigDecimal("1000.00"), inicio, "Salario", 1L, 5L);
        Transacao despesa = new Transacao(2L, TipoTransacao.DESPESA, new BigDecimal("250.50"), inicio, "Mercado", 1L, 5L);

        when(transacaoRepository.buscarPorPeriodo(5L, inicio, fim)).thenReturn(List.of(receita, despesa));

        // Act
        BigDecimal saldo = calcularSaldoUseCase.execute(5L, inicio, fim);

        // Assert
        assertEquals(new BigDecimal("749.50"), saldo);
        verify(transacaoRepository).buscarPorPeriodo(5L, inicio, fim);
    }
}
