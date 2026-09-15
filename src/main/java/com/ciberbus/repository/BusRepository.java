package com.ciberbus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Bus;

public interface BusRepository extends JpaRepository<Bus, Integer> {
}
