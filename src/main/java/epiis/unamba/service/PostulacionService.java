package epiis.unamba.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import epiis.unamba.dto.postulacion.PostulacionRequestDTO;
import epiis.unamba.dto.postulacion.PostulacionResponseDTO;
import epiis.unamba.exception.BusinessException;
import epiis.unamba.exception.ResourceNotFoundException;
import epiis.unamba.model.EstadoPostulacion;
import epiis.unamba.model.EstadoProyecto;
import epiis.unamba.model.Postulacion;
import epiis.unamba.model.Proyecto;
import epiis.unamba.model.Rol;
import epiis.unamba.model.Usuario;
import epiis.unamba.repository.PostulacionRepository;

@Service
public class PostulacionService {
    private final PostulacionRepository postulacionRepository;
    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;

    public PostulacionService(PostulacionRepository postulacionRepository, ProyectoService proyectoService, UsuarioService usuarioService) {
        this.postulacionRepository = postulacionRepository;
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
    }

    public PostulacionResponseDTO postular(PostulacionRequestDTO request) {
        Usuario freelancer = usuarioService.usuarioActual();
        if (freelancer.getRol() != Rol.FREELANCER) throw new BusinessException("Solo un freelancer puede postularse");
        Proyecto proyecto = proyectoService.obtenerEntidad(request.getProyectoId());
        if (proyecto.getEstado() != EstadoProyecto.ABIERTO) throw new BusinessException("Solo se permiten postulaciones a proyectos abiertos");
        if (postulacionRepository.existsByProyectoAndFreelancer(proyecto, freelancer)) throw new BusinessException("Ya te postulaste a este proyecto");
        if (request.getPropuestaEconomica() == null || request.getPropuestaEconomica().signum() <= 0) throw new BusinessException("La propuesta economica debe ser mayor a cero");
        if (request.getCartaPresentacion() == null || request.getCartaPresentacion().isBlank()) throw new BusinessException("La carta de presentacion es obligatoria");
        Postulacion postulacion = new Postulacion();
        postulacion.setProyecto(proyecto);
        postulacion.setFreelancer(freelancer);
        postulacion.setPropuestaEconomica(request.getPropuestaEconomica());
        postulacion.setCartaPresentacion(request.getCartaPresentacion());
        postulacion.setEstado(EstadoPostulacion.PENDIENTE);
        return new PostulacionResponseDTO(postulacionRepository.save(postulacion));
    }

    @Transactional
    public PostulacionResponseDTO aceptar(Long postulacionId) {
        Usuario cliente = usuarioService.usuarioActual();
        Postulacion aceptada = postulacionRepository.findById(postulacionId)
            .orElseThrow(() -> new ResourceNotFoundException("Postulacion no encontrada"));
        Proyecto proyecto = aceptada.getProyecto();
        if (!proyecto.getCliente().getId().equals(cliente.getId())) throw new BusinessException("Solo el cliente dueno del proyecto puede aceptar postulaciones");
        if (proyecto.getEstado() != EstadoProyecto.ABIERTO) throw new BusinessException("Este proyecto ya no acepta postulaciones");
        List<Postulacion> postulaciones = postulacionRepository.findByProyecto(proyecto);
        for (Postulacion postulacion : postulaciones) {
            postulacion.setEstado(postulacion.getId().equals(aceptada.getId()) ? EstadoPostulacion.ACEPTADA : EstadoPostulacion.RECHAZADA);
        }
        proyecto.setEstado(EstadoProyecto.EN_PROGRESO);
        proyecto.setFreelancerAsignado(aceptada.getFreelancer());
        proyectoService.guardar(proyecto);
        postulacionRepository.saveAll(postulaciones);
        return new PostulacionResponseDTO(aceptada);
    }

    public List<PostulacionResponseDTO> listarPorProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoService.obtenerEntidad(proyectoId);
        Usuario usuario = usuarioService.usuarioActual();
        if (!proyecto.getCliente().getId().equals(usuario.getId())) throw new BusinessException("Solo el cliente dueno puede ver estas postulaciones");
        return postulacionRepository.findByProyecto(proyecto).stream().map(PostulacionResponseDTO::new).toList();
    }

    public List<PostulacionResponseDTO> misPostulaciones() {
        return postulacionRepository.findByFreelancer(usuarioService.usuarioActual()).stream().map(PostulacionResponseDTO::new).toList();
    }
}
