package com.ciberbus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

    List<Conductor> findByEstado(Integer estado);

    Optional<Conductor> findByNroDocumento(String nroDocumento);

    boolean existsByNroDocumento(String nroDocumento);

    boolean existsByCorreo(String correo);
}
