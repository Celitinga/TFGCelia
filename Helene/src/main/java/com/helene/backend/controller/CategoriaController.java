package com.helene.backend.controller;

import com.helene.backend.dto.categoria.CategoriaDTO;
import com.helene.backend.dto.categoria.CrearCategoriaDTO;
import com.helene.backend.service.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public CategoriaDTO crear(@RequestBody CrearCategoriaDTO dto) {
        log.info("Solicitud de creación de categoría: '{}'", dto.getNombre());
        try {
            CategoriaDTO creada = categoriaService.crearCategoria(dto);
            log.info("Categoría '{}' creada correctamente con id={}", creada.getNombre(), creada.getId());
            return creada;
        } catch (Exception e) {
            log.error("Error al crear categoría '{}': {}", dto.getNombre(), e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<CategoriaDTO> listar() {
        log.info("Obteniendo listado de categorías");
        List<CategoriaDTO> categorias = categoriaService.listarCategorias();
        log.info("Se devuelven {} categorías", categorias.size());
        return categorias;
    }
}