package com.financheck.usecase.perfil;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("RemoverPerfilFinanceiroUseCase - Testes Unitarios")
class RemoverPerfilFinanceiroUseCaseTest {

    @Mock
    private PerfilFinanceiroRepository perfilFinanceiroRepository;

    @InjectMocks
    private RemoverPerfilFinanceiroUseCase removerPerfilFinanceiroUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve impedir perfil filho de excluir perfil responsavel")
    void testFilhoNaoPodeExcluirResponsavel() {
        // Arrange
        PerfilFinanceiro responsavel = new PerfilFinanceiro(1L, "Stephanie", TipoPerfilFinanceiro.RESPONSAVEL, 10L);
        PerfilFinanceiro filho = new PerfilFinanceiro(2L, "Joao", TipoPerfilFinanceiro.FILHO, 10L);
        when(perfilFinanceiroRepository.buscarPorId(1L)).thenReturn(Optional.of(responsavel));
        when(perfilFinanceiroRepository.buscarPorId(2L)).thenReturn(Optional.of(filho));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> removerPerfilFinanceiroUseCase.execute(1L, 2L));

        assertEquals("Perfil FILHO nao pode excluir perfil RESPONSAVEL", exception.getMessage());
        verify(perfilFinanceiroRepository, never()).remover(anyLong());
    }

    @Test
    @DisplayName("Deve remover perfil quando regra permitir")
    void testRemoverPerfilPermitido() {
        // Arrange
        PerfilFinanceiro familiar = new PerfilFinanceiro(3L, "Maria", TipoPerfilFinanceiro.FAMILIAR, 10L);
        PerfilFinanceiro responsavel = new PerfilFinanceiro(1L, "Stephanie", TipoPerfilFinanceiro.RESPONSAVEL, 10L);
        when(perfilFinanceiroRepository.buscarPorId(3L)).thenReturn(Optional.of(familiar));
        when(perfilFinanceiroRepository.buscarPorId(1L)).thenReturn(Optional.of(responsavel));

        // Act
        removerPerfilFinanceiroUseCase.execute(3L, 1L);

        // Assert
        verify(perfilFinanceiroRepository).remover(3L);
    }

    @Test
    @DisplayName("Deve lancar excecao quando perfil nao existir")
    void testPerfilNaoEncontrado() {
        // Arrange
        when(perfilFinanceiroRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> removerPerfilFinanceiroUseCase.execute(99L, null));

        assertEquals("Perfil financeiro nao encontrado: 99", exception.getMessage());
    }
}
