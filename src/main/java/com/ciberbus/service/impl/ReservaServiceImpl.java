package com.ciberbus.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.DetalleReserva;
import com.ciberbus.entity.Pasajero;
import com.ciberbus.entity.Reserva;
import com.ciberbus.entity.Usuario;
import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.repository.DetalleReservaRepository;
import com.ciberbus.repository.PasajeroRepository;
import com.ciberbus.repository.ReservaRepository;
import com.ciberbus.service.ReservaService;
import com.ciberbus.service.UsuarioService;
import com.ciberbus.service.ViajeService;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final DetalleReservaRepository detalleReservaRepository;
    private final PasajeroRepository pasajeroRepository;
    private final UsuarioService usuarioService;
    private final ViajeService viajeService;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              DetalleReservaRepository detalleReservaRepository,
                              PasajeroRepository pasajeroRepository,
                              UsuarioService usuarioService,
                              ViajeService viajeService) {
        this.reservaRepository = reservaRepository;
        this.detalleReservaRepository = detalleReservaRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.usuarioService = usuarioService;
        this.viajeService = viajeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorCodigo(String codigoReserva) {
        return reservaRepository.findByCodigoReservaWithDetails(codigoReserva);
    }

    @Override
    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarPorUsuario(Integer idUsuario) {
        return reservaRepository.findByUsuarioIdWithDetails(idUsuario);
    }

    @Override
    public Reserva crearReserva(Integer idUsuario, Integer idViaje, List<Integer> idAsientos, List<Pasajero> pasajeros, String metodoPago) {
        if (idAsientos == null || idAsientos.isEmpty()) {
            throw new NegocioException("Debe seleccionar al menos un asiento.");
        }
        if (idAsientos.size() > 5) {
            throw new NegocioException("No se puede reservar más de 5 asientos por reserva.");
        }
        if (new HashSet<>(idAsientos).size() != idAsientos.size()) {
            throw new NegocioException("No puede repetir un mismo asiento en la reserva.");
        }
        if (pasajeros == null || pasajeros.size() != idAsientos.size()) {
            throw new NegocioException("Debe registrar un pasajero por cada asiento seleccionado.");
        }

        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new NegocioException("Usuario no encontrado"));

        Viaje viaje = viajeService.buscarPorId(idViaje)
                .orElseThrow(() -> new NegocioException("Viaje no encontrado"));

        // Obtener asientos del viaje usando exclusivamente ViajeService (regla P5)
        Map<Integer, ViajeAsiento> asientosPorId = viajeService.listarAsientos(idViaje).stream()
                .collect(Collectors.toMap(ViajeAsiento::getIdViajeAsiento, a -> a));

        List<ViajeAsiento> asientosSeleccionados = new ArrayList<>();
        for (Integer idAsiento : idAsientos) {
            ViajeAsiento asiento = asientosPorId.get(idAsiento);
            if (asiento == null) {
                throw new NegocioException("Uno o más asientos seleccionados no son válidos para este viaje.");
            }
            asientosSeleccionados.add(asiento);
        }

        // Verificar que estén disponibles (Estado == 1)
        for (ViajeAsiento asiento : asientosSeleccionados) {
            if (asiento.getEstado() != 1) {
                throw new NegocioException("El asiento " + asiento.getNroAsiento() + " (Piso " + asiento.getPiso() + ") ya no está disponible.");
            }
        }

        // Calcular monto total
        BigDecimal tarifa = viaje.getTarifa() != null ? viaje.getTarifa() : BigDecimal.ZERO;
        BigDecimal montoTotal = tarifa.multiply(BigDecimal.valueOf(asientosSeleccionados.size()));

        // Crear Reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setViaje(viaje);
        reserva.setCodigoReserva("CB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setMontoTotal(montoTotal);
        reserva.setMetodoPago(metodoPago != null ? metodoPago : "TARJETA");
        reserva.setEstado("CONFIRMADA");

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // Un pasajero por asiento: se respeta el orden de idAsientos
        Map<String, Pasajero> pasajerosPorDocumento = new HashMap<>();
        for (int i = 0; i < asientosSeleccionados.size(); i++) {
            ViajeAsiento asiento = asientosSeleccionados.get(i);
            Pasajero datos = pasajeros.get(i);
            if (datos == null || datos.getNroDocumento() == null || datos.getNroDocumento().isBlank()) {
                throw new NegocioException("Faltan los datos del pasajero para el asiento " + asiento.getNroAsiento() + ".");
            }

            Pasajero pasajero = pasajerosPorDocumento.computeIfAbsent(datos.getNroDocumento(),
                    doc -> pasajeroRepository.findByNroDocumento(doc)
                            .orElseGet(() -> pasajeroRepository.save(datos)));

            DetalleReserva detalle = new DetalleReserva();
            detalle.setReserva(reservaGuardada);
            detalle.setViajeAsiento(asiento);
            detalle.setPasajero(pasajero);
            detalle.setPrecioPagado(tarifa);
            detalleReservaRepository.save(detalle);

            // Marcar asiento como ocupado (Estado = 0) y persistir
            asiento.setEstado(0);
            viajeService.actualizarAsiento(asiento);
        }

        return reservaGuardada;
    }
}
