package com.helene.backend.steps;

import com.helene.backend.entity.Usuario;
import com.helene.backend.service.UsuarioService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioServiceSteps {

    @Autowired
    private UsuarioService usuarioService;

    private Optional<Usuario> resultado;

    @When("se busca el usuario por username {string}")
    public void buscaUsuario(String username) {
        resultado = usuarioService.buscarPorUsername(username);
    }

    @Then("el usuario es encontrado")
    public void usuarioEncontrado() {
        assertThat(resultado).isPresent();
    }

    @Then("el usuario no es encontrado")
    public void usuarioNoEncontrado() {
        assertThat(resultado).isEmpty();
    }

    @Then("el username del usuario encontrado es {string}")
    public void usernameDelUsuario(String username) {
        assertThat(resultado.get().getUsername()).isEqualTo(username);
    }
}