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

    List<Viaje> findByRuta_CiudadPartida_IdCiudadAndRuta_CiudadLlegada_IdCiudadAndFechaSalida(
            Integer idOrigen, Integer idDestino, LocalDate fechaSalida);

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
