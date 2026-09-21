package com.ciberbus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Bus;

public interface BusRepository extends JpaRepository<Bus, Integer> {

    List<Bus> findByEstado(Integer estado);

    boolean existsByNroPlaca(String nroPlaca);
}
