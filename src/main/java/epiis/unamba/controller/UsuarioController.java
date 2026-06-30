package epiis.unamba.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epiis.unamba.dto.usuario.PerfilUpdateDTO;
import epiis.unamba.dto.usuario.UsuarioResponseDTO;
import epiis.unamba.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/freelancers")
    public List<UsuarioResponseDTO> freelancers() {
        return usuarioService.listarFreelancers();
    }

    @PutMapping("/perfil")
    @PreAuthorize("hasRole('FREELANCER')")
    public UsuarioResponseDTO actualizarPerfil(@RequestBody PerfilUpdateDTO request) {
        return usuarioService.actualizarPerfilFreelancer(request);
    }
}
