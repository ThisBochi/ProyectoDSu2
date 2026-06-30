package epiis.unamba.dto.auth;

import epiis.unamba.model.Rol;

public class RegistroRequestDTO {
    private String username;
    private String password;
    private String email;
    private Rol rol;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
