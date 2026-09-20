package com.ciberbus.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.repository.ViajeAsientoRepository;
import com.ciberbus.repository.ViajeRepository;
import com.ciberbus.repository.RutaRepository;
import com.ciberbus.service.ViajeService;

@Service
@Transactional
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository viajeRepository;
    private final ViajeAsientoRepository viajeAsientoRepository;
    private final RutaRepository rutaRepository;

    public ViajeServiceImpl(
            ViajeRepository viajeRepository,
            ViajeAsientoRepository viajeAsientoRepository,
            RutaRepository rutaRepository) {
        this.viajeRepository = viajeRepository;
        this.viajeAsientoRepository = viajeAsientoRepository;
        this.rutaRepository = rutaRepository;
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
    @Transactional(readOnly = true)
    public Viaje buscarDetallePorId(Integer id) {
        return viajeRepository.buscarDetallePorId(id)
                .orElseThrow(() -> new NegocioException("El viaje seleccionado no existe."));
    }

    @Override
    public Viaje guardar(Viaje viaje) {
        return viajeRepository.save(viaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ciudad> listarOrigenesDisponibles() {
        return rutaRepository.listarOrigenesDisponibles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> listarDestinosPorOrigen(Integer idOrigen) {
        if (idOrigen == null) {
            return List.of();
        }
        return rutaRepository.listarDestinosPorOrigen(idOrigen, 1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Viaje> buscarDisponiblesPorRutaYFecha(Integer rutaId, LocalDate fecha) {
        if (rutaId == null || fecha == null) {
            throw new NegocioException("Destino y fecha de viaje son obligatorios.");
        }
        return viajeRepository.buscarDisponiblesPorRutaYFecha(rutaId, fecha);
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
    @Transactional(readOnly = true)
    public List<ViajeAsiento> validarAsientosSeleccionados(Integer idViaje, List<Integer> idsAsiento) {
        if (idsAsiento == null || idsAsiento.isEmpty()) {
            throw new NegocioException("Debe seleccionar al menos un asiento.");
        }
        if (idsAsiento.size() > 5 || new HashSet<>(idsAsiento).size() != idsAsiento.size()) {
            throw new NegocioException("Puede seleccionar entre uno y cinco asientos distintos.");
        }

        Map<Integer, ViajeAsiento> asientosPorId = new HashMap<>();
        for (ViajeAsiento asiento : listarAsientos(idViaje)) {
            asientosPorId.put(asiento.getIdViajeAsiento(), asiento);
        }

        return idsAsiento.stream().map(idAsiento -> {
            ViajeAsiento asiento = asientosPorId.get(idAsiento);
            if (asiento == null || !Integer.valueOf(1).equals(asiento.getEstado())) {
                throw new NegocioException("Uno de los asientos ya no está disponible.");
            }
            return asiento;
        }).toList();
    }
}
