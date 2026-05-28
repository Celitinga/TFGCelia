package com.helene.backend.service;

import com.helene.backend.dto.categoria.CategoriaDTO;
import com.helene.backend.dto.categoria.CrearCategoriaDTO;
import com.helene.backend.entity.Categoria;
import com.helene.backend.exceptions.CreateEntityException;
import com.helene.backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService implements ICategoriaService{
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public CategoriaDTO crearCategoria(CrearCategoriaDTO dto) {

        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new CreateEntityException("La categoría ya existe");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());

        categoriaRepository.save(categoria);

        CategoriaDTO response = new CategoriaDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());

        return response;
    }

    @Override
    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(cat -> {
                    CategoriaDTO dto = new CategoriaDTO();
                    dto.setId(cat.getId());
                    dto.setNombre(cat.getNombre());
                    return dto;
                })
                .toList();
    }
}
