package epiis.unamba.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import epiis.unamba.dto.auth.LoginRequestDTO;
import epiis.unamba.dto.auth.LoginResponseDTO;
import epiis.unamba.dto.auth.RegistroRequestDTO;
import epiis.unamba.dto.usuario.UsuarioResponseDTO;
import epiis.unamba.exception.BusinessException;
import epiis.unamba.model.Rol;
import epiis.unamba.model.Usuario;
import epiis.unamba.repository.UsuarioRepository;
import epiis.unamba.security.JwtProvider;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public UsuarioResponseDTO registrar(RegistroRequestDTO request) {
        validarRegistro(request);
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername().trim());
        usuario.setEmail(request.getEmail().trim());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException("Credenciales invalidas"));
        return new LoginResponseDTO(jwtProvider.generateToken(usuario), usuario.getId(), usuario.getUsername(), usuario.getEmail(), usuario.getRol());
    }

    private void validarRegistro(RegistroRequestDTO request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) throw new BusinessException("El username es obligatorio");
        if (request.getEmail() == null || request.getEmail().isBlank()) throw new BusinessException("El email es obligatorio");
        if (request.getPassword() == null || request.getPassword().length() < 4) throw new BusinessException("La password debe tener al menos 4 caracteres");
        if (request.getRol() == null || request.getRol() == Rol.ADMIN) throw new BusinessException("Solo se permite registro como CLIENTE o FREELANCER");
        if (usuarioRepository.existsByUsername(request.getUsername())) throw new BusinessException("El username ya existe");
        if (usuarioRepository.existsByEmail(request.getEmail())) throw new BusinessException("El email ya existe");
    }
}
