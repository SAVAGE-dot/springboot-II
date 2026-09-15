package com.ciberbus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
}
