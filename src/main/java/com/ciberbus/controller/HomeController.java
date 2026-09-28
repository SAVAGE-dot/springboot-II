package com.ciberbus.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ciberbus.service.CatalogoService;
import com.ciberbus.service.UsuarioService;
import com.ciberbus.service.ViajeService;

@Controller
public class HomeController {

    private final CatalogoService catalogoService;
    private final ViajeService viajeService;
    private final UsuarioService usuarioService;

    public HomeController(CatalogoService catalogoService,
                          ViajeService viajeService,
                          UsuarioService usuarioService) {
        this.catalogoService = catalogoService;
        this.viajeService = viajeService;
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(@RequestParam(required = false) Integer origenId,
                         Model model,
                         Principal principal) {
        model.addAttribute("titulo", "CiberBus");
        model.addAttribute("totalCiudades", catalogoService.listarCiudades().size());
        model.addAttribute("origenes", viajeService.listarOrigenesDisponibles());
        model.addAttribute("destinos", viajeService.listarDestinosPorOrigen(origenId));
        model.addAttribute("origenId", origenId);
        if (principal != null) {
            usuarioService.buscarPorNroDocumento(principal.getName())
                    .ifPresent(u -> model.addAttribute("usuario", u));
        }
        return "inicio";
    }
}
