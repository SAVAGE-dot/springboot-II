package com.ciberbus.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ciberbus.entity.Pasajero;
import com.ciberbus.entity.Reserva;
import com.ciberbus.entity.Usuario;
import com.ciberbus.entity.Viaje;
import com.ciberbus.entity.ViajeAsiento;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.service.CatalogoService;
import com.ciberbus.service.ReservaService;
import com.ciberbus.service.UsuarioService;
import com.ciberbus.service.ViajeService;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;
    private final ViajeService viajeService;
    private final CatalogoService catalogoService;
    private final UsuarioService usuarioService;

    public ReservaController(ReservaService reservaService,
                             ViajeService viajeService,
                             CatalogoService catalogoService,
                             UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.viajeService = viajeService;
        this.catalogoService = catalogoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar")
    public String buscarViajesForm(Model model) {
        model.addAttribute("ciudades", catalogoService.listarCiudades());
        model.addAttribute("viajes", List.of());
        return "reserva-buscar";
    }

    @GetMapping("/resultados")
    public String resultadosBusqueda(
            @RequestParam Integer origen,
            @RequestParam Integer destino,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {
        model.addAttribute("ciudades", catalogoService.listarCiudades());
        model.addAttribute("viajes", viajeService.buscar(origen, destino, fecha));
        model.addAttribute("origenSeleccionado", origen);
        model.addAttribute("destinoSeleccionado", destino);
        model.addAttribute("fechaSeleccionada", fecha);
        return "reserva-buscar";
    }

    @GetMapping("/asientos/{idViaje}")
    public String seleccionarAsientos(@PathVariable Integer idViaje, Model model) {
        Viaje viaje = viajeService.buscarPorId(idViaje)
                .orElseThrow(() -> new NegocioException("Viaje no encontrado"));
        List<ViajeAsiento> asientos = viajeService.listarAsientos(idViaje);

        model.addAttribute("viaje", viaje);
        model.addAttribute("asientos", asientos);
        return "reserva-asientos";
    }

    @PostMapping("/checkout")
    public String checkoutForm(
            @RequestParam Integer idViaje,
            @RequestParam List<Integer> asientosIds,
            Authentication authentication,
            Model model) {
        Viaje viaje = viajeService.buscarPorId(idViaje)
                .orElseThrow(() -> new NegocioException("Viaje no encontrado"));
        List<ViajeAsiento> asientos = viajeService.listarAsientos(idViaje).stream()
                .filter(a -> asientosIds.contains(a.getIdViajeAsiento()))
                .toList();

        model.addAttribute("viaje", viaje);
        model.addAttribute("asientosSeleccionados", asientos);
        model.addAttribute("usuarioActual", usuarioActual(authentication));
        return "reserva-checkout";
    }

    @PostMapping("/procesar")
    public String procesarReserva(
            @RequestParam Integer idViaje,
            @RequestParam List<Integer> asientosIds,
            @RequestParam List<String> tipoDocumento,
            @RequestParam List<String> nroDocumento,
            @RequestParam List<String> nombre,
            @RequestParam List<String> apellido,
            @RequestParam(required = false) List<String> correo,
            @RequestParam(required = false) List<String> telefono,
            @RequestParam String metodoPago,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Integer idUsuario = usuarioActual(authentication).getIdUsuario();

            List<Pasajero> pasajeros = new ArrayList<>();
            for (int i = 0; i < asientosIds.size(); i++) {
                Pasajero pasajero = new Pasajero();
                pasajero.setTipoDocumento(valor(tipoDocumento, i));
                pasajero.setNroDocumento(valor(nroDocumento, i));
                pasajero.setNombre(valor(nombre, i));
                pasajero.setApellido(valor(apellido, i));
                pasajero.setCorreo(blankToNull(valor(correo, i)));
                pasajero.setTelefono(blankToNull(valor(telefono, i)));
                pasajeros.add(pasajero);
            }

            Reserva reserva = reservaService.crearReserva(idUsuario, idViaje, asientosIds, pasajeros, metodoPago);
            redirectAttributes.addFlashAttribute("mensaje", "Reserva creada con éxito. Código: " + reserva.getCodigoReserva());
            return "redirect:/reserva/detalle/" + reserva.getCodigoReserva();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar reserva: " + e.getMessage());
            return "redirect:/reserva/buscar";
        }
    }

    @GetMapping("/detalle/{codigo}")
    public String detalleReserva(@PathVariable String codigo, Model model) {
        Reserva reserva = reservaService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new NegocioException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        return "reserva-detalle";
    }

    @GetMapping("/mis-reservas")
    public String misReservas(Authentication authentication, Model model) {
        Usuario usuario = usuarioActual(authentication);
        model.addAttribute("reservas", reservaService.listarPorUsuario(usuario.getIdUsuario()));
        model.addAttribute("usuarioActual", usuario);
        return "reserva-lista";
    }

    /**
     * Usa el usuario autenticado (login por NroDocumento) o el invitado cuando no hay sesión.
     */
    private Usuario usuarioActual(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return usuarioService.buscarPorNroDocumento(authentication.getName())
                    .orElseThrow(() -> new NegocioException("Usuario autenticado no encontrado."));
        }
        return usuarioService.obtenerInvitado();
    }

    private String valor(List<String> lista, int indice) {
        return (lista != null && indice < lista.size()) ? lista.get(indice) : null;
    }

    private String blankToNull(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }
}
