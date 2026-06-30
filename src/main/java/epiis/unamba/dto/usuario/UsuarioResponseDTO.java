package epiis.unamba.dto.usuario;

import java.math.BigDecimal;

import epiis.unamba.model.Rol;
import epiis.unamba.model.Usuario;

public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Rol rol;
    private BigDecimal tarifaHora;
    private String habilidades;
    private String biografia;
    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol();
        this.tarifaHora = usuario.getTarifaHora();
        this.habilidades = usuario.getHabilidades();
        this.biografia = usuario.getBiografia();
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public String getHabilidades() { return habilidades; }
    public String getBiografia() { return biografia; }
}
