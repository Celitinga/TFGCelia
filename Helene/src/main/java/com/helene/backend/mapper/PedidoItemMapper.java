package com.helene.backend.mapper;

import com.helene.backend.dto.pedido.PedidoItemDTO;
import com.helene.backend.entity.PedidoItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PedidoItemMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    @Mapping(source = "producto.imagenUrl", target = "productoImagen")
    @Mapping(source = "pedido.id", target = "pedidoId")
    PedidoItemDTO toDTO(PedidoItem item);

    List<PedidoItemDTO> toDTOList(List<PedidoItem> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    PedidoItem toEntity(PedidoItemDTO dto);

    List<PedidoItem> toEntityList(List<PedidoItemDTO> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    void updateFromDTO(PedidoItemDTO dto, @MappingTarget PedidoItem entity);
}