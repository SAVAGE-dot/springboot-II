package com.ciberbus.controller;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;
import com.ciberbus.service.ViajeService;

@Controller
public class ViajeController {

    private final ViajeService viajeService;

    public ViajeController(ViajeService viajeService) {
        this.viajeService = viajeService;
    }

    @GetMapping("/viajes/buscar")
    public String buscar(
            @RequestParam Integer rutaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {
        model.addAttribute("viajes", viajeService.buscarDisponiblesPorRutaYFecha(rutaId, fecha));
        model.addAttribute("fecha", fecha);
        return "viajes";
    }

    @GetMapping("/viajes/{idViaje}/asientos")
    public String seleccionarAsientos(
            @PathVariable Integer idViaje,
            @RequestParam(required = false) Integer rutaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String asientoIds,
            Model model) {
        List<Integer> idsSeleccionados = parsearIds(asientoIds);
        if (!idsSeleccionados.isEmpty()) {
            idsSeleccionados = viajeService.validarAsientosSeleccionados(idViaje, idsSeleccionados).stream()
                    .map(ViajeAsiento::getIdViajeAsiento)
                    .toList();
        }
        model.addAttribute("viaje", viajeService.buscarDetallePorId(idViaje));
        model.addAttribute("asientos", viajeService.listarAsientos(idViaje));
        model.addAttribute("asientosSeleccionados", idsSeleccionados);
        model.addAttribute("rutaId", rutaId);
        model.addAttribute("fecha", fecha);
        return "asientos";
    }

    @PostMapping("/viajes/resumen")
    public String resumen(
            @RequestParam Integer idViaje,
            @RequestParam String asientoIds,
            @RequestParam(required = false) Integer rutaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {
        List<Integer> ids = parsearIds(asientoIds);
        Viaje viaje = viajeService.buscarDetallePorId(idViaje);
        List<ViajeAsiento> asientos = viajeService.validarAsientosSeleccionados(idViaje, ids);
        BigDecimal total = viaje.getTarifa().multiply(BigDecimal.valueOf(asientos.size()));

        model.addAttribute("viaje", viaje);
        model.addAttribute("asientos", asientos);
        model.addAttribute("total", total);
        model.addAttribute("asientoIds", asientos.stream()
                .map(asiento -> String.valueOf(asiento.getIdViajeAsiento()))
                .collect(Collectors.joining(",")));
        model.addAttribute("rutaId", rutaId);
        model.addAttribute("fecha", fecha);
        return "resumen";
    }

    private List<Integer> parsearIds(String asientoIds) {
        if (asientoIds == null || asientoIds.isBlank()) {
            return List.of();
        }
        return Arrays.stream(asientoIds.split(","))
                .filter(valor -> !valor.isBlank())
                .map(Integer::valueOf)
                .toList();
    }
}
