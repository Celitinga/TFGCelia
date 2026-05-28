package com.helene.backend.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class UsuarioRolDTO implements Serializable {
    private final Long id;
    private final String username;
    private final String rol;
}