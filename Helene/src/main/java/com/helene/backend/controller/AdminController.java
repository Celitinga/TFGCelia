package com.helene.backend.controller;

import com.helene.backend.dto.usuario.CrearUsuarioAdminRequestDTO;
import com.helene.backend.dto.usuario.UsuarioDto;
import com.helene.backend.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración", description = "Gestión de usuarios — solo accesible con rol ADMIN")
@SecurityRequirement(name = "Bearer Auth")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final IAdminService adminService;

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
        log.info("Admin solicita lista de todos los usuarios");
        List<UsuarioDto> usuarios = adminService.listarTodos();
        log.info("Se devuelven {} usuarios", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Crear nuevo usuario como admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Username ya existe", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN", content = @Content)
    })
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDto> crearUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"username\":\"empleado1\",\"email\":\"emp@helene.com\",\"password\":\"123456\",\"rol\":\"EMPLEADO\"}"
                    ))
            )
            @RequestBody CrearUsuarioAdminRequestDTO request) {
        log.info("Admin crea usuario: username='{}', email='{}', rol='{}'",
                request.getUsername(), request.getEmail(), request.getRol());
        try {
            UsuarioDto creado = adminService.crearUsuario(request);
            log.info("Usuario '{}' creado correctamente con id={}", creado.getUsername(), creado.getId());
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            log.error("Error al crear usuario '{}': {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Eliminar usuario", description = "No se puede eliminar una cuenta ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "Intento de eliminar un ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(
            @Parameter(description = "ID del usuario a eliminar", example = "3")
            @PathVariable Long id) {
        log.info("Admin solicita eliminar usuario con id={}", id);
        try {
            adminService.eliminarUsuario(id);
            log.info("Usuario con id={} eliminado correctamente", id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar usuario con id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Cambiar rol de usuario",
            description = "Roles disponibles: CLIENTE, EMPLEADO, SUSCRIPTOR, ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado", content = @Content)
    })
    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<UsuarioDto> cambiarRol(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"rol\":\"EMPLEADO\"}"))
            )
            @RequestBody Map<String, String> body) {
        String nuevoRol = body.get("rol");
        log.info("Admin cambia rol del usuario id={} a '{}'", id, nuevoRol);
        try {
            UsuarioDto actualizado = adminService.cambiarRol(id, nuevoRol);
            log.info("Rol del usuario id={} actualizado correctamente a '{}'", id, nuevoRol);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("Error al cambiar rol del usuario id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}