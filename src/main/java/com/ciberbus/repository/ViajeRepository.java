package com.ciberbus.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ciberbus.entity.Viaje;

public interface ViajeRepository extends JpaRepository<Viaje, Integer> {

    @Query("""
            select v
            from Viaje v
            join fetch v.ruta r
            join fetch r.ciudadPartida
            join fetch r.ciudadLlegada
            join fetch v.bus
            where v.idViaje = :idViaje
            """)
    java.util.Optional<Viaje> buscarDetallePorId(@Param("idViaje") Integer idViaje);

    @Query("""
            select distinct v
            from Viaje v
            join fetch v.ruta r
            join fetch r.ciudadPartida
            join fetch r.ciudadLlegada
            join fetch v.bus
            join fetch v.conductor
            order by v.fechaSalida, v.horaSalida
            """)
    List<Viaje> listarConDetalle();

    @Query("""
            select v
            from Viaje v
            join fetch v.ruta r
            join fetch r.ciudadPartida
            join fetch r.ciudadLlegada
            join fetch v.bus
            where r.ciudadPartida.idCiudad = :idOrigen
              and r.ciudadLlegada.idCiudad = :idDestino
              and v.fechaSalida = :fecha
              and v.estado = 1
            order by v.horaSalida
            """)
    List<Viaje> buscarPorOrigenDestinoFecha(
            @Param("idOrigen") Integer idOrigen,
            @Param("idDestino") Integer idDestino,
            @Param("fecha") LocalDate fecha);

    long countByRuta_IdRutaAndEstado(Integer idRuta, Integer estado);

    long countByBus_IdBusAndEstado(Integer idBus, Integer estado);

    long countByConductor_IdConductorAndEstado(Integer idConductor, Integer estado);

    boolean existsByCodigoViaje(String codigoViaje);

    boolean existsByBus_IdBusAndFechaSalidaAndHoraSalida(
            Integer idBus, LocalDate fechaSalida, java.time.LocalTime horaSalida);

    boolean existsByConductor_IdConductorAndFechaSalidaAndHoraSalida(
            Integer idConductor, LocalDate fechaSalida, java.time.LocalTime horaSalida);

    @Query("""
            select v
            from Viaje v
            join fetch v.ruta r
            join fetch r.ciudadPartida
            join fetch r.ciudadLlegada
            join fetch v.bus
            where r.idRuta = :rutaId
              and v.fechaSalida = :fecha
              and v.estado = 1
            order by v.horaSalida
            """)
    List<Viaje> buscarDisponiblesPorRutaYFecha(
            @Param("rutaId") Integer rutaId,
            @Param("fecha") LocalDate fecha);
}
