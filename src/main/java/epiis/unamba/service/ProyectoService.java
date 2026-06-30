package epiis.unamba.service;

import java.util.List;

import org.springframework.stereotype.Service;

import epiis.unamba.dto.proyecto.ProyectoCrearDTO;
import epiis.unamba.dto.proyecto.ProyectoResponseDTO;
import epiis.unamba.exception.BusinessException;
import epiis.unamba.exception.ResourceNotFoundException;
import epiis.unamba.model.EstadoProyecto;
import epiis.unamba.model.Proyecto;
import epiis.unamba.model.Rol;
import epiis.unamba.model.Usuario;
import epiis.unamba.repository.ProyectoRepository;

@Service
public class ProyectoService {
    private final ProyectoRepository proyectoRepository;
    private final UsuarioService usuarioService;

    public ProyectoService(ProyectoRepository proyectoRepository, UsuarioService usuarioService) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioService = usuarioService;
    }

    public List<ProyectoResponseDTO> listarTodos() {
        return proyectoRepository.findAll().stream().map(ProyectoResponseDTO::new).toList();
    }

    public List<ProyectoResponseDTO> listarAbiertos() {
        return proyectoRepository.findByEstado(EstadoProyecto.ABIERTO).stream().map(ProyectoResponseDTO::new).toList();
    }

    public Proyecto obtenerEntidad(Long id) {
        return proyectoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
    }

    public ProyectoResponseDTO crear(ProyectoCrearDTO request) {
        Usuario cliente = usuarioService.usuarioActual();
        if (cliente.getRol() != Rol.CLIENTE) throw new BusinessException("Solo un cliente puede crear proyectos");
        if (request.getTitulo() == null || request.getTitulo().isBlank()) throw new BusinessException("El titulo es obligatorio");
        if (request.getDescripcion() == null || request.getDescripcion().isBlank()) throw new BusinessException("La descripcion es obligatoria");
        if (request.getPresupuesto() == null || request.getPresupuesto().signum() <= 0) throw new BusinessException("El presupuesto debe ser mayor a cero");
        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo(request.getTitulo());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setPresupuesto(request.getPresupuesto());
        proyecto.setEstado(EstadoProyecto.ABIERTO);
        proyecto.setCliente(cliente);
        return new ProyectoResponseDTO(proyectoRepository.save(proyecto));
    }

    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }
}
