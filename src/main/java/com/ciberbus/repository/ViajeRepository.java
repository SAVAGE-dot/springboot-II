package com.ciberbus.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Viaje;

public interface ViajeRepository extends JpaRepository<Viaje, Integer> {

    List<Viaje> findByRuta_CiudadPartida_IdCiudadAndRuta_CiudadLlegada_IdCiudadAndFechaSalida(
            Integer idOrigen, Integer idDestino, LocalDate fechaSalida);
}
