package com.helene.backend.steps;

import com.helene.backend.dto.usuario.CrearUsuarioAdminRequestDTO;
import com.helene.backend.dto.usuario.UsuarioDto;
import com.helene.backend.entity.Rol;
import com.helene.backend.entity.Usuario;
import com.helene.backend.mapper.UsuarioMapper;
import com.helene.backend.repository.RolRepository;
import com.helene.backend.repository.UsuarioRepository;
import com.helene.backend.service.AdminService;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdminSteps {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    private UsuarioDto resultadoDto;
    private Exception excepcionCapturada;
    private List<UsuarioDto> listaResultado;

    @Before
    public void resetState() {
        resultadoDto = null;
        excepcionCapturada = null;
        listaResultado = null;
    }

    @Given("existen {int} usuarios en el sistema")
    public void existenUsuarios(int cantidad) {
        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            Usuario u = new Usuario();
            u.setId((long) i);
            u.setUsername("usuario" + i);
            u.setRoles(new HashSet<>());
            usuarios.add(u);
        }
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(usuarioMapper.toDto(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            return new UsuarioDto(u.getId(), u.getUsername(),
                    null, u.getEmail(), u.getFechaRegistro(), Set.of());
        });
    }

    @Given("existe un usuario con id {int} username {string} y rol {string}")
    public void existeUsuarioConIdYRol(int id, String username, String rolNombre) {
        Rol rol = new Rol((long) id, rolNombre);
        Usuario usuario = new Usuario();
        usuario.setId((long) id);
        usuario.setUsername(username);
        usuario.setRoles(new HashSet<>(Set.of(rol)));

        when(usuarioRepository.findById((long) id))
                .thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDto(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            Set<UsuarioDto.RolDto> roles = Set.of(
                    new UsuarioDto.RolDto(rol.getId(), rol.getNombre()));
            return new UsuarioDto(u.getId(), u.getUsername(),
                    null, u.getEmail(), u.getFechaRegistro(), roles);
        });
    }

    @When("el admin solicita la lista de usuarios")
    public void adminListaUsuarios() {
        listaResultado = adminService.listarTodos();
    }

    @When("el admin crea un usuario con username {string} email {string} password {string} y rol {string}")
    public void adminCreaUsuario(String username, String email,
                                 String password, String rolNombre) {
        CrearUsuarioAdminRequestDTO request = new CrearUsuarioAdminRequestDTO();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setRol(rolNombre);

        when(usuarioRepository.save(any())).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(99L);
            return u;
        });

        try {
            resultadoDto = adminService.crearUsuario(request);
        } catch (Exception e) {
            excepcionCapturada = e;
        }
    }

    @When("el admin elimina el usuario con id {int}")
    public void adminEliminaUsuario(int id) {
        try {
            doNothing().when(usuarioRepository).deleteById((long) id);
            adminService.eliminarUsuario((long) id);
        } catch (Exception e) {
            excepcionCapturada = e;
        }
    }

    @When("el admin cambia el rol del usuario con id {int} a {string}")
    public void adminCambiaRol(int id, String nuevoRol) {
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        try {
            resultadoDto = adminService.cambiarRol((long) id, nuevoRol);
        } catch (Exception e) {
            excepcionCapturada = e;
        }
    }

    @Then("la respuesta contiene {int} usuarios")
    public void respuestaContieneUsuarios(int cantidad) {
        assertThat(listaResultado).hasSize(cantidad);
    }

    @Then("el usuario es creado correctamente")
    public void usuarioCreadoCorrectamente() {
        assertThat(excepcionCapturada).isNull();
        assertThat(resultadoDto).isNotNull();
    }

    @Then("el usuario tiene el rol {string}")
    public void usuarioTieneRol(String rolEsperado) {
        assertThat(resultadoDto).isNotNull();
        assertThat(resultadoDto.getRoles())
                .anyMatch(r -> r.getNombre().equals(rolEsperado));
    }

    @Then("el usuario es eliminado correctamente")
    public void usuarioEliminadoCorrectamente() {
        assertThat(excepcionCapturada).isNull();
        verify(usuarioRepository, times(1)).deleteById(anyLong());
    }

    @Then("se lanza una excepción con mensaje {string}")
    public void lanzaExcepcion(String mensajeEsperado) {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getMessage()).contains(mensajeEsperado);
    }
}