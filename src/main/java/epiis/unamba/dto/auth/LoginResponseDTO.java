package epiis.unamba.dto.auth;

import epiis.unamba.model.Rol;

public class LoginResponseDTO {
    private String token;
    private Long id;
    private String username;
    private String email;
    private Rol rol;
    public LoginResponseDTO(String token, Long id, String username, String email, Rol rol) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.rol = rol;
    }
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
}
