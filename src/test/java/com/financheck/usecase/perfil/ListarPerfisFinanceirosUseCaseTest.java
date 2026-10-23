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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ListarPerfisFinanceirosUseCase - Testes Unitarios")
class ListarPerfisFinanceirosUseCaseTest {

    @Mock
    private PerfilFinanceiroRepository perfilFinanceiroRepository;

    @InjectMocks
    private ListarPerfisFinanceirosUseCase listarPerfisFinanceirosUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve listar perfis por usuario")
    void testListarPerfisPorUsuario() {
        // Arrange
        PerfilFinanceiro responsavel = new PerfilFinanceiro(1L, "Stephanie", TipoPerfilFinanceiro.RESPONSAVEL, 10L);
        PerfilFinanceiro filho = new PerfilFinanceiro(2L, "Joao", TipoPerfilFinanceiro.FILHO, 10L);
        when(perfilFinanceiroRepository.buscarPorUsuario(10L)).thenReturn(List.of(responsavel, filho));

        // Act
        List<PerfilFinanceiro> resultado = listarPerfisFinanceirosUseCase.execute(10L);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Stephanie", resultado.get(0).getNome());
        assertEquals("Joao", resultado.get(1).getNome());
        verify(perfilFinanceiroRepository).buscarPorUsuario(10L);
    }
}
