package com.ciberbus.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Bus;
import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Conductor;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Turno;
import com.ciberbus.repository.BusRepository;
import com.ciberbus.repository.CiudadRepository;
import com.ciberbus.repository.ConductorRepository;
import com.ciberbus.repository.RutaRepository;
import com.ciberbus.repository.TurnoRepository;
import com.ciberbus.service.CatalogoService;

@Service
@Transactional
public class CatalogoServiceImpl implements CatalogoService {

    private final CiudadRepository ciudadRepository;
    private final TurnoRepository turnoRepository;
    private final BusRepository busRepository;
    private final ConductorRepository conductorRepository;
    private final RutaRepository rutaRepository;

    public CatalogoServiceImpl(
            CiudadRepository ciudadRepository,
            TurnoRepository turnoRepository,
            BusRepository busRepository,
            ConductorRepository conductorRepository,
            RutaRepository rutaRepository) {
        this.ciudadRepository = ciudadRepository;
        this.turnoRepository = turnoRepository;
        this.busRepository = busRepository;
        this.conductorRepository = conductorRepository;
        this.rutaRepository = rutaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ciudad> listarCiudades() {
        return ciudadRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ciudad> buscarCiudad(Integer id) {
        return ciudadRepository.findById(id);
    }

    @Override
    public Ciudad guardarCiudad(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    @Override
    public Turno guardarTurno(Turno turno) {
        return turnoRepository.save(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> listarBuses() {
        return busRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bus> buscarBus(Integer id) {
        return busRepository.findById(id);
    }

    @Override
    public Bus guardarBus(Bus bus) {
        return busRepository.save(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarConductores() {
        return conductorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conductor> buscarConductor(Integer id) {
        return conductorRepository.findById(id);
    }

    @Override
    public Conductor guardarConductor(Conductor conductor) {
        return conductorRepository.save(conductor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> listarRutas() {
        return rutaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ruta> buscarRuta(Integer id) {
        return rutaRepository.findById(id);
    }

    @Override
    public Ruta guardarRuta(Ruta ruta) {
        return rutaRepository.save(ruta);
    }
}
