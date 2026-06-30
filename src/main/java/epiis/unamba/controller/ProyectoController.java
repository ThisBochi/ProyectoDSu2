package epiis.unamba.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epiis.unamba.dto.proyecto.ProyectoCrearDTO;
import epiis.unamba.dto.proyecto.ProyectoResponseDTO;
import epiis.unamba.service.ProyectoService;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<ProyectoResponseDTO> listar(@RequestParam(defaultValue = "false") boolean soloAbiertos) {
        return soloAbiertos ? proyectoService.listarAbiertos() : proyectoService.listarTodos();
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ProyectoResponseDTO> crear(@RequestBody ProyectoCrearDTO request) {
        return new ResponseEntity<>(proyectoService.crear(request), HttpStatus.CREATED);
    }
}
