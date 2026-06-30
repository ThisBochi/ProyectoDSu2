package epiis.unamba.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import epiis.unamba.model.EstadoProyecto;
import epiis.unamba.model.Proyecto;
import epiis.unamba.model.Rol;
import epiis.unamba.model.Usuario;
import epiis.unamba.repository.ProyectoRepository;
import epiis.unamba.repository.UsuarioRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    CommandLineRunner seed(UsuarioRepository usuarioRepository, ProyectoRepository proyectoRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Usuario cliente = crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder, "cliente", "cliente@test.com", "1234", Rol.CLIENTE);
            Usuario freelancer = crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder, "freelancer", "freelancer@test.com", "1234", Rol.FREELANCER);
            crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder, "admin", "admin@test.com", "1234", Rol.ADMIN);

            if (freelancer.getHabilidades() == null) {
                freelancer.setHabilidades("Java, Spring Boot, MariaDB, APIs REST");
                freelancer.setBiografia("Freelancer de prueba para validar postulaciones y perfil tecnico.");
                freelancer.setTarifaHora(new BigDecimal("35.00"));
                usuarioRepository.save(freelancer);
            }

            if (proyectoRepository.count() == 0) {
                Proyecto proyecto = new Proyecto();
                proyecto.setTitulo("API REST para catalogo de servicios");
                proyecto.setDescripcion("Construir endpoints seguros, documentables y probables desde Postman.");
                proyecto.setPresupuesto(new BigDecimal("850.00"));
                proyecto.setEstado(EstadoProyecto.ABIERTO);
                proyecto.setCliente(cliente);
                proyectoRepository.save(proyecto);
            }
        };
    }

    private Usuario crearUsuarioSiNoExiste(UsuarioRepository repository, PasswordEncoder encoder, String username, String email, String password, Rol rol) {
        return repository.findByUsername(username).orElseGet(() -> {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(encoder.encode(password));
            usuario.setRol(rol);
            return repository.save(usuario);
        });
    }
}
