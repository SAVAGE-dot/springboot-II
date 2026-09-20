package com.ciberbus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;

/**
 * P4 — viajes y asientos.
 * Contrato para P5: buscar() y listarAsientos(); reserva no toca el mapa de asientos directo.
 */
public interface ViajeService {

    List<Viaje> listar();

    Optional<Viaje> buscarPorId(Integer id);

    Viaje buscarDetallePorId(Integer id);

    Viaje guardar(Viaje viaje);

    List<Ciudad> listarOrigenesDisponibles();

    List<Ruta> listarDestinosPorOrigen(Integer idOrigen);

    List<Viaje> buscarDisponiblesPorRutaYFecha(Integer rutaId, LocalDate fecha);

    List<Viaje> buscar(Integer idOrigen, Integer idDestino, LocalDate fecha);

    List<ViajeAsiento> listarAsientos(Integer idViaje);

    List<ViajeAsiento> validarAsientosSeleccionados(Integer idViaje, List<Integer> idsAsiento);
}
