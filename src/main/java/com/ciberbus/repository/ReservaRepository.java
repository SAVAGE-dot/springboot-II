package com.ciberbus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByCodigoReserva(String codigoReserva);
}
