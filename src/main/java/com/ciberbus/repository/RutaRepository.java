package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Ruta;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    @Query("""
            select distinct r.ciudadPartida
            from Ruta r
            where r.estado = 1
            order by r.ciudadPartida.ciudad
            """)
    List<Ciudad> listarOrigenesDisponibles();

    @Query("""
            select r
            from Ruta r
            where r.ciudadPartida.idCiudad = :idOrigen
              and r.estado = :estado
            order by r.ciudadLlegada.ciudad
            """)
    List<Ruta> listarDestinosPorOrigen(
            @Param("idOrigen") Integer idOrigen,
            @Param("estado") Integer estado);
}
