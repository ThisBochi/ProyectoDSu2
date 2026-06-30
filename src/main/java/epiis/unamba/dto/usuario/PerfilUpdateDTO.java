package epiis.unamba.dto.usuario;

import java.math.BigDecimal;

public class PerfilUpdateDTO {
    private BigDecimal tarifaHora;
    private String habilidades;
    private String biografia;
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }
    public String getHabilidades() { return habilidades; }
    public void setHabilidades(String habilidades) { this.habilidades = habilidades; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
}
