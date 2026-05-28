package com.helene.backend.config;

import com.helene.backend.entity.Categoria;
import com.helene.backend.entity.Rol;
import com.helene.backend.repository.CategoriaRepository;
import com.helene.backend.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitConfig {

    @Bean
    CommandLineRunner initRoles(RolRepository rolRepository) {
        return args -> {
            if (rolRepository.count() == 0) {
                rolRepository.save(new Rol(null, "ADMIN"));
                rolRepository.save(new Rol(null, "EMPLEADO"));
                rolRepository.save(new Rol(null, "SUSCRIPTOR"));
                rolRepository.save(new Rol(null, "CLIENTE"));
            }
        };
    }

    @Bean
    CommandLineRunner initCategorias(CategoriaRepository categoriaRepository) {
        return args -> {
            if (categoriaRepository.count() == 0) {
                categoriaRepository.save(new Categoria(null, "crema facial", null));
                categoriaRepository.save(new Categoria(null, "crema corporal", null));
                categoriaRepository.save(new Categoria(null, "serums", null));
                categoriaRepository.save(new Categoria(null, "ofertas", null));

            }
        };
    }
}