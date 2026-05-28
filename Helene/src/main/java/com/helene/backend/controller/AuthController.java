package com.helene.backend.controller;

import com.helene.backend.security.JwtUtil;
import com.helene.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints de login y registro de usuarios")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso o credenciales incorrectas",
                    content = @Content(examples = {
                            @ExampleObject(name = "Éxito", value = "{\"success\":true,\"token\":\"eyJ...\",\"roles\":[\"CLIENTE\"]}"),
                            @ExampleObject(name = "Error", value = "{\"success\":false,\"message\":\"Usuario o contraseña incorrectos\"}")
                    }))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        log.info("Intento de login para el usuario: {}", username);

        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            log.warn("Login fallido: campos vacíos o nulos para usuario '{}'", username);
            response.put("success", false);
            response.put("message", "Usuario y contraseña son requeridos");
            return ResponseEntity.ok(response);
        }

        boolean isValid = usuarioService.login(username, password);

        if (isValid) {
            var usuario = usuarioService.buscarPorUsername(username).orElseThrow();
            Set<String> roles = usuario.getRoles().stream()
                    .map(r -> r.getNombre())
                    .collect(Collectors.toSet());
            String token = jwtUtil.generarToken(username, roles);

            log.info("Login exitoso para usuario '{}' con roles: {}", username, roles);

            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("username", username);
            response.put("token", token);
            response.put("roles", roles);
            response.put("userId", usuario.getId());
            return ResponseEntity.ok(response);
        }

        log.warn("Login fallido: credenciales incorrectas para usuario '{}'", username);
        response.put("success", false);
        response.put("message", "Usuario o contraseña incorrectos");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta con rol CLIENTE por defecto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro exitoso o usuario ya existe",
                    content = @Content(examples = {
                            @ExampleObject(name = "Éxito", value = "{\"success\":true,\"message\":\"Usuario registrado correctamente\"}"),
                            @ExampleObject(name = "Ya existe", value = "{\"success\":false,\"message\":\"El usuario ya existe\"}")
                    }))
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email    = body.get("email");
        String password = body.get("password");

        log.info("Solicitud de registro para username: '{}', email: '{}'", username, email);

        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null) {
            log.warn("Registro fallido: datos nulos recibidos");
            response.put("success", false);
            response.put("message", "Datos inválidos");
            return ResponseEntity.ok(response);
        }

        boolean ok = usuarioService.registro(username, email, password);

        if (ok) {
            log.info("Usuario '{}' registrado correctamente con email '{}'", username, email);
            response.put("success", true);
            response.put("message", "Usuario registrado correctamente");
        } else {
            log.warn("Registro fallido: el username '{}' ya existe", username);
            response.put("success", false);
            response.put("message", "El usuario ya existe");
        }

        return ResponseEntity.ok(response);
    }
}