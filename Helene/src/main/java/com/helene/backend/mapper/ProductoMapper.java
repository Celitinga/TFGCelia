package com.helene.backend.mapper;

import com.helene.backend.dto.producto.ProductoDTO;
import com.helene.backend.entity.Producto;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductoMapper {

    @Mapping(source = "categoria.nombre", target = "categoria")
    @Mapping(source = "categoria.id", target = "categoriaId")
    ProductoDTO toDTO(Producto producto);

    List<ProductoDTO> toDTOList(List<Producto> productos);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoDTO dto);

    List<Producto> toEntityList(List<ProductoDTO> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoria", ignore = true)
    void updateFromDTO(ProductoDTO dto, @MappingTarget Producto entity);
}
