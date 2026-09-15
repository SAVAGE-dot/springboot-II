package com.ciberbus.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberbus.entity.Reserva;
import com.ciberbus.repository.ReservaRepository;
import com.ciberbus.service.ReservaService;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorCodigo(String codigoReserva) {
        return reservaRepository.findByCodigoReserva(codigoReserva);
    }

    @Override
    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
}
