package com.ciberbus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ciberbus.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.usuario u " +
           "JOIN FETCH r.viaje v " +
           "JOIN FETCH v.ruta ru " +
           "JOIN FETCH ru.ciudadPartida cp " +
           "JOIN FETCH ru.ciudadLlegada cl " +
           "WHERE r.codigoReserva = :codigoReserva")
    Optional<Reserva> findByCodigoReservaWithDetails(@Param("codigoReserva") String codigoReserva);

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.usuario u " +
           "JOIN FETCH r.viaje v " +
           "JOIN FETCH v.ruta ru " +
           "JOIN FETCH ru.ciudadPartida cp " +
           "JOIN FETCH ru.ciudadLlegada cl " +
           "WHERE u.idUsuario = :idUsuario")
    List<Reserva> findByUsuarioIdWithDetails(@Param("idUsuario") Integer idUsuario);
}
