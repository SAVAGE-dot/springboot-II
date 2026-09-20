package com.ciberbus.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ciberbus.entity.Usuario;
import com.ciberbus.repository.UsuarioRepository;

/**
 * Puente entre Spring Security y la base de datos.
 * El "username" del login es el NroDocumento del usuario.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nroDocumento) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByNroDocumento(nroDocumento)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + nroDocumento));

        // Asegurar prefijo ROLE_ para que hasRole() funcione
        String rol = u.getRol() == null ? "CLIENTE" : u.getRol().toUpperCase();
        String authority = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

        // Bloquear usuarios inactivos (Estado = 0)
        boolean activo = u.getEstado() != null && u.getEstado() == 1;

        return User.builder()
                .username(u.getNroDocumento())
                .password(u.getClave())
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .disabled(!activo)
                .build();
    }
}