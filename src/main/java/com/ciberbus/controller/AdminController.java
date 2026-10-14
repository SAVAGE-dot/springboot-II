package com.ciberbus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ciberbus.service.CatalogoService;
import com.ciberbus.service.ReservaService;
import com.ciberbus.service.ViajeService;

/** P3 / P6 — menú principal del administrador. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CatalogoService catalogoService;
    private final ViajeService viajeService;
    private final ReservaService reservaService;

    public AdminController(CatalogoService catalogoService,
                           ViajeService viajeService,
                           ReservaService reservaService) {
        this.catalogoService = catalogoService;
        this.viajeService = viajeService;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("totalCiudades", catalogoService.listarCiudades().size());
        model.addAttribute("totalTurnos", catalogoService.listarTurnos().size());
        model.addAttribute("totalBuses", catalogoService.listarBuses().size());
        model.addAttribute("totalConductores", catalogoService.listarConductores().size());
        model.addAttribute("totalRutas", catalogoService.listarRutas().size());
        model.addAttribute("totalViajes", viajeService.listar().size());
        model.addAttribute("totalReservas", reservaService.listarTodas().size());
        return "admin";
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaService.listarTodas());
        return "admin-reservas";
    }
}
