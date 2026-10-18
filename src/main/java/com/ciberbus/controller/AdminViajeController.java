package com.ciberbus.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ciberbus.entity.Bus;
import com.ciberbus.entity.Conductor;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Viaje;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.service.CatalogoService;
import com.ciberbus.service.ViajeService;

/** P4 — el admin registra viajes. Al crear uno nuevo se generan los asientos del bus. */
@Controller
@RequestMapping("/admin/viajes")
public class AdminViajeController {

    private final ViajeService viajeService;
    private final CatalogoService catalogoService;

    public AdminViajeController(ViajeService viajeService, CatalogoService catalogoService) {
        this.viajeService = viajeService;
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("viajes", viajeService.listar());
        model.addAttribute("rutas", catalogoService.listarRutas());
        model.addAttribute("buses", catalogoService.listarBuses());
        model.addAttribute("conductores", catalogoService.listarConductores());
        return "admin-viajes";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) String codigoViaje,
                          @RequestParam Integer idRuta,
                          @RequestParam Integer idBus,
                          @RequestParam Integer idConductor,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaSalida,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLlegada,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaLlegada,
                          @RequestParam BigDecimal tarifa,
                          @RequestParam(defaultValue = "1") Integer estado,
                          RedirectAttributes ra) {
        try {
            Ruta ruta = catalogoService.buscarRuta(idRuta)
                    .orElseThrow(() -> new NegocioException("La ruta no existe."));
            Bus bus = catalogoService.buscarBus(idBus)
                    .orElseThrow(() -> new NegocioException("El bus no existe."));
            Conductor conductor = catalogoService.buscarConductor(idConductor)
                    .orElseThrow(() -> new NegocioException("El conductor no existe."));

            Viaje viaje = new Viaje();
            viaje.setCodigoViaje(codigoViaje == null ? "" : codigoViaje.trim());
            viaje.setRuta(ruta);
            viaje.setBus(bus);
            viaje.setConductor(conductor);
            viaje.setFechaSalida(fechaSalida);
            viaje.setHoraSalida(horaSalida);
            viaje.setFechaLlegada(fechaLlegada);
            viaje.setHoraLlegada(horaLlegada);
            viaje.setTarifa(tarifa);
            viaje.setEstado(estado);

            viajeService.guardarConAsientos(viaje);
            ra.addFlashAttribute("ok", "Viaje guardado. Se pueden crear varios horarios para la misma ruta y fecha.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/viajes";
    }
}
