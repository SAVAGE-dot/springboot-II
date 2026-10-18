package com.ciberbus.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Bus;
import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.repository.BusRepository;
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
    private final BusRepository busRepository;

    public ViajeServiceImpl(
            ViajeRepository viajeRepository,
            ViajeAsientoRepository viajeAsientoRepository,
            RutaRepository rutaRepository,
            BusRepository busRepository) {
        this.viajeRepository = viajeRepository;
        this.viajeAsientoRepository = viajeAsientoRepository;
        this.rutaRepository = rutaRepository;
        this.busRepository = busRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Viaje> listar() {
        return viajeRepository.listarConDetalle();
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
    public Viaje guardarConAsientos(Viaje viaje) {
        if (viaje.getBus() == null || viaje.getBus().getIdBus() == null) {
            throw new NegocioException("Debe elegir un bus.");
        }
        Bus bus = busRepository.findById(viaje.getBus().getIdBus())
                .orElseThrow(() -> new NegocioException("El bus no existe."));
        viaje.setBus(bus);
        validarAltaViaje(viaje);

        Viaje guardado = viajeRepository.save(viaje);
        List<ViajeAsiento> existentes = viajeAsientoRepository
                .findByViaje_IdViajeOrderByPisoAscNroAsientoAsc(guardado.getIdViaje());
        if (!existentes.isEmpty()) {
            return guardado;
        }

        int total = bus.getNroAsientos() == null ? 40 : bus.getNroAsientos();
        for (int nro = 1; nro <= total; nro++) {
            ViajeAsiento asiento = new ViajeAsiento();
            asiento.setViaje(guardado);
            asiento.setNroAsiento(nro);
            asiento.setPiso(1);
            asiento.setEstado(1);
            viajeAsientoRepository.save(asiento);
        }
        return guardado;
    }

    /**
     * Se pueden crear varios viajes de la misma ruta y fecha (mañana, tarde, noche).
     * Lo único que no se repite es el código, y el mismo bus/conductor a la misma hora.
     */
    private void validarAltaViaje(Viaje viaje) {
        String codigo = viaje.getCodigoViaje() == null ? "" : viaje.getCodigoViaje().trim();
        if (codigo.isBlank()) {
            codigo = generarCodigoViaje(viaje.getFechaSalida(), viaje.getHoraSalida());
        }
        if (viajeRepository.existsByCodigoViaje(codigo)) {
            throw new NegocioException(
                    "Ya existe un viaje con el código " + codigo
                            + ". Use otro código o déjelo vacío para generarlo automáticamente.");
        }
        viaje.setCodigoViaje(codigo);

        Integer idBus = viaje.getBus().getIdBus();
        Integer idConductor = viaje.getConductor() == null ? null : viaje.getConductor().getIdConductor();
        LocalDate fecha = viaje.getFechaSalida();
        LocalTime hora = viaje.getHoraSalida();

        if (viajeRepository.existsByBus_IdBusAndFechaSalidaAndHoraSalida(idBus, fecha, hora)) {
            throw new NegocioException(
                    "Ese bus ya tiene un viaje el " + fecha + " a las " + hora
                            + ". Puede reutilizarlo en otro horario del mismo día.");
        }
        if (idConductor != null
                && viajeRepository.existsByConductor_IdConductorAndFechaSalidaAndHoraSalida(
                        idConductor, fecha, hora)) {
            throw new NegocioException(
                    "Ese conductor ya tiene un viaje el " + fecha + " a las " + hora
                            + ". Puede asignarlo a otro horario del mismo día.");
        }
    }

    private String generarCodigoViaje(LocalDate fecha, LocalTime hora) {
        String base = "CB-" + fecha.format(DateTimeFormatter.ofPattern("yyMMdd"))
                + "-" + hora.format(DateTimeFormatter.ofPattern("HHmm"));
        String codigo = base;
        int sufijo = 2;
        while (viajeRepository.existsByCodigoViaje(codigo)) {
            codigo = base + "-" + sufijo;
            sufijo++;
        }
        return codigo;
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
        return viajeRepository.buscarPorOrigenDestinoFecha(idOrigen, idDestino, fecha);
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

    @Override
    public void actualizarAsiento(ViajeAsiento asiento) {
        viajeAsientoRepository.save(asiento);
    }
}
