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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ResolverPerfilFinanceiroUseCase - Testes Unitarios")
class ResolverPerfilFinanceiroUseCaseTest {

    @Mock
    private PerfilFinanceiroRepository perfilFinanceiroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ResolverPerfilFinanceiroUseCase resolverPerfilFinanceiroUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve retornar perfil informado quando existir")
    void testResolverPerfilInformado() {
        // Arrange
        PerfilFinanceiro perfil = new PerfilFinanceiro(5L, "Maria", TipoPerfilFinanceiro.FAMILIAR, 1L);
        when(perfilFinanceiroRepository.buscarPorId(5L)).thenReturn(Optional.of(perfil));

        // Act
        Long resultado = resolverPerfilFinanceiroUseCase.execute(5L, null);

        // Assert
        assertEquals(5L, resultado);
        verify(perfilFinanceiroRepository).buscarPorId(5L);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve usar perfil responsavel do usuario em chamadas legadas")
    void testResolverPerfilResponsavelPorUsuario() {
        // Arrange
        PerfilFinanceiro responsavel = new PerfilFinanceiro(7L, "Stephanie", TipoPerfilFinanceiro.RESPONSAVEL, 2L);
        when(perfilFinanceiroRepository.buscarResponsavelPorUsuario(2L)).thenReturn(Optional.of(responsavel));

        // Act
        Long resultado = resolverPerfilFinanceiroUseCase.execute(null, 2L);

        // Assert
        assertEquals(7L, resultado);
        verify(perfilFinanceiroRepository).buscarResponsavelPorUsuario(2L);
    }

    @Test
    @DisplayName("Deve criar perfil responsavel para usuario antigo sem perfis")
    void testCriarPerfilParaUsuarioAntigo() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setNome("Stephanie");

        PerfilFinanceiro perfilCriado = new PerfilFinanceiro(9L, "Stephanie", TipoPerfilFinanceiro.RESPONSAVEL, 3L);

        when(perfilFinanceiroRepository.buscarResponsavelPorUsuario(3L)).thenReturn(Optional.empty());
        when(perfilFinanceiroRepository.buscarPorUsuario(3L)).thenReturn(List.of());
        when(usuarioRepository.buscarPorId(3L)).thenReturn(Optional.of(usuario));
        when(perfilFinanceiroRepository.salvar(any(PerfilFinanceiro.class))).thenReturn(perfilCriado);

        // Act
        Long resultado = resolverPerfilFinanceiroUseCase.execute(null, 3L);

        // Assert
        assertEquals(9L, resultado);
        verify(perfilFinanceiroRepository).salvar(argThat(perfil ->
                perfil.getNome().equals("Stephanie")
                        && perfil.getTipo() == TipoPerfilFinanceiro.RESPONSAVEL
                        && perfil.getUsuarioId().equals(3L)
        ));
    }

    @Test
    @DisplayName("Deve exigir perfilId ou usuarioId")
    void testExigirIdentificador() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> resolverPerfilFinanceiroUseCase.execute(null, null));

        assertEquals("Informe perfilId ou usuarioId", exception.getMessage());
    }
}
