package com.ciberbus.controller;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ciberbus.entity.Usuario;
import com.ciberbus.service.UsuarioService;

/**
 * Pone el usuario logueado en todas las pantallas (menú del encabezado).
 */
@ControllerAdvice
public class UsuarioModelAdvice {

    private final UsuarioService usuarioService;

    public UsuarioModelAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void agregarUsuario(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("usuario", null);
            return;
        }
        Usuario usuario = usuarioService.buscarPorNroDocumento(principal.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
    }
}
