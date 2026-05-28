package com.helene.backend.mapper;

import com.helene.backend.dto.carrito.CarritoDTO;
import com.helene.backend.dto.carrito.CarritoRespuestaDTO;
import com.helene.backend.entity.Carrito;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CarritoMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    @Mapping(source = "producto.precioFinal", target = "precioProducto")
    @Mapping(source = "producto.imagenUrl", target = "imagenUrl")
    @Mapping(target = "subtotal", ignore = true)
    CarritoRespuestaDTO toDTO(Carrito carrito);

    List<CarritoRespuestaDTO> toDTOList(List<Carrito> carritoList);

    @Mapping(source = "productoId", target = "producto.id")
    Carrito toEntity(CarritoDTO dto);
}
