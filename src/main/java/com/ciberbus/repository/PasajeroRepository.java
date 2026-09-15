package com.ciberbus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Pasajero;

public interface PasajeroRepository extends JpaRepository<Pasajero, Integer> {
}
