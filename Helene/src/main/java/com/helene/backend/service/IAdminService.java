package com.helene.backend.service;

import com.helene.backend.dto.usuario.CrearUsuarioAdminRequestDTO;
import com.helene.backend.dto.usuario.UsuarioDto;

import java.util.List;

public interface IAdminService {

    List<UsuarioDto> listarTodos();

    UsuarioDto crearUsuario(CrearUsuarioAdminRequestDTO request);

    void eliminarUsuario(Long id);

    UsuarioDto cambiarRol(Long id, String nuevoRol);
}