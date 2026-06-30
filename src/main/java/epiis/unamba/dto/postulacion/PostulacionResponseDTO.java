package epiis.unamba.dto.postulacion;

import java.math.BigDecimal;

import epiis.unamba.dto.usuario.UsuarioResponseDTO;
import epiis.unamba.model.EstadoPostulacion;
import epiis.unamba.model.Postulacion;

public class PostulacionResponseDTO {
    private Long id;
    private Long proyectoId;
    private String proyectoTitulo;
    private BigDecimal propuestaEconomica;
    private String cartaPresentacion;
    private EstadoPostulacion estado;
    private UsuarioResponseDTO freelancer;
    public PostulacionResponseDTO(Postulacion postulacion) {
        this.id = postulacion.getId();
        this.proyectoId = postulacion.getProyecto().getId();
        this.proyectoTitulo = postulacion.getProyecto().getTitulo();
        this.propuestaEconomica = postulacion.getPropuestaEconomica();
        this.cartaPresentacion = postulacion.getCartaPresentacion();
        this.estado = postulacion.getEstado();
        this.freelancer = new UsuarioResponseDTO(postulacion.getFreelancer());
    }
    public Long getId() { return id; }
    public Long getProyectoId() { return proyectoId; }
    public String getProyectoTitulo() { return proyectoTitulo; }
    public BigDecimal getPropuestaEconomica() { return propuestaEconomica; }
    public String getCartaPresentacion() { return cartaPresentacion; }
    public EstadoPostulacion getEstado() { return estado; }
    public UsuarioResponseDTO getFreelancer() { return freelancer; }
}
