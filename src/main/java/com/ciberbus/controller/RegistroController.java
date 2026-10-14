package com.ciberbus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ciberbus.entity.Usuario;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.service.UsuarioService;

/** P2 — un cliente nuevo se crea una cuenta con su DNI. */
@Controller
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String formulario() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nroDocumento,
                            @RequestParam String nombre,
                            @RequestParam String apellido,
                            @RequestParam String clave,
                            @RequestParam(required = false) String correo,
                            @RequestParam(required = false) String telefono,
                            Model model,
                            RedirectAttributes ra) {
        try {
            if (clave == null || clave.length() < 6) {
                throw new NegocioException("La clave debe tener al menos 6 caracteres.");
            }
            Usuario usuario = new Usuario();
            usuario.setTipoDocumento("DNI");
            usuario.setNroDocumento(nroDocumento.trim());
            usuario.setNombre(nombre.trim());
            usuario.setApellido(apellido.trim());
            usuario.setClave(clave);
            usuario.setCorreo(correo == null || correo.isBlank() ? null : correo.trim());
            usuario.setTelefono(telefono == null || telefono.isBlank() ? null : telefono.trim());
            usuario.setRol("CLIENTE");
            usuario.setEstado(1);
            usuarioService.registrar(usuario);
            ra.addFlashAttribute("ok", "Cuenta creada. Ya puedes ingresar.");
            return "redirect:/login";
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("nroDocumento", nroDocumento);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("correo", correo);
            model.addAttribute("telefono", telefono);
            return "registro";
        }
    }
}
