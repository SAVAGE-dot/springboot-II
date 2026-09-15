package com.ciberbus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ciberbus.service.CatalogoService;

@Controller
public class HomeController {

    private final CatalogoService catalogoService;

    public HomeController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("titulo", "CiberBus");
        model.addAttribute("totalCiudades", catalogoService.listarCiudades().size());
        return "inicio";
    }
}
