package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ciberbus.entity.DetalleReserva;

public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Integer> {

    @Query("""
            select d
            from DetalleReserva d
            join fetch d.pasajero
            join fetch d.viajeAsiento
            where d.reserva.idReserva = :idReserva
            order by d.viajeAsiento.nroAsiento
            """)
    List<DetalleReserva> listarPorReserva(@Param("idReserva") Integer idReserva);
}
