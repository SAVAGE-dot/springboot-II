package com.ciberbus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ciberbus.service.CatalogoService;
import com.ciberbus.service.ViajeService;

@Controller
public class HomeController {

    private final CatalogoService catalogoService;
    private final ViajeService viajeService;

    public HomeController(CatalogoService catalogoService, ViajeService viajeService) {
        this.catalogoService = catalogoService;
        this.viajeService = viajeService;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(@RequestParam(required = false) Integer origenId, Model model) {
        model.addAttribute("titulo", "CiberBus");
        model.addAttribute("totalCiudades", catalogoService.listarCiudades().size());
        model.addAttribute("origenes", viajeService.listarOrigenesDisponibles());
        model.addAttribute("destinos", viajeService.listarDestinosPorOrigen(origenId));
        model.addAttribute("origenId", origenId);
        return "inicio";
    }
}
