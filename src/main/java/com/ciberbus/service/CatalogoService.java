package com.ciberbus.service;

import java.util.List;
import java.util.Optional;

import com.ciberbus.entity.Bus;
import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Conductor;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Turno;

/** P3 — maestros. CRUD de ciudad, turno, bus, conductor y ruta. */
public interface CatalogoService {

    List<Ciudad> listarCiudades();

    Optional<Ciudad> buscarCiudad(Integer id);

    Ciudad guardarCiudad(Ciudad ciudad);

    List<Turno> listarTurnos();

    Turno guardarTurno(Turno turno);

    List<Bus> listarBuses();

    Optional<Bus> buscarBus(Integer id);

    Bus guardarBus(Bus bus);

    List<Conductor> listarConductores();

    Optional<Conductor> buscarConductor(Integer id);

    Conductor guardarConductor(Conductor conductor);

    List<Ruta> listarRutas();

    Optional<Ruta> buscarRuta(Integer id);

    Ruta guardarRuta(Ruta ruta);
}
