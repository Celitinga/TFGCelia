package com.helene.backend.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.helene.backend.repository.UsuarioRepository;
import com.helene.backend.repository.RolRepository;
import com.helene.backend.security.JwtUtil;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfig {

    @MockBean
    public UsuarioRepository usuarioRepository;

    @MockBean
    public RolRepository rolRepository;

    @MockBean
    public JwtUtil jwtUtil;
}