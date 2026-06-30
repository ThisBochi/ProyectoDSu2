package epiis.unamba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import epiis.unamba.model.Postulacion;
import epiis.unamba.model.Proyecto;
import epiis.unamba.model.Usuario;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {
    boolean existsByProyectoAndFreelancer(Proyecto proyecto, Usuario freelancer);
    List<Postulacion> findByProyecto(Proyecto proyecto);
    List<Postulacion> findByFreelancer(Usuario freelancer);
}
