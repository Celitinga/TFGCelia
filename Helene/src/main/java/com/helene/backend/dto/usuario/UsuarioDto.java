package com.helene.backend.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.helene.backend.entity.Usuario}
 */
@AllArgsConstructor
@Getter
@Data
public class UsuarioDto implements Serializable {
    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final LocalDateTime fechaRegistro;
    private final Set<RolDto> roles;

    @AllArgsConstructor
    @Getter
    public static class RolDto implements Serializable {
        private final Long id;
        private final String nombre;
    }
}