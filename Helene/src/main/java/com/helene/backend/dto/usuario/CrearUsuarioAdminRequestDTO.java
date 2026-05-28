package com.helene.backend.dto.usuario;

import lombok.Data;

@Data
public class CrearUsuarioAdminRequestDTO {
    private String username;
    private String password;
    private String email;
    private String rol; // ADMIN, CLIENTE, EMPLEADO, SUSCRIPTOR
}
