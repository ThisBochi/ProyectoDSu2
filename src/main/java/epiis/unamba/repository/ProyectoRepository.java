package epiis.unamba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import epiis.unamba.model.EstadoProyecto;
import epiis.unamba.model.Proyecto;
import epiis.unamba.model.Usuario;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEstado(EstadoProyecto estado);
    List<Proyecto> findByCliente(Usuario cliente);
}
