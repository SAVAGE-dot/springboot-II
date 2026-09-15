package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.ViajeAsiento;

public interface ViajeAsientoRepository extends JpaRepository<ViajeAsiento, Integer> {

    List<ViajeAsiento> findByViaje_IdViajeOrderByPisoAscNroAsientoAsc(Integer idViaje);
}
