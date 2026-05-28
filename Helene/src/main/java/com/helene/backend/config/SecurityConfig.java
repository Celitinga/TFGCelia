package com.helene.backend.config;

import com.helene.backend.security.JwtAuthenticationFilter;
import com.helene.backend.security.JwtEntryPoint;
import com.helene.backend.security.JwtAccessDenied;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtAccessDenied jwtAccessDenied;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, JwtEntryPoint jwtEntryPoint, JwtAccessDenied jwtAccessDenied) {
        this.jwtFilter = jwtFilter;
        this.jwtEntryPoint = jwtEntryPoint;
        this.jwtAccessDenied = jwtAccessDenied;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtEntryPoint)
                        .accessDeniedHandler(jwtAccessDenied)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/register").permitAll()

                        .requestMatchers("/api/carrito/**").authenticated()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/empleado/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/api/suscriptor/**").hasAnyRole("ADMIN", "SUSCRIPTOR")
                        .requestMatchers("/api/cliente/**").hasAnyRole("ADMIN", "SUSCRIPTOR", "CLIENTE")

                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
