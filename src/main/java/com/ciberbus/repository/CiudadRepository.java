package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {

    List<Ciudad> findByEstado(Integer estado);
}
