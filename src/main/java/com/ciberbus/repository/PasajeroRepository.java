package com.ciberbus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Pasajero;

public interface PasajeroRepository extends JpaRepository<Pasajero, Integer> {

    Optional<Pasajero> findByNroDocumento(String nroDocumento);
}
