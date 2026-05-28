package com.helene.backend.mapper;

import com.helene.backend.dto.usuario.UsuarioDto;
import com.helene.backend.dto.usuario.UsuarioRolDTO;
import com.helene.backend.entity.Usuario;
import com.helene.backend.entity.Rol;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDto usuarioDto);

    UsuarioDto toDto(Usuario usuario);

    @Mapping(target = "rol", expression = "java(mapRol(usuario.getRoles()))")
    UsuarioRolDTO toRolDto(Usuario usuario);

    default String mapRol(Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            return "CLIENTE";
        }
        return roles.iterator().next().getNombre();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Usuario partialUpdate(UsuarioDto usuarioDto, @MappingTarget Usuario usuario);
}