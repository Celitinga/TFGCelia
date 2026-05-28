package com.helene.backend.service;

import com.helene.backend.dto.usuario.CrearUsuarioAdminRequestDTO;
import com.helene.backend.dto.usuario.UsuarioDto;
import com.helene.backend.entity.Rol;
import com.helene.backend.entity.Usuario;
import com.helene.backend.mapper.UsuarioMapper;
import com.helene.backend.repository.RolRepository;
import com.helene.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<UsuarioDto> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioDto crearUsuario(CrearUsuarioAdminRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(
                    "El username ya existe: " + request.getUsername());
        }

        Rol rol = rolRepository.findByNombre(request.getRol())
                .orElseThrow(() -> new RuntimeException(
                        "Rol no encontrado: " + request.getRol()));

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDto cambiarRol(Long id, String nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado: " + id));

        Rol rol = rolRepository.findByNombre(nuevoRol)
                .orElseThrow(() -> new RuntimeException(
                        "Rol no encontrado: " + nuevoRol));

        usuario.getRoles().clear();
        usuario.getRoles().add(rol);

        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado: " + id));

        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getNombre()));

        if (esAdmin) {
            throw new RuntimeException(
                    "No se puede eliminar una cuenta con rol ADMIN");
        }

        usuarioRepository.deleteById(id);
    }
}