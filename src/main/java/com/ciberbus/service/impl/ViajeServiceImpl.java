package com.ciberbus.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.repository.ViajeAsientoRepository;
import com.ciberbus.repository.ViajeRepository;
import com.ciberbus.service.ViajeService;

@Service
@Transactional
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository viajeRepository;
    private final ViajeAsientoRepository viajeAsientoRepository;

    public ViajeServiceImpl(ViajeRepository viajeRepository, ViajeAsientoRepository viajeAsientoRepository) {
        this.viajeRepository = viajeRepository;
        this.viajeAsientoRepository = viajeAsientoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Viaje> listar() {
        return viajeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Viaje> buscarPorId(Integer id) {
        return viajeRepository.findById(id);
    }

    @Override
    public Viaje guardar(Viaje viaje) {
        return viajeRepository.save(viaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Viaje> buscar(Integer idOrigen, Integer idDestino, LocalDate fecha) {
        if (idOrigen == null || idDestino == null || fecha == null) {
            throw new NegocioException("Origen, destino y fecha son obligatorios.");
        }
        return viajeRepository.findByRuta_CiudadPartida_IdCiudadAndRuta_CiudadLlegada_IdCiudadAndFechaSalida(
                idOrigen, idDestino, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeAsiento> listarAsientos(Integer idViaje) {
        if (idViaje == null) {
            throw new NegocioException("Debe indicar el viaje.");
        }
        return viajeAsientoRepository.findByViaje_IdViajeOrderByPisoAscNroAsientoAsc(idViaje);
    }

    @Override
    public void actualizarAsiento(ViajeAsiento asiento) {
        viajeAsientoRepository.save(asiento);
    }
}
