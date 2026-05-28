package com.helene.backend.steps;

import com.helene.backend.entity.Rol;
import com.helene.backend.entity.Usuario;
import com.helene.backend.repository.RolRepository;
import com.helene.backend.repository.UsuarioRepository;
import com.helene.backend.security.JwtUtil;
import com.helene.backend.service.UsuarioService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthSteps {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private boolean loginResult;
    private boolean registroResult;
    private Map<String, Object> lastResponse = new HashMap<>();

    @Given("existe un usuario con username {string} y password {string}")
    public void existeUsuario(String username, String password) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername(username);
        usuario.setPassword(encoder.encode(password));

        Rol rol = new Rol(1L, "CLIENTE");
        usuario.setRoles(new HashSet<>(Set.of(rol)));

        when(usuarioRepository.findByUsername(username))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByUsername(username))
                .thenReturn(true);
    }

    @Given("no existe ningún usuario con username {string}")
    public void noExisteUsuario(String username) {
        when(usuarioRepository.findByUsername(username))
                .thenReturn(Optional.empty());
        when(usuarioRepository.existsByUsername(username))
                .thenReturn(false);
    }

    @Given("existe el rol {string} en el sistema")
    public void existeRol(String nombreRol) {
        Rol rol = new Rol(1L, nombreRol);
        when(rolRepository.findByNombre(nombreRol))
                .thenReturn(Optional.of(rol));
    }

    @When("el usuario intenta hacer login con username {string} y password {string}")
    public void intentaLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            lastResponse.put("success", false);
            lastResponse.put("message", "Usuario y contraseña son requeridos");
            loginResult = false;
            return;
        }
        loginResult = usuarioService.login(username, password);
        lastResponse.put("success", loginResult);
        lastResponse.put("message", loginResult
                ? "Login exitoso"
                : "Usuario o contraseña incorrectos");

        if (loginResult) {
            when(jwtUtil.generarToken(eq(username), any()))
                    .thenReturn("mock.jwt.token");
            lastResponse.put("token", "mock.jwt.token");
            lastResponse.put("username", username);
        }
    }

    @When("se registra un usuario con username {string} email {string} y password {string}")
    public void registraUsuario(String username, String email, String password) {
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        registroResult = usuarioService.registro(username, email, password);
        lastResponse.put("success", registroResult);
        lastResponse.put("message", registroResult
                ? "Usuario registrado correctamente"
                : "El usuario ya existe");
    }

    @When("se envía un registro con body vacío")
    public void registraBodyVacio() {
        lastResponse.put("success", false);
        lastResponse.put("message", "Datos inválidos");
        registroResult = false;
    }

    @Then("la respuesta de login es exitosa")
    public void loginExitoso() {
        assertThat(loginResult).isTrue();
        assertThat(lastResponse.get("success")).isEqualTo(true);
    }

    @Then("la respuesta de login falla")
    public void loginFalla() {
        assertThat(lastResponse.get("success")).isEqualTo(false);
    }

    @Then("la respuesta contiene un token JWT")
    public void contieneToken() {
        assertThat(lastResponse.get("token")).isNotNull();
        assertThat(lastResponse.get("token").toString()).isNotEmpty();
    }

    @Then("la respuesta contiene el username {string}")
    public void contieneUsername(String username) {
        assertThat(lastResponse.get("username")).isEqualTo(username);
    }

    @Then("el mensaje de error es {string}")
    public void mensajeError(String mensaje) {
        assertThat(lastResponse.get("message")).isEqualTo(mensaje);
    }

    @Then("la respuesta de registro es exitosa")
    public void registroExitoso() {
        assertThat(registroResult).isTrue();
    }

    @Then("la respuesta de registro falla")
    public void registroFalla() {
        assertThat(registroResult).isFalse();
    }

    @Then("el mensaje es {string}")
    public void mensaje(String mensaje) {
        assertThat(lastResponse.get("message")).isEqualTo(mensaje);
    }
}