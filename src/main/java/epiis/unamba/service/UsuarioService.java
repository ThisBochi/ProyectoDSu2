package epiis.unamba.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import epiis.unamba.dto.usuario.PerfilUpdateDTO;
import epiis.unamba.dto.usuario.UsuarioResponseDTO;
import epiis.unamba.exception.BusinessException;
import epiis.unamba.exception.ResourceNotFoundException;
import epiis.unamba.model.Rol;
import epiis.unamba.model.Usuario;
import epiis.unamba.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    public List<UsuarioResponseDTO> listarFreelancers() {
        return usuarioRepository.findByRol(Rol.FREELANCER).stream().map(UsuarioResponseDTO::new).toList();
    }

    public UsuarioResponseDTO actualizarPerfilFreelancer(PerfilUpdateDTO request) {
        Usuario usuario = usuarioActual();
        if (usuario.getRol() != Rol.FREELANCER) throw new BusinessException("Solo los freelancers pueden editar este perfil");
        if (request.getTarifaHora() != null && request.getTarifaHora().signum() < 0) throw new BusinessException("La tarifa no puede ser negativa");
        usuario.setTarifaHora(request.getTarifaHora());
        usuario.setHabilidades(request.getHabilidades());
        usuario.setBiografia(request.getBiografia());
        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }
}
