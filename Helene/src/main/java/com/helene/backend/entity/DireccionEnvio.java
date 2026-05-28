package com.helene.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionEnvio {

    @Column(nullable = false, length = 100)
    private String nombreCompleto;

    @Column(nullable = false, length = 150)
    private String calle;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 50)
    private String piso;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 10)
    private String codigoPostal;

    @Column(nullable = false, length = 100)
    private String provincia;

    @Column(nullable = false, length = 100)
    private String pais;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 500)
    private String instruccionesEspeciales;
}