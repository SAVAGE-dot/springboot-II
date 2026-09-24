package com.ciberbus;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ciberbus.entity.Usuario;
import com.ciberbus.service.UsuarioService;

@SpringBootApplication
public class CiberbusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CiberbusApplication.class, args);
    }

    /**
     * DataLoader: crea usuarios de prueba si no existen.
     * Se ejecuta al arrancar la app. Si el usuario ya existe, NO lo toca.
     *
     *  Admin   → NroDocumento: 12345678 / clave: admin123
     *  Cliente → NroDocumento: 87654321 / clave: cliente123
     */
    @Bean
    public CommandLineRunner cargarUsuariosIniciales(UsuarioService usuarioService,
                                                    PasswordEncoder passwordEncoder) {
        return args -> {
            crearSiNoExiste(usuarioService, passwordEncoder,
                    "12345678", "admin123", "ADMIN",
                    "Cristian", "Arellano", "admin@ciberbus.com");

            crearSiNoExiste(usuarioService, passwordEncoder,
                    "87654321", "cliente123", "CLIENTE",
                    "Cliente", "Prueba", "cliente@ciberbus.com");
        };
    }

    private void crearSiNoExiste(UsuarioService usuarioService,
                                 PasswordEncoder passwordEncoder,
                                 String nroDocumento, String clavePlano, String rol,
                                 String nombre, String apellido, String correo) {
        if (usuarioService.buscarPorNroDocumento(nroDocumento).isEmpty()) {
            Usuario u = new Usuario();
            u.setTipoDocumento("DNI");
            u.setNroDocumento(nroDocumento);
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setCorreo(correo);
            u.setClave(clavePlano); // registrar() lo encripta con BCrypt
            u.setRol(rol);
            u.setEstado(1);
            usuarioService.registrar(u);
            System.out.println(">>> Usuario creado: " + nroDocumento + " (" + rol + ")");
        }
    }
}