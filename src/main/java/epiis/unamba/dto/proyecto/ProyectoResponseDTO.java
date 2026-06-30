package epiis.unamba.dto.proyecto;

import java.math.BigDecimal;

import epiis.unamba.dto.usuario.UsuarioResponseDTO;
import epiis.unamba.model.EstadoProyecto;
import epiis.unamba.model.Proyecto;

public class ProyectoResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal presupuesto;
    private EstadoProyecto estado;
    private UsuarioResponseDTO cliente;
    private UsuarioResponseDTO freelancerAsignado;
    public ProyectoResponseDTO(Proyecto proyecto) {
        this.id = proyecto.getId();
        this.titulo = proyecto.getTitulo();
        this.descripcion = proyecto.getDescripcion();
        this.presupuesto = proyecto.getPresupuesto();
        this.estado = proyecto.getEstado();
        this.cliente = new UsuarioResponseDTO(proyecto.getCliente());
        this.freelancerAsignado = proyecto.getFreelancerAsignado() == null ? null : new UsuarioResponseDTO(proyecto.getFreelancerAsignado());
    }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPresupuesto() { return presupuesto; }
    public EstadoProyecto getEstado() { return estado; }
    public UsuarioResponseDTO getCliente() { return cliente; }
    public UsuarioResponseDTO getFreelancerAsignado() { return freelancerAsignado; }
}
