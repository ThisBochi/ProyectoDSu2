package epiis.unamba.dto.proyecto;

import java.math.BigDecimal;

public class ProyectoCrearDTO {
    private String titulo;
    private String descripcion;
    private BigDecimal presupuesto;
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }
}
