package com.helene.backend.mapper;

import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.backend.entity.Pedido;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {PedidoItemMapper.class}
)
public interface PedidoMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.username", target = "usuarioNombre")
    @Mapping(source = "usuario.email", target = "usuarioEmail")
    @Mapping(source = "items", target = "items")
    PedidoDTO toDTO(Pedido pedido);

    List<PedidoDTO> toDTOList(List<Pedido> pedidos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "numeroPedido", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Pedido toEntity(PedidoDTO dto);

    List<Pedido> toEntityList(List<PedidoDTO> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "numeroPedido", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateFromDTO(PedidoDTO dto, @MappingTarget Pedido entity);
}