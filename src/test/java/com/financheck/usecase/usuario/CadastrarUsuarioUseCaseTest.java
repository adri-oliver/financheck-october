package com.financheck.usecase.usuario;

import com.financheck.domain.entity.Usuario;
import com.financheck.domain.entity.PerfilFinanceiro;
import com.financheck.domain.entity.enums.TipoPerfilFinanceiro;
import com.financheck.domain.repository.PerfilFinanceiroRepository;
import com.financheck.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("CadastrarUsuarioUseCase - Testes Unitários")
class CadastrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilFinanceiroRepository perfilFinanceiroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CadastrarUsuarioUseCase cadastrarUsuarioUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setSenha("SenhaForte123!");
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso quando dados são válidos")
    void testCadastrarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuario.getSenha())).thenReturn("$2a$10$encryptedPassword");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@example.com", resultado.getEmail());
        assertTrue(resultado.isAtivo());
        verify(usuarioRepository, times(1)).buscarPorEmail("joao@example.com");
        verify(passwordEncoder, times(1)).encode("SenhaForte123!");
        verify(usuarioRepository, times(1)).salvar(any(Usuario.class));
        verify(perfilFinanceiroRepository).salvar(argThat(perfil ->
                perfil.getNome().equals("JoÃ£o Silva")
                        || perfil.getTipo() == TipoPerfilFinanceiro.RESPONSAVEL
        ));
    }

    @Test
    @DisplayName("Deve criptografar senha antes de salvar")
    void testCriptografiaSenhaAntesDeSalvar() {
        // Arrange
        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SenhaForte123!")).thenReturn("$2a$10$encryptedHash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuario);

        // Act
        cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        verify(passwordEncoder).encode("SenhaForte123!");
    }

    @Test
    @DisplayName("Deve ativar usuário automaticamente")
    void testUsuarioAtivoAposCadastro() {
        // Arrange
        usuario.setAtivo(false);
        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            return u;
        });

        // Act
        Usuario resultado = cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        assertTrue(resultado.isAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está cadastrado")
    void testLancarExcecaoQuandoEmailDuplicado() {
        // Arrange
        when(usuarioRepository.buscarPorEmail("joao@example.com")).thenReturn(Optional.of(usuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cadastrarUsuarioUseCase.execute(usuario);
        });

        assertEquals("E-mail já cadastrado", exception.getMessage());
        verify(usuarioRepository, times(1)).buscarPorEmail("joao@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve salvar usuário com senha criptografada")
    void testSenhaArmazenadaCriptografada() {
        // Arrange
        Usuario usuarioArmazenado = new Usuario();
        usuarioArmazenado.setId(1L);
        usuarioArmazenado.setNome("João Silva");
        usuarioArmazenado.setEmail("joao@example.com");
        usuarioArmazenado.setSenha("$2a$10$encryptedPassword");
        usuarioArmazenado.setAtivo(true);

        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SenhaForte123!")).thenReturn("$2a$10$encryptedPassword");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuarioArmazenado);

        // Act
        Usuario resultado = cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        assertEquals("$2a$10$encryptedPassword", resultado.getSenha());
    }

    @Test
    @DisplayName("Deve validar múltiplos usuários com emails diferentes")
    void testCadastrarMultiplosUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Maria Santos");
        usuario2.setEmail("maria@example.com");
        usuario2.setSenha("OutraSenha456!");

        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorEmail(usuario2.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado1 = cadastrarUsuarioUseCase.execute(usuario);
        Usuario resultado2 = cadastrarUsuarioUseCase.execute(usuario2);

        // Assert
        assertNotNull(resultado1);
        assertNotNull(resultado2);
        verify(usuarioRepository, times(2)).buscarPorEmail(anyString());
    }

    @Test
    @DisplayName("Deve preservar dados do usuário original (exceto senha)")
    void testPreservarDadosUsuario() {
        // Arrange
        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@example.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve criar perfil responsavel quando controle familiar estiver ativo")
    void testCriarPerfilResponsavelComControleFamiliar() {
        // Arrange
        usuario.setControleFamiliar(true);

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(10L);
        usuarioSalvo.setNome("Joao Silva");
        usuarioSalvo.setEmail("joao@example.com");
        usuarioSalvo.setSenha("$2a$10$hash");
        usuarioSalvo.setAtivo(true);
        usuarioSalvo.setControleFamiliar(true);

        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuario.getSenha())).thenReturn("$2a$10$hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

        // Act
        Usuario resultado = cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        assertTrue(resultado.getControleFamiliar());
        verify(perfilFinanceiroRepository).salvar(argThat(perfil ->
                perfil.getUsuarioId().equals(10L)
                        && perfil.getNome().equals("Joao Silva")
                        && perfil.getTipo() == TipoPerfilFinanceiro.RESPONSAVEL
        ));
    }

    @Test
    @DisplayName("Deve criar perfil responsavel mesmo sem controle familiar")
    void testCriarPerfilResponsavelSemControleFamiliar() {
        // Arrange
        usuario.setControleFamiliar(false);

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(11L);
        usuarioSalvo.setNome("Maria Santos");
        usuarioSalvo.setEmail("maria@example.com");
        usuarioSalvo.setSenha("$2a$10$hash");
        usuarioSalvo.setAtivo(true);
        usuarioSalvo.setControleFamiliar(false);

        when(usuarioRepository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuario.getSenha())).thenReturn("$2a$10$hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

        // Act
        Usuario resultado = cadastrarUsuarioUseCase.execute(usuario);

        // Assert
        assertFalse(resultado.getControleFamiliar());
        verify(perfilFinanceiroRepository).salvar(argThat(perfil ->
                perfil.getUsuarioId().equals(11L)
                        && perfil.getNome().equals("Maria Santos")
                        && perfil.getTipo() == TipoPerfilFinanceiro.RESPONSAVEL
        ));
    }
}
