package com.ciberbus.service;

import java.util.Optional;

import com.ciberbus.entity.Reserva;

/** P5 — reserva, pago simulado y autogestión. Consume ViajeService, no el repository de asientos. */
public interface ReservaService {

    Optional<Reserva> buscarPorCodigo(String codigoReserva);

    Reserva guardar(Reserva reserva);
}
