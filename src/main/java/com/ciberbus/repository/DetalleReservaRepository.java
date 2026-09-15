package com.ciberbus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.DetalleReserva;

public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Integer> {
}
