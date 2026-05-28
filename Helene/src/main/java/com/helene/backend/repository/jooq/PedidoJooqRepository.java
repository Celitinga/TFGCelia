package com.helene.backend.repository.jooq;

import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.jooq.enums.PedidosEstado;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.helene.jooq.tables.Pedidos.PEDIDOS;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.date;
import static org.jooq.impl.DSL.month;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.year;

@RequiredArgsConstructor
@Repository
public class PedidoJooqRepository implements IPedidoJooqRepository {

    private final DSLContext dsl;

    @Override
    public List<PedidoDTO> findPedidosPendientesPago(LocalDateTime fechaLimite) {

        return dsl
                .select(
                        PEDIDOS.ID,
                        PEDIDOS.NUMERO_PEDIDO,
                        PEDIDOS.FECHA_CREACION,
                        PEDIDOS.FECHA_ENTREGA,
                        PEDIDOS.USUARIO_ID,
                        PEDIDOS.ESTADO,
                        PEDIDOS.METODO_PAGO,
                        PEDIDOS.SUBTOTAL,
                        PEDIDOS.COSTE_ENVIO,
                        PEDIDOS.DESCUENTO_APLICADO,
                        PEDIDOS.TOTAL,
                        PEDIDOS.NOTAS,
                        PEDIDOS.PAYPAL_PAYMENT_ID
                )
                .from(PEDIDOS)
                .where(PEDIDOS.ESTADO.eq(PedidosEstado.PENDIENTE_PAGO))
                .and(PEDIDOS.FECHA_CREACION.lt(fechaLimite))
                .fetchInto(PedidoDTO.class);
    }

    @Override
    public List<Object[]> findVentasDiarias() {

        return dsl
                .select(
                        date((Date) PEDIDOS.FECHA_CREACION).as("fecha"),
                        sum(PEDIDOS.TOTAL).as("total")
                )
                .from(PEDIDOS)
                .where(PEDIDOS.ESTADO.eq(PedidosEstado.PAGADO))
                .groupBy(date((Date) PEDIDOS.FECHA_CREACION))
                .fetch()
                .map(r -> new Object[]{
                        r.get("fecha"),
                        r.get("total")
                });
    }

    @Override
    public List<Object[]> countPedidosByEstado() {

        return dsl
                .select(
                        PEDIDOS.ESTADO,
                        count().as("cantidad")
                )
                .from(PEDIDOS)
                .groupBy(PEDIDOS.ESTADO)
                .fetch()
                .map(r -> new Object[]{
                        r.get(PEDIDOS.ESTADO),
                        r.get("cantidad")
                });
    }

    @Override
    public List<Object[]> findVentasPorMes() {

        return dsl
                .select(
                        year(PEDIDOS.FECHA_CREACION).as("anio"),
                        month(PEDIDOS.FECHA_CREACION).as("mes"),
                        sum(PEDIDOS.TOTAL).as("total")
                )
                .from(PEDIDOS)
                .where(PEDIDOS.ESTADO.eq(PedidosEstado.PAGADO))
                .groupBy(
                        year(PEDIDOS.FECHA_CREACION),
                        month(PEDIDOS.FECHA_CREACION)
                )
                .fetch()
                .map(r -> new Object[]{
                        r.get("anio"),
                        r.get("mes"),
                        r.get("total")
                });
    }
}