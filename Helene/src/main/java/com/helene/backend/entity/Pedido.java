package com.helene.backend.entity;

import com.helene.backend.enums.EstadoPedido;
import com.helene.backend.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroPedido;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDate fechaEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double costeEnvio;

    @Column(nullable = false)
    private Double descuentoAplicado;

    @Column(nullable = false)
    private Double total;

    @Column(length = 500)
    private String notas;

    private String paypalPaymentId;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoItem> items = new ArrayList<>();

    @Embedded
    private DireccionEnvio direccionEnvio;

    @PrePersist
    public void generarNumeroPedido() {
        if (this.numeroPedido == null) {
            String año = String.valueOf(java.time.Year.now().getValue());
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
            this.numeroPedido = "HEL-" + año + "-" + timestamp;
        }

        if (this.estado == null) {
            this.estado = EstadoPedido.PENDIENTE_PAGO;
        }

        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}