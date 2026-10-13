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

    Optional<Turno> buscarTurno(Integer id);

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

    // P3 — consulta filtrada por estado: null = todos, 1 = activos, 0 = inactivos

    List<Ciudad> listarCiudadesPorEstado(Integer estado);

    List<Turno> listarTurnosPorEstado(Integer estado);

    List<Bus> listarBusesPorEstado(Integer estado);

    List<Conductor> listarConductoresPorEstado(Integer estado);

    List<Ruta> listarRutasPorEstado(Integer estado);

    // P3 — baja lógica (estado = 0) con bloqueo si el maestro está referenciado

    void eliminarCiudad(Integer id);

    void eliminarTurno(Integer id);

    void eliminarBus(Integer id);

    void eliminarConductor(Integer id);

    void eliminarRuta(Integer id);
}
