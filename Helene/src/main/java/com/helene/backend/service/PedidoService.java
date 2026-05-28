package com.helene.backend.service;

import com.helene.backend.dto.pedido.CrearPedidoRequestDTO;
import com.helene.backend.dto.pedido.ItemPedidoRequestDTO;
import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.backend.entity.*;
import com.helene.backend.enums.EstadoPedido;
import com.helene.backend.enums.MetodoPago;
import com.helene.backend.exceptions.NotFoundEntityException;
import com.helene.backend.mapper.PedidoMapper;
import com.helene.backend.repository.CarritoRepository;
import com.helene.backend.repository.PedidoItemRepository;
import com.helene.backend.repository.PedidoRepository;
import com.helene.backend.repository.ProductoRepository;
import com.helene.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService implements IPedidoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Override
    @Transactional
    public PedidoDTO crearPedido(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundEntityException("Usuario no encontrado"));

        List<Carrito> carrito = carritoRepository.findByUsuarioId(usuarioId);

        if (carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        double subtotal = carrito.stream()
                .mapToDouble(i -> i.getProducto().getPrecioFinal() * i.getCantidad())
                .sum();

        double costeEnvio = calcularCosteEnvio(subtotal);
        double total = subtotal + costeEnvio;

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);
        pedido.setMetodoPago(MetodoPago.CONTRAENTREGA);
        pedido.setSubtotal(subtotal);
        pedido.setCosteEnvio(costeEnvio);
        pedido.setDescuentoAplicado(0.0);
        pedido.setTotal(total);

        pedidoRepository.save(pedido);

        for (Carrito item : carrito) {
            PedidoItem pi = new PedidoItem();
            pi.setPedido(pedido);
            pi.setProducto(item.getProducto());
            pi.setCantidad(item.getCantidad());
            pi.setPrecioUnitario(item.getProducto().getPrecioFinal());
            pi.setDescuentoAplicado(0.0);
            pi.setSubtotal(item.getProducto().getPrecioFinal() * item.getCantidad());
            pi.setNombreProducto(item.getProducto().getNombre());
            pi.setImagenProducto(item.getProducto().getImagenUrl());

            pedidoItemRepository.save(pi);
            pedido.getItems().add(pi);
        }

        carritoRepository.deleteByUsuarioId(usuarioId);

        return pedidoMapper.toDTO(pedido);
    }

    @Override
    public List<PedidoDTO> listarPedidos(Long usuarioId) {
        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuarioId);
        return pedidoMapper.toDTOList(pedidos);
    }

    @Override
    @Transactional
    public PedidoDTO crearPedidoCompleto(CrearPedidoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new NotFoundEntityException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setSubtotal(request.getSubtotal());
        pedido.setCosteEnvio(request.getCosteEnvio());
        pedido.setDescuentoAplicado(request.getDescuentoAplicado() != null ? request.getDescuentoAplicado() : 0.0);
        pedido.setTotal(request.getTotal());
        pedido.setNotas(request.getNotas());
        pedido.setDireccionEnvio(request.getDireccionEnvio());

        if (request.getPaypalPaymentId() != null) {
            pedido.setPaypalPaymentId(request.getPaypalPaymentId());
        }

        pedidoRepository.save(pedido);

        for (ItemPedidoRequestDTO itemRequest : request.getItems()) {
            Producto producto = productoRepository.findById(itemRequest.getProductoId())
                    .orElseThrow(() -> new NotFoundEntityException("Producto no encontrado: " + itemRequest.getProductoId()));

            double descuentoItem = itemRequest.getDescuento() != null ? itemRequest.getDescuento() : 0.0;
            double precioConDescuento = itemRequest.getPrecioUnitario() * (1 - descuentoItem / 100);

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemRequest.getCantidad());
            item.setPrecioUnitario(itemRequest.getPrecioUnitario());
            item.setDescuentoAplicado(descuentoItem);
            item.setSubtotal(precioConDescuento * itemRequest.getCantidad());
            item.setNombreProducto(producto.getNombre());
            item.setImagenProducto(producto.getImagenUrl());

            pedidoItemRepository.save(item);
            pedido.getItems().add(item);
        }
        carritoRepository.deleteByUsuarioId(request.getUsuarioId());
        return pedidoMapper.toDTO(pedido);
    }

    @Override
    @Transactional
    public PedidoDTO actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundEntityException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoPedido.PAGADO) {
            pedido.setFechaEntrega(LocalDate.now().plusDays(3));
        }

        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoDTO confirmarPago(Long pedidoId, String paypalPaymentId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundEntityException("Pedido no encontrado"));

        pedido.setEstado(EstadoPedido.PAGADO);
        pedido.setPaypalPaymentId(paypalPaymentId);
        pedido.setFechaEntrega(LocalDate.now().plusDays(3));

        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoDTO cancelarPedido(Long pedidoId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundEntityException("Pedido no encontrado"));

        if (pedido.getEstado() == EstadoPedido.ENVIADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido ya enviado o entregado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        String notasActuales = pedido.getNotas() != null ? pedido.getNotas() : "";
        pedido.setNotas(notasActuales + " | Cancelado: " + motivo);

        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoActualizado);
    }

    @Override
    public PedidoDTO obtenerPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Pedido no encontrado"));
        return pedidoMapper.toDTO(pedido);
    }

    @Override
    public PedidoDTO obtenerPedidoPorNumero(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new NotFoundEntityException("Pedido no encontrado"));
        return pedidoMapper.toDTO(pedido);
    }

    @Override
    public List<PedidoDTO> obtenerPedidosPorEstado(EstadoPedido estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstado(estado);
        return pedidoMapper.toDTOList(pedidos);
    }

    @Override
    public List<PedidoDTO> obtenerTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidoMapper.toDTOList(pedidos);
    }

    private double calcularCosteEnvio(double subtotal) {
        if (subtotal > 50) {
            return 0;
        }
        return 4.95;
    }
}