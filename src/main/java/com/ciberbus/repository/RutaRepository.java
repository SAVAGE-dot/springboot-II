package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Ruta;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    List<Ruta> findByEstado(Integer estado);
}
