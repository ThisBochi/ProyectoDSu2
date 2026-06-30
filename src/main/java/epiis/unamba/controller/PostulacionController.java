package epiis.unamba.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epiis.unamba.dto.postulacion.PostulacionRequestDTO;
import epiis.unamba.dto.postulacion.PostulacionResponseDTO;
import epiis.unamba.service.PostulacionService;

@RestController
@RequestMapping("/api/postulaciones")
public class PostulacionController {
    private final PostulacionService postulacionService;

    public PostulacionController(PostulacionService postulacionService) {
        this.postulacionService = postulacionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('FREELANCER')")
    public PostulacionResponseDTO postular(@RequestBody PostulacionRequestDTO request) {
        return postulacionService.postular(request);
    }

    @PatchMapping("/{id}/aceptar")
    @PreAuthorize("hasRole('CLIENTE')")
    public PostulacionResponseDTO aceptar(@PathVariable Long id) {
        return postulacionService.aceptar(id);
    }

    @GetMapping
    public List<PostulacionResponseDTO> listar(@RequestParam(required = false) Long proyectoId) {
        if (proyectoId != null) return postulacionService.listarPorProyecto(proyectoId);
        return postulacionService.misPostulaciones();
    }
}
