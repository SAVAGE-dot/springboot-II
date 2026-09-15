package com.ciberbus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberbus.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);
}
