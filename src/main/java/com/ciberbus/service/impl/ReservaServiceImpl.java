package com.ciberbus.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.ciberbus.repository.UsuarioRepository;
import com.ciberbus.service.ReservaService;
import com.ciberbus.service.ViajeService;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final DetalleReservaRepository detalleReservaRepository;
    private final PasajeroRepository pasajeroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ViajeService viajeService;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              DetalleReservaRepository detalleReservaRepository,
                              PasajeroRepository pasajeroRepository,
                              UsuarioRepository usuarioRepository,
                              ViajeService viajeService) {
        this.reservaRepository = reservaRepository;
        this.detalleReservaRepository = detalleReservaRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.usuarioRepository = usuarioRepository;
        this.viajeService = viajeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorCodigo(String codigoReserva) {
        return reservaRepository.findByCodigoReserva(codigoReserva);
    }

    @Override
    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarPorUsuario(Integer idUsuario) {
        // Implement query or custom method if needed, or query by usuario
        return reservaRepository.findAll().stream()
                .filter(r -> r.getUsuario() != null && r.getUsuario().getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public Reserva crearReserva(Integer idUsuario, Integer idViaje, List<Integer> idAsientos, Pasajero pasajeroData, String metodoPago) {
        if (idAsientos == null || idAsientos.isEmpty()) {
            throw new NegocioException("Debe seleccionar al menos un asiento.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NegocioException("Usuario no encontrado"));

        Viaje viaje = viajeService.buscarPorId(idViaje)
                .orElseThrow(() -> new NegocioException("Viaje no encontrado"));

        // Obtener asientos del viaje usando exclusivamente ViajeService (regla P5)
        List<ViajeAsiento> asientosViaje = viajeService.listarAsientos(idViaje);

        List<ViajeAsiento> asientosSeleccionados = asientosViaje.stream()
                .filter(a -> idAsientos.contains(a.getIdViajeAsiento()))
                .toList();

        if (asientosSeleccionados.size() != idAsientos.size()) {
            throw new NegocioException("Uno o más asientos seleccionados no son válidos para este viaje.");
        }

        // Verificar que estén disponibles (Estado == 1)
        for (ViajeAsiento asiento : asientosSeleccionados) {
            if (asiento.getEstado() != 1) {
                throw new NegocioException("El asiento " + asiento.getNroAsiento() + " (Piso " + asiento.getPiso() + ") ya no está disponible.");
            }
        }

        // Guardar o actualizar Pasajero
        Pasajero pasajero = pasajeroRepository.findAll().stream()
                .filter(p -> p.getNroDocumento().equals(pasajeroData.getNroDocumento()))
                .findFirst()
                .orElseGet(() -> pasajeroRepository.save(pasajeroData));

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

        // Crear Detalles y actualizar estado de asientos (si aplica)
        for (ViajeAsiento asiento : asientosSeleccionados) {
            DetalleReserva detalle = new DetalleReserva();
            detalle.setReserva(reservaGuardada);
            detalle.setViajeAsiento(asiento);
            detalle.setPasajero(pasajero);
            detalle.setPrecioPagado(tarifa);
            detalleReservaRepository.save(detalle);

            // Marcar asiento como ocupado (Estado = 0)
            asiento.setEstado(0);
            // Nota: actualizamos el asiento a través de ViajeService/Repositorio si fuera necesario, o persistimos
        }

        return reservaGuardada;
    }
}
