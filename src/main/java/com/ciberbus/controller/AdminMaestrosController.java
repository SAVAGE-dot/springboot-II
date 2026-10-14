package com.ciberbus.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ciberbus.entity.Bus;
import com.ciberbus.entity.Ciudad;
import com.ciberbus.entity.Conductor;
import com.ciberbus.entity.Ruta;
import com.ciberbus.entity.Turno;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.service.CatalogoService;

/**
 * P3 (Ospina) — Panel /admin de los maestros.
 * Rutas protegidas por SecurityConfig con role ADMIN.
 */
@Controller
@RequestMapping("/admin")
public class AdminMaestrosController {

    private final CatalogoService catalogoService;

    public AdminMaestrosController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public String panel() {
        return "redirect:/admin/ciudades";
    }

    // ===== Ciudad =====

    @GetMapping("/ciudades")
    public String listarCiudades(@RequestParam(required = false) Integer estado, Model model) {
        return vistaCiudades(model, estado, new Ciudad(), "Nueva ciudad", null);
    }

    @GetMapping("/ciudades/{id}")
    public String editarCiudad(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Ciudad> ciudad = catalogoService.buscarCiudad(id);
        if (ciudad.isEmpty()) {
            flash.addFlashAttribute("error", "La ciudad no existe.");
            return "redirect:/admin/ciudades" + sufijoEstado(estado);
        }
        return vistaCiudades(model, estado, ciudad.get(), "Editar ciudad", null);
    }

    @PostMapping("/ciudades")
    public String guardarCiudad(
            @RequestParam(required = false) Integer id,
            @RequestParam String ciudad,
            @RequestParam String departamento,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Ciudad> existente = id == null ? Optional.empty() : catalogoService.buscarCiudad(id);
        if (id != null && existente.isEmpty()) {
            flash.addFlashAttribute("error", "La ciudad no existe.");
            return "redirect:/admin/ciudades" + sufijoEstado(estado);
        }
        Ciudad entidad = existente.orElseGet(Ciudad::new);
        entidad.setCiudad(ciudad);
        entidad.setDepartamento(departamento);
        String titulo = id == null ? "Nueva ciudad" : "Editar ciudad";
        try {
            catalogoService.guardarCiudad(entidad);
        } catch (NegocioException e) {
            return vistaCiudades(model, estado, entidad, titulo, e.getMessage());
        }
        flash.addFlashAttribute("ok", "Ciudad guardada correctamente.");
        return "redirect:/admin/ciudades" + sufijoEstado(estado);
    }

    @PostMapping("/ciudades/{id}/eliminar")
    public String eliminarCiudad(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            RedirectAttributes flash) {
        try {
            catalogoService.eliminarCiudad(id);
            flash.addFlashAttribute("ok", "Ciudad dada de baja correctamente.");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/ciudades" + sufijoEstado(estado);
    }

    private String vistaCiudades(Model model, Integer estado, Ciudad entidad,
            String tituloForm, String error) {
        model.addAttribute("ciudades", catalogoService.listarCiudadesPorEstado(estado));
        model.addAttribute("entidad", entidad);
        model.addAttribute("tituloForm", tituloForm);
        model.addAttribute("estado", estado);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin-ciudades";
    }

    // ===== Turno =====

    @GetMapping("/turnos")
    public String listarTurnos(@RequestParam(required = false) Integer estado, Model model) {
        return vistaTurnos(model, estado, new Turno(), "Nuevo turno", null);
    }

    @GetMapping("/turnos/{id}")
    public String editarTurno(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Turno> turno = catalogoService.buscarTurno(id);
        if (turno.isEmpty()) {
            flash.addFlashAttribute("error", "El turno no existe.");
            return "redirect:/admin/turnos" + sufijoEstado(estado);
        }
        return vistaTurnos(model, estado, turno.get(), "Editar turno", null);
    }

    @PostMapping("/turnos")
    public String guardarTurno(
            @RequestParam(required = false) Integer id,
            @RequestParam String nombre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Turno> existente = id == null ? Optional.empty() : catalogoService.buscarTurno(id);
        if (id != null && existente.isEmpty()) {
            flash.addFlashAttribute("error", "El turno no existe.");
            return "redirect:/admin/turnos" + sufijoEstado(estado);
        }
        Turno entidad = existente.orElseGet(Turno::new);
        entidad.setNombre(nombre);
        entidad.setHoraInicio(horaInicio);
        entidad.setHoraFin(horaFin);
        String titulo = id == null ? "Nuevo turno" : "Editar turno";
        try {
            catalogoService.guardarTurno(entidad);
        } catch (NegocioException e) {
            return vistaTurnos(model, estado, entidad, titulo, e.getMessage());
        }
        flash.addFlashAttribute("ok", "Turno guardado correctamente.");
        return "redirect:/admin/turnos" + sufijoEstado(estado);
    }

    @PostMapping("/turnos/{id}/eliminar")
    public String eliminarTurno(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            RedirectAttributes flash) {
        try {
            catalogoService.eliminarTurno(id);
            flash.addFlashAttribute("ok", "Turno dado de baja correctamente.");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/turnos" + sufijoEstado(estado);
    }

    private String vistaTurnos(Model model, Integer estado, Turno entidad,
            String tituloForm, String error) {
        model.addAttribute("turnos", catalogoService.listarTurnosPorEstado(estado));
        model.addAttribute("entidad", entidad);
        model.addAttribute("tituloForm", tituloForm);
        model.addAttribute("estado", estado);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin-turnos";
    }

    // ===== Bus =====

    @GetMapping("/buses")
    public String listarBuses(@RequestParam(required = false) Integer estado, Model model) {
        return vistaBuses(model, estado, new Bus(), "Nuevo bus", null);
    }

    @GetMapping("/buses/{id}")
    public String editarBus(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Bus> bus = catalogoService.buscarBus(id);
        if (bus.isEmpty()) {
            flash.addFlashAttribute("error", "El bus no existe.");
            return "redirect:/admin/buses" + sufijoEstado(estado);
        }
        return vistaBuses(model, estado, bus.get(), "Editar bus", null);
    }

    @PostMapping("/buses")
    public String guardarBus(
            @RequestParam(required = false) Integer id,
            @RequestParam String nroPlaca,
            @RequestParam String marca,
            @RequestParam String tipoBus,
            @RequestParam Integer cantidadPisos,
            @RequestParam Integer nroAsientos,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Bus> existente = id == null ? Optional.empty() : catalogoService.buscarBus(id);
        if (id != null && existente.isEmpty()) {
            flash.addFlashAttribute("error", "El bus no existe.");
            return "redirect:/admin/buses" + sufijoEstado(estado);
        }
        Bus entidad = existente.orElseGet(Bus::new);
        entidad.setNroPlaca(nroPlaca);
        entidad.setMarca(marca);
        entidad.setTipoBus(tipoBus);
        entidad.setCantidadPisos(cantidadPisos);
        entidad.setNroAsientos(nroAsientos);
        String titulo = id == null ? "Nuevo bus" : "Editar bus";
        try {
            catalogoService.guardarBus(entidad);
        } catch (NegocioException e) {
            return vistaBuses(model, estado, entidad, titulo, e.getMessage());
        }
        flash.addFlashAttribute("ok", "Bus guardado correctamente.");
        return "redirect:/admin/buses" + sufijoEstado(estado);
    }

    @PostMapping("/buses/{id}/eliminar")
    public String eliminarBus(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            RedirectAttributes flash) {
        try {
            catalogoService.eliminarBus(id);
            flash.addFlashAttribute("ok", "Bus dado de baja correctamente.");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/buses" + sufijoEstado(estado);
    }

    private String vistaBuses(Model model, Integer estado, Bus entidad,
            String tituloForm, String error) {
        model.addAttribute("buses", catalogoService.listarBusesPorEstado(estado));
        model.addAttribute("entidad", entidad);
        model.addAttribute("tituloForm", tituloForm);
        model.addAttribute("estado", estado);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin-buses";
    }

    // ===== Conductor =====

    @GetMapping("/conductores")
    public String listarConductores(@RequestParam(required = false) Integer estado, Model model) {
        return vistaConductores(model, estado, new Conductor(), "Nuevo conductor", null);
    }

    @GetMapping("/conductores/{id}")
    public String editarConductor(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Conductor> conductor = catalogoService.buscarConductor(id);
        if (conductor.isEmpty()) {
            flash.addFlashAttribute("error", "El conductor no existe.");
            return "redirect:/admin/conductores" + sufijoEstado(estado);
        }
        return vistaConductores(model, estado, conductor.get(), "Editar conductor", null);
    }

    @PostMapping("/conductores")
    public String guardarConductor(
            @RequestParam(required = false) Integer id,
            @RequestParam String tipoDocumento,
            @RequestParam String nroDocumento,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam String categoriaLicencia,
            @RequestParam(required = false) String diaDescanso,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimientoLicencia,
            @RequestParam(required = false) Integer idTurno,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Conductor> existente = id == null ? Optional.empty() : catalogoService.buscarConductor(id);
        if (id != null && existente.isEmpty()) {
            flash.addFlashAttribute("error", "El conductor no existe.");
            return "redirect:/admin/conductores" + sufijoEstado(estado);
        }
        Conductor entidad = existente.orElseGet(Conductor::new);
        entidad.setTipoDocumento(tipoDocumento);
        entidad.setNroDocumento(nroDocumento);
        entidad.setNombre(nombre);
        entidad.setApellido(apellido);
        entidad.setCorreo(correo);
        entidad.setTelefono(telefono);
        entidad.setCategoriaLicencia(categoriaLicencia);
        entidad.setDiaDescanso(diaDescanso);
        entidad.setVencimientoLicencia(vencimientoLicencia);
        Turno turno = new Turno();
        turno.setIdTurno(idTurno);
        entidad.setTurno(turno);
        String titulo = id == null ? "Nuevo conductor" : "Editar conductor";
        try {
            catalogoService.guardarConductor(entidad);
        } catch (NegocioException e) {
            return vistaConductores(model, estado, entidad, titulo, e.getMessage());
        }
        flash.addFlashAttribute("ok", "Conductor guardado correctamente.");
        return "redirect:/admin/conductores" + sufijoEstado(estado);
    }

    @PostMapping("/conductores/{id}/eliminar")
    public String eliminarConductor(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            RedirectAttributes flash) {
        try {
            catalogoService.eliminarConductor(id);
            flash.addFlashAttribute("ok", "Conductor dado de baja correctamente.");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/conductores" + sufijoEstado(estado);
    }

    private String vistaConductores(Model model, Integer estado, Conductor entidad,
            String tituloForm, String error) {
        model.addAttribute("conductores", catalogoService.listarConductoresPorEstado(estado));
        model.addAttribute("turnos", catalogoService.listarTurnosPorEstado(null));
        model.addAttribute("entidad", entidad);
        model.addAttribute("tituloForm", tituloForm);
        model.addAttribute("estado", estado);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin-conductores";
    }

    // ===== Ruta =====

    @GetMapping("/rutas")
    public String listarRutas(@RequestParam(required = false) Integer estado, Model model) {
        return vistaRutas(model, estado, new Ruta(), "Nueva ruta", null);
    }

    @GetMapping("/rutas/{id}")
    public String editarRuta(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Ruta> ruta = catalogoService.buscarRuta(id);
        if (ruta.isEmpty()) {
            flash.addFlashAttribute("error", "La ruta no existe.");
            return "redirect:/admin/rutas" + sufijoEstado(estado);
        }
        return vistaRutas(model, estado, ruta.get(), "Editar ruta", null);
    }

    @PostMapping("/rutas")
    public String guardarRuta(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer idCiudadPartida,
            @RequestParam(required = false) Integer idCiudadLlegada,
            @RequestParam(required = false) BigDecimal horasEstimadas,
            @RequestParam(required = false) Integer estado,
            Model model, RedirectAttributes flash) {
        Optional<Ruta> existente = id == null ? Optional.empty() : catalogoService.buscarRuta(id);
        if (id != null && existente.isEmpty()) {
            flash.addFlashAttribute("error", "La ruta no existe.");
            return "redirect:/admin/rutas" + sufijoEstado(estado);
        }
        Ruta entidad = existente.orElseGet(Ruta::new);
        entidad.setCiudadPartida(conCiudad(idCiudadPartida));
        entidad.setCiudadLlegada(conCiudad(idCiudadLlegada));
        entidad.setHorasEstimadas(horasEstimadas);
        String titulo = id == null ? "Nueva ruta" : "Editar ruta";
        try {
            catalogoService.guardarRuta(entidad);
        } catch (NegocioException e) {
            return vistaRutas(model, estado, entidad, titulo, e.getMessage());
        }
        flash.addFlashAttribute("ok", "Ruta guardada correctamente.");
        return "redirect:/admin/rutas" + sufijoEstado(estado);
    }

    @PostMapping("/rutas/{id}/eliminar")
    public String eliminarRuta(@PathVariable Integer id,
            @RequestParam(required = false) Integer estado,
            RedirectAttributes flash) {
        try {
            catalogoService.eliminarRuta(id);
            flash.addFlashAttribute("ok", "Ruta dada de baja correctamente.");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/rutas" + sufijoEstado(estado);
    }

    private String vistaRutas(Model model, Integer estado, Ruta entidad,
            String tituloForm, String error) {
        model.addAttribute("rutas", catalogoService.listarRutasPorEstado(estado));
        model.addAttribute("ciudades", catalogoService.listarCiudadesPorEstado(null));
        model.addAttribute("entidad", entidad);
        model.addAttribute("tituloForm", tituloForm);
        model.addAttribute("estado", estado);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin-rutas";
    }

    /** La ciudad llega solo con el id del formulario; el service la resuelve en la base. */
    private static Ciudad conCiudad(Integer idCiudad) {
        if (idCiudad == null) {
            return null;
        }
        Ciudad ciudad = new Ciudad();
        ciudad.setIdCiudad(idCiudad);
        return ciudad;
    }

    private static String sufijoEstado(Integer estado) {
        return estado == null ? "" : "?estado=" + estado;
    }
}
