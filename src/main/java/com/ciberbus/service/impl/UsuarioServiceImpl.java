package com.ciberbus.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Usuario;
import com.ciberbus.exception.NegocioException;
import com.ciberbus.repository.UsuarioRepository;
import com.ciberbus.service.UsuarioService;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final String INVITADO_NRO_DOCUMENTO = "GUEST";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNroDocumento(String nroDocumento) {
        return usuarioRepository.findByNroDocumento(nroDocumento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByNroDocumento(usuario.getNroDocumento())) {
            throw new NegocioException("Ya existe un usuario con ese documento: "
                    + usuario.getNroDocumento());
        }
        // Encriptar la clave antes de guardar
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario obtenerInvitado() {
        return usuarioRepository.findByNroDocumento(INVITADO_NRO_DOCUMENTO)
                .orElseGet(() -> {
                    Usuario invitado = new Usuario();
                    invitado.setTipoDocumento("DNI");
                    invitado.setNroDocumento(INVITADO_NRO_DOCUMENTO);
                    invitado.setNombre("Invitado");
                    invitado.setApellido("CiberBus");
                    invitado.setRol("CLIENTE");
                    invitado.setEstado(1);
                    return usuarioRepository.save(invitado);
                });
    }
}