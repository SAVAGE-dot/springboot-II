package com.ciberbus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;

/**
 * P4 — viajes y asientos.
 * Contrato para P5: buscar() y listarAsientos(); reserva no toca el mapa de asientos directo.
 */
public interface ViajeService {

    List<Viaje> listar();

    Optional<Viaje> buscarPorId(Integer id);

    Viaje guardar(Viaje viaje);

    List<Viaje> buscar(Integer idOrigen, Integer idDestino, LocalDate fecha);

    List<ViajeAsiento> listarAsientos(Integer idViaje);

    void actualizarAsiento(ViajeAsiento asiento);
}
