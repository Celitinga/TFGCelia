package com.helene.backend.service;

import com.helene.backend.entity.Rol;
import com.helene.backend.entity.Usuario;
import com.helene.backend.repository.RolRepository;
import com.helene.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean login(String username, String password) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return passwordEncoder.matches(password, usuario.getPassword());
        }
        return false;
    }

    public boolean registro(String username, String email, String password) {
        if (usuarioRepository.existsByUsername(username)) {
            return false;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmail(email);

        Rol rolCliente = rolRepository.findByNombre("CLIENTE").orElseThrow(() ->
                new RuntimeException("Rol CLIENTE no encontrado"));
        usuario.getRoles().add(rolCliente);

        usuarioRepository.save(usuario);
        return true;
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}