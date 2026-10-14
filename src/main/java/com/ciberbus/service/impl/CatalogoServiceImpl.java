package com.ciberbus.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Bus;
import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Conductor;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Turno;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.repository.BusRepository;
import com.ciberbus.repository.CiudadRepository;
import com.ciberbus.repository.ConductorRepository;
import com.ciberbus.repository.RutaRepository;
import com.ciberbus.repository.TurnoRepository;
import com.ciberbus.repository.ViajeRepository;
import com.ciberbus.service.CatalogoService;

@Service
@Transactional
public class CatalogoServiceImpl implements CatalogoService {

    private final CiudadRepository ciudadRepository;
    private final TurnoRepository turnoRepository;
    private final BusRepository busRepository;
    private final ConductorRepository conductorRepository;
    private final RutaRepository rutaRepository;
    private final ViajeRepository viajeRepository;

    public CatalogoServiceImpl(
            CiudadRepository ciudadRepository,
            TurnoRepository turnoRepository,
            BusRepository busRepository,
            ConductorRepository conductorRepository,
            RutaRepository rutaRepository,
            ViajeRepository viajeRepository) {
        this.ciudadRepository = ciudadRepository;
        this.turnoRepository = turnoRepository;
        this.busRepository = busRepository;
        this.conductorRepository = conductorRepository;
        this.rutaRepository = rutaRepository;
        this.viajeRepository = viajeRepository;
    }

    // --- Ciudad ---

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
        ciudad.setCiudad(normalizar(ciudad.getCiudad()));
        ciudad.setDepartamento(normalizar(ciudad.getDepartamento()));
        requerido(ciudad.getCiudad(), "El nombre de la ciudad es obligatorio.");
        requerido(ciudad.getDepartamento(), "El departamento es obligatorio.");
        return ciudadRepository.save(ciudad);
    }

    @Override
    public void eliminarCiudad(Integer id) {
        Ciudad ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La ciudad no existe."));
        long rutas = rutaRepository.countByCiudadPartida_IdCiudadAndEstado(id, 1)
                + rutaRepository.countByCiudadLlegada_IdCiudadAndEstado(id, 1);
        if (rutas > 0) {
            throw new NegocioException(
                    "La ciudad no se puede dar de baja porque está en " + rutas + " ruta(s) activa(s).");
        }
        ciudad.setEstado(0);
        ciudadRepository.save(ciudad);
    }

    // --- Turno ---

    @Override
    @Transactional(readOnly = true)
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Turno> buscarTurno(Integer id) {
        return turnoRepository.findById(id);
    }

    @Override
    public Turno guardarTurno(Turno turno) {
        turno.setNombre(normalizar(turno.getNombre()));
        requerido(turno.getNombre(), "El nombre del turno es obligatorio.");
        if (turno.getHoraInicio() == null) {
            throw new NegocioException("La hora de inicio del turno es obligatoria.");
        }
        if (turno.getHoraFin() == null) {
            throw new NegocioException("La hora de fin del turno es obligatoria.");
        }
        if (!turno.getHoraFin().isAfter(turno.getHoraInicio())) {
            throw new NegocioException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        return turnoRepository.save(turno);
    }

    @Override
    public void eliminarTurno(Integer id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El turno no existe."));
        long conductores = conductorRepository.countByTurno_IdTurnoAndEstado(id, 1);
        if (conductores > 0) {
            throw new NegocioException(
                    "El turno no se puede dar de baja porque está asignado a " + conductores + " conductor(es) activo(s).");
        }
        turno.setEstado(0);
        turnoRepository.save(turno);
    }

    // --- Bus ---

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
        bus.setNroPlaca(normalizar(bus.getNroPlaca()));
        bus.setMarca(normalizar(bus.getMarca()));
        bus.setTipoBus(normalizar(bus.getTipoBus()));
        requerido(bus.getNroPlaca(), "La placa del bus es obligatoria.");
        requerido(bus.getMarca(), "La marca del bus es obligatoria.");
        requerido(bus.getTipoBus(), "El tipo de bus es obligatorio.");
        if (bus.getCantidadPisos() == null || bus.getCantidadPisos() < 1) {
            throw new NegocioException("La cantidad de pisos debe ser al menos 1.");
        }
        if (bus.getNroAsientos() == null || bus.getNroAsientos() < 1) {
            throw new NegocioException("La cantidad de asientos debe ser al menos 1.");
        }
        boolean placaRepetida = bus.getIdBus() == null
                ? busRepository.existsByNroPlaca(bus.getNroPlaca())
                : busRepository.existsByNroPlacaAndIdBusNot(bus.getNroPlaca(), bus.getIdBus());
        if (placaRepetida) {
            throw new NegocioException("Ya existe un bus registrado con la placa " + bus.getNroPlaca() + ".");
        }
        return busRepository.save(bus);
    }

    @Override
    public void eliminarBus(Integer id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El bus no existe."));
        long viajes = viajeRepository.countByBus_IdBusAndEstado(id, 1);
        if (viajes > 0) {
            throw new NegocioException(
                    "El bus no se puede dar de baja porque tiene " + viajes + " viaje(s) activo(s).");
        }
        bus.setEstado(0);
        busRepository.save(bus);
    }

    // --- Conductor ---

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
        conductor.setTipoDocumento(normalizar(conductor.getTipoDocumento()));
        conductor.setNroDocumento(normalizar(conductor.getNroDocumento()));
        conductor.setNombre(normalizar(conductor.getNombre()));
        conductor.setApellido(normalizar(conductor.getApellido()));
        conductor.setCorreo(normalizar(conductor.getCorreo()));
        conductor.setTelefono(normalizar(conductor.getTelefono()));
        conductor.setCategoriaLicencia(normalizar(conductor.getCategoriaLicencia()));
        conductor.setDiaDescanso(normalizar(conductor.getDiaDescanso()));
        if (conductor.getCorreo() != null && conductor.getCorreo().isBlank()) {
            conductor.setCorreo(null);
        }

        requerido(conductor.getTipoDocumento(), "El tipo de documento del conductor es obligatorio.");
        requerido(conductor.getNroDocumento(), "El número de documento del conductor es obligatorio.");
        requerido(conductor.getNombre(), "El nombre del conductor es obligatorio.");
        requerido(conductor.getApellido(), "El apellido del conductor es obligatorio.");
        requerido(conductor.getCategoriaLicencia(), "La categoría de licencia es obligatoria.");
        if (conductor.getVencimientoLicencia() == null) {
            throw new NegocioException("El vencimiento de la licencia es obligatorio.");
        }

        Integer idTurno = conductor.getTurno() == null ? null : conductor.getTurno().getIdTurno();
        if (idTurno == null) {
            throw new NegocioException("El turno del conductor es obligatorio.");
        }
        conductor.setTurno(turnoRepository.findById(idTurno)
                .orElseThrow(() -> new NegocioException("El turno seleccionado no existe.")));
        if (Integer.valueOf(0).equals(conductor.getTurno().getEstado())) {
            throw new NegocioException("El turno seleccionado está inactivo.");
        }

        boolean documentoRepetido = conductor.getIdConductor() == null
                ? conductorRepository.existsByNroDocumento(conductor.getNroDocumento())
                : conductorRepository.existsByNroDocumentoAndIdConductorNot(
                        conductor.getNroDocumento(), conductor.getIdConductor());
        if (documentoRepetido) {
            throw new NegocioException("Ya existe un conductor con el documento " + conductor.getNroDocumento() + ".");
        }

        if (conductor.getCorreo() != null) {
            boolean correoRepetido = conductor.getIdConductor() == null
                    ? conductorRepository.existsByCorreo(conductor.getCorreo())
                    : conductorRepository.existsByCorreoAndIdConductorNot(
                            conductor.getCorreo(), conductor.getIdConductor());
            if (correoRepetido) {
                throw new NegocioException("Ya existe un conductor registrado con el correo " + conductor.getCorreo() + ".");
            }
        }
        return conductorRepository.save(conductor);
    }

    @Override
    public void eliminarConductor(Integer id) {
        Conductor conductor = conductorRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El conductor no existe."));
        long viajes = viajeRepository.countByConductor_IdConductorAndEstado(id, 1);
        if (viajes > 0) {
            throw new NegocioException(
                    "El conductor no se puede dar de baja porque tiene " + viajes + " viaje(s) activo(s).");
        }
        conductor.setEstado(0);
        conductorRepository.save(conductor);
    }

    // --- Ruta ---

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> listarRutas() {
        return rutaRepository.listarConCiudades();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ruta> buscarRuta(Integer id) {
        return rutaRepository.findById(id);
    }

    @Override
    public Ruta guardarRuta(Ruta ruta) {
        Integer idPartida = ruta.getCiudadPartida() == null ? null : ruta.getCiudadPartida().getIdCiudad();
        Integer idLlegada = ruta.getCiudadLlegada() == null ? null : ruta.getCiudadLlegada().getIdCiudad();
        if (idPartida == null) {
            throw new NegocioException("La ciudad de partida es obligatoria.");
        }
        if (idLlegada == null) {
            throw new NegocioException("La ciudad de llegada es obligatoria.");
        }
        if (idPartida.equals(idLlegada)) {
            throw new NegocioException("La ciudad de partida y la de llegada no pueden ser iguales.");
        }
        ruta.setCiudadPartida(buscarCiudad(idPartida)
                .orElseThrow(() -> new NegocioException("La ciudad de partida no existe.")));
        ruta.setCiudadLlegada(buscarCiudad(idLlegada)
                .orElseThrow(() -> new NegocioException("La ciudad de llegada no existe.")));
        if (Integer.valueOf(0).equals(ruta.getCiudadPartida().getEstado())
                || Integer.valueOf(0).equals(ruta.getCiudadLlegada().getEstado())) {
            throw new NegocioException("No se puede usar una ciudad inactiva en la ruta.");
        }

        if (ruta.getHorasEstimadas() == null) {
            throw new NegocioException("Las horas estimadas de la ruta son obligatorias.");
        }
        if (ruta.getHorasEstimadas().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("Las horas estimadas deben ser mayores a cero.");
        }
        return rutaRepository.save(ruta);
    }

    @Override
    public void eliminarRuta(Integer id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La ruta no existe."));
        long viajes = viajeRepository.countByRuta_IdRutaAndEstado(id, 1);
        if (viajes > 0) {
            throw new NegocioException(
                    "La ruta no se puede dar de baja porque tiene " + viajes + " viaje(s) activo(s).");
        }
        ruta.setEstado(0);
        rutaRepository.save(ruta);
    }

    // --- P3: consulta filtrada por estado ---

    @Override
    @Transactional(readOnly = true)
    public List<Ciudad> listarCiudadesPorEstado(Integer estado) {
        return estado == null ? ciudadRepository.findAll() : ciudadRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turno> listarTurnosPorEstado(Integer estado) {
        return estado == null ? turnoRepository.findAll() : turnoRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> listarBusesPorEstado(Integer estado) {
        return estado == null ? busRepository.findAll() : busRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarConductoresPorEstado(Integer estado) {
        return estado == null ? conductorRepository.findAll() : conductorRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> listarRutasPorEstado(Integer estado) {
        return estado == null ? rutaRepository.findAll() : rutaRepository.findByEstado(estado);
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private static void requerido(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new NegocioException(mensaje);
        }
    }
}
