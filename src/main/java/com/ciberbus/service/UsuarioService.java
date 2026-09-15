package com.ciberbus.service;

import java.util.List;
import java.util.Optional;

import com.ciberbus.entity.Usuario;

/** P2 — seguridad y usuarios. El controller no habla con el repository. */
public interface UsuarioService {

    Optional<Usuario> buscarPorCorreo(String correo);

    List<Usuario> listar();

    Usuario guardar(Usuario usuario);
}
