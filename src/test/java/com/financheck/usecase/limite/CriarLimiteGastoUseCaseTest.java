package com.financheck.usecase.limite;

import com.financheck.domain.entity.Categoria;
import com.financheck.domain.entity.LimiteGasto;
import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.repository.CategoriaRepository;
import com.financheck.domain.repository.LimiteGastoRepository;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
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

@DisplayName("CriarLimiteGastoUseCase - Testes Unitarios")
class CriarLimiteGastoUseCaseTest {

    @Mock
    private LimiteGastoRepository limiteGastoRepository;

    @Mock
    private PerfilFinanceiroRepository perfilFinanceiroRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CriarLimiteGastoUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private LimiteGasto novoLimite(String valor) {
        LimiteGasto l = new LimiteGasto();
        l.setNome("Delivery");
        l.setCategoriaId(7L);
        l.setPerfilId(3L);
        l.setValorLimite(valor == null ? null : new BigDecimal(valor));
        return l;
    }

    @Test
    @DisplayName("Deve definir a data de cadastro como hoje ao criar")
    void deveDefinirDataDeCadastro() {
        when(perfilFinanceiroRepository.buscarPorId(3L)).thenReturn(Optional.of(new PerfilFinanceiro()));
        when(categoriaRepository.buscarPorId(7L)).thenReturn(Optional.of(new Categoria()));
        when(limiteGastoRepository.salvar(any(LimiteGasto.class))).thenAnswer(i -> i.getArgument(0));

        LimiteGasto salvo = useCase.execute(novoLimite("300.00"));

        assertEquals(LocalDate.now(), salvo.getDataCadastro());
        verify(limiteGastoRepository).salvar(salvo);
    }

    @Test
    @DisplayName("Deve rejeitar valor de limite zero ou negativo")
    void deveRejeitarValorInvalido() {
        when(perfilFinanceiroRepository.buscarPorId(3L)).thenReturn(Optional.of(new PerfilFinanceiro()));
        when(categoriaRepository.buscarPorId(7L)).thenReturn(Optional.of(new Categoria()));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(novoLimite("0")));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(novoLimite("-10")));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(novoLimite(null)));
        verify(limiteGastoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar quando a categoria nao existe")
    void deveFalharSemCategoria() {
        when(perfilFinanceiroRepository.buscarPorId(3L)).thenReturn(Optional.of(new PerfilFinanceiro()));
        when(categoriaRepository.buscarPorId(7L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> useCase.execute(novoLimite("100.00")));
        verify(limiteGastoRepository, never()).salvar(any());
    }
}
