package com.financheck.usecase.perfil;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.Usuario;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CriarPerfilFinanceiroUseCase - Testes Unitarios")
class CriarPerfilFinanceiroUseCaseTest {

    @Mock
    private PerfilFinanceiroRepository perfilFinanceiroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CriarPerfilFinanceiroUseCase criarPerfilFinanceiroUseCase;

    private PerfilFinanceiro perfil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        perfil = new PerfilFinanceiro();
        perfil.setNome("Joao");
        perfil.setTipo(TipoPerfilFinanceiro.FILHO);
        perfil.setUsuarioId(1L);
    }

    @Test
    @DisplayName("Deve criar perfil quando usuario existir")
    void testCriarPerfilComUsuarioExistente() {
        // Arrange
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(perfilFinanceiroRepository.salvar(perfil)).thenReturn(perfil);

        // Act
        PerfilFinanceiro resultado = criarPerfilFinanceiroUseCase.execute(perfil);

        // Assert
        assertEquals("Joao", resultado.getNome());
        assertEquals(TipoPerfilFinanceiro.FILHO, resultado.getTipo());
        verify(usuarioRepository).buscarPorId(1L);
        verify(perfilFinanceiroRepository).salvar(perfil);
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario nao existir")
    void testUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> criarPerfilFinanceiroUseCase.execute(perfil));

        assertEquals("Usuario nao encontrado: 1", exception.getMessage());
        verify(perfilFinanceiroRepository, never()).salvar(any());
    }
}
