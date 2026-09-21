package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Turno;

public interface TurnoRepository extends JpaRepository<Turno, Integer> {

    List<Turno> findByEstado(Integer estado);
}
