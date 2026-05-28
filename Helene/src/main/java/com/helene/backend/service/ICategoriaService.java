package com.helene.backend.service;

import com.helene.backend.dto.categoria.CategoriaDTO;
import com.helene.backend.dto.categoria.CrearCategoriaDTO;

import java.util.List;

public interface ICategoriaService {
    CategoriaDTO crearCategoria(CrearCategoriaDTO dto);
    List<CategoriaDTO> listarCategorias();
}
