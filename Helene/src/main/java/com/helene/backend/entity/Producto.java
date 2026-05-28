package com.helene.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precioOriginal;

    @Column(nullable = false)
    private Double precioFinal;

    @Column(nullable = false)
    private Integer descuento;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(length = 500)
    private String imagenUrl;

    public void aplicarDescuento() {
        if (descuento != null && descuento > 0) {
            this.precioFinal = precioOriginal - (precioOriginal * descuento / 100.0);
        } else {
            this.precioFinal = precioOriginal;
        }
    }
}
