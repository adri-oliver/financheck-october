package com.financheck.presentation;

import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.presentation.dto.PerfilFinanceiroDTO;
import com.financheck.usecase.perfil.CriarPerfilFinanceiroUseCase;
import com.financheck.usecase.perfil.ListarPerfisFinanceirosUseCase;
import com.financheck.usecase.perfil.RemoverPerfilFinanceiroUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PerfilFinanceiroController - Testes Unitarios")
class PerfilFinanceiroControllerTest {

    @Mock
    private CriarPerfilFinanceiroUseCase criarPerfilFinanceiroUseCase;

    @Mock
    private ListarPerfisFinanceirosUseCase listarPerfisFinanceirosUseCase;

    @Mock
    private RemoverPerfilFinanceiroUseCase removerPerfilFinanceiroUseCase;

    @InjectMocks
    private PerfilFinanceiroController perfilFinanceiroController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar perfil e retornar 201")
    void testCriarPerfil() {
        // Arrange
        PerfilFinanceiroDTO dto = new PerfilFinanceiroDTO();
        dto.setNome("Joao");
        dto.setTipo(TipoPerfilFinanceiro.FILHO);
        dto.setUsuarioId(1L);

        PerfilFinanceiro salvo = new PerfilFinanceiro(2L, "Joao", TipoPerfilFinanceiro.FILHO, 1L);
        when(criarPerfilFinanceiroUseCase.execute(any(PerfilFinanceiro.class))).thenReturn(salvo);

        // Act
        ResponseEntity<PerfilFinanceiro> response = perfilFinanceiroController.criar(dto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        verify(criarPerfilFinanceiroUseCase).execute(argThat(perfil ->
                perfil.getNome().equals("Joao")
                        && perfil.getTipo() == TipoPerfilFinanceiro.FILHO
                        && perfil.getUsuarioId().equals(1L)
        ));
    }

    @Test
    @DisplayName("Deve listar perfis por usuario")
    void testListarPerfis() {
        // Arrange
        when(listarPerfisFinanceirosUseCase.execute(1L))
                .thenReturn(List.of(new PerfilFinanceiro(1L, "Stephanie", TipoPerfilFinanceiro.RESPONSAVEL, 1L)));

        // Act
        ResponseEntity<List<PerfilFinanceiro>> response = perfilFinanceiroController.listarPorUsuario(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(listarPerfisFinanceirosUseCase).execute(1L);
    }

    @Test
    @DisplayName("Deve remover perfil")
    void testRemoverPerfil() {
        // Act
        ResponseEntity<Void> response = perfilFinanceiroController.remover(3L, 1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(removerPerfilFinanceiroUseCase).execute(3L, 1L);
    }
}
