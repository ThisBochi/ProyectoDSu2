package epiis.unamba.dto.postulacion;

import java.math.BigDecimal;

public class PostulacionRequestDTO {
    private Long proyectoId;
    private BigDecimal propuestaEconomica;
    private String cartaPresentacion;
    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
    public BigDecimal getPropuestaEconomica() { return propuestaEconomica; }
    public void setPropuestaEconomica(BigDecimal propuestaEconomica) { this.propuestaEconomica = propuestaEconomica; }
    public String getCartaPresentacion() { return cartaPresentacion; }
    public void setCartaPresentacion(String cartaPresentacion) { this.cartaPresentacion = cartaPresentacion; }
}
