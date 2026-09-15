package com.ciberbus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Integer> {
}
