package com.ciberbus.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/destinos")
    public String destinos(Model model) {
        model.addAttribute("destinosPublicos", destinosPublicos());
        return "destinos";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @PostMapping("/contacto")
    public String enviarContacto(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("ok", "Gracias. Recibimos tu mensaje y te contactaremos pronto.");
        return "redirect:/contacto";
    }

    @GetMapping("/autogestion")
    public String autogestion() {
        return "reserva-consultar";
    }

    private List<Map<String, String>> destinosPublicos() {
        return List.of(
                destino("Lima", "Lima", "plazaLima.png", "limaModal.png",
                        "Lima es la capital del Perú, reconocida por su centro histórico, la Costa Verde y su gastronomía de nivel mundial."),
                destino("Ica", "Ica", "huacachinaIca.png", "icaModal.png",
                        "Ica es famosa por su oasis de Huacachina, sus viñedos y las dunas del desierto que ofrecen experiencias únicas."),
                destino("Cusco", "Cusco", "plazaCusco.png", "cuscoModal.png",
                        "Cusco es la capital histórica del Perú y puerta de entrada a Machu Picchu, con una mezcla única de cultura inca y colonial."),
                destino("Chachapoyas", "Chachapoyas", "Amazonas.png", "chachapoyasModal.png",
                        "Chachapoyas es conocida por su naturaleza exuberante y sitios arqueológicos como la fortaleza de Kuélap."),
                destino("Huaraz", "Huaraz", "huaraz.png", "huarazModal.png",
                        "Huaraz es la capital del alpinismo peruano, ubicada en la Cordillera Blanca y famosa por sus paisajes nevados."),
                destino("Abancay", "Abancay", "abancay.png", "abancayModal.png",
                        "Abancay, capital de Apurímac, es conocida como la “Ciudad de la Eterna Primavera” por su clima templado y sus paisajes andinos."),
                destino("Arequipa", "Arequipa", "arequipa.png", "arequipaModal.png",
                        "Arequipa, conocida como la “Ciudad Blanca”, destaca por su arquitectura colonial, el imponente volcán Misti y su variada gastronomía."),
                destino("Ayacucho", "Ayacucho", "ayacucho.png", "ayacuchoModal.png",
                        "Ayacucho es conocida como la “Ciudad de las Iglesias”, con un centro histórico colonial y una tradición artesanal reconocida."),
                destino("Cajamarca", "Cajamarca", "cajamarca.jpg", "cajamarcaModal.png",
                        "Cajamarca es célebre por sus carnavales, sus aguas termales y su importancia en la historia del Tahuantinsuyo."),
                destino("Huancavelica", "Huancavelica", "huancavelica.jpg", "huancavelicaModal.png",
                        "Huancavelica es conocida por sus paisajes andinos, su tradición minera y su riqueza cultural."),
                destino("Huanuco", "Huánuco", "huanuco.jpg", "huanucoModal.png",
                        "Huánuco es conocida como la “Ciudad del Mejor Clima del Mundo”, con valles fértiles y restos arqueológicos."),
                destino("Huancayo", "Huancayo", "huancayo.jpg", "huancayoModal.png",
                        "Huancayo es la capital del Valle del Mantaro, reconocida por su feria dominical y su gastronomía andina."),
                destino("Trujillo", "Trujillo", "trujillo.jpg", "trujilloModal.png",
                        "Trujillo es conocida como la “Capital de la Primavera”, famosa por Chan Chan, las huacas y el caballo de paso."),
                destino("Chiclayo", "Chiclayo", "chiclayo.jpg", "chiclayoModal.png",
                        "Chiclayo es conocida como la “Capital de la Amistad”, con importante herencia moche y una gastronomía norteña destacada."),
                destino("Pasco", "Cerro de Pasco", "pasco.jpg", "pascoModal.png",
                        "Cerro de Pasco es una ciudad minera ubicada en la sierra central, reconocida por su altitud y su importancia histórica en la explotación de minerales."),
                destino("Piura", "Piura", "piura.png", "piuraModal.png",
                        "Piura es famosa por sus playas cálidas como Máncora, su tradición artesanal y su deliciosa gastronomía norteña."),
                destino("Puno", "Puno", "puno.jpg", "punoModal.png",
                        "Puno es la capital del folclore peruano, famosa por la Fiesta de la Candelaria y por sus paisajes a orillas del lago Titicaca."),
                destino("Moyobamba", "Moyobamba", "moyobamba.png", "moyobambaModal.png",
                        "Moyobamba es la “Ciudad de las Orquídeas”, reconocida por su biodiversidad amazónica, sus miradores naturales y sus aguas termales."),
                destino("Tumbes", "Tumbes", "tumbes.jpg", "tumbesModal.png",
                        "Tumbes es un destino costero destacado por sus playas cálidas, manglares y su biodiversidad tropical."),
                destino("Pucallpa", "Pucallpa", "pucallpa.jpg", "pucallpaModal.png",
                        "Pucallpa es la “Ciudad del Color y del Calor”, ubicada en la Amazonía peruana y reconocida por su cultura shipibo-conibo y sus paisajes selváticos.")
        );
    }

    private Map<String, String> destino(String id, String ciudad, String imagen, String modal, String texto) {
        return Map.of("id", id, "ciudad", ciudad, "imagen", imagen, "modal", modal, "texto", texto);
    }
}
