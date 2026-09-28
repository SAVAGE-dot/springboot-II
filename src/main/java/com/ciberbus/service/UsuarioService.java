package com.ciberbus.service;

import java.util.List;
import java.util.Optional;

import com.ciberbus.entity.Usuario;

/** P2 — seguridad y usuarios. El controller no habla con el repository. */
public interface UsuarioService {

    Optional<Usuario> buscarPorId(Integer id);

    Optional<Usuario> buscarPorCorreo(String correo);

    Optional<Usuario> buscarPorNroDocumento(String nroDocumento);

    List<Usuario> listar();

    Usuario guardar(Usuario usuario);

    /** Registra un usuario nuevo encriptando su clave con BCrypt. */
    Usuario registrar(Usuario usuario);

    /** Usuario genérico para compras sin sesión iniciada (invitado). */
    Usuario obtenerInvitado();
}