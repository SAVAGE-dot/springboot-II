package com.ciberbus.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detallereserva")
public class DetalleReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDetalleReserva")
    private Integer idDetalleReserva;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdReserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdViajeAsiento", nullable = false)
    private ViajeAsiento viajeAsiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdPasajero", nullable = false)
    private Pasajero pasajero;

    @Column(name = "PrecioPagado", nullable = false, precision = 6, scale = 2)
    private BigDecimal precioPagado;

    public Integer getIdDetalleReserva() { return idDetalleReserva; }
    public void setIdDetalleReserva(Integer idDetalleReserva) { this.idDetalleReserva = idDetalleReserva; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public ViajeAsiento getViajeAsiento() { return viajeAsiento; }
    public void setViajeAsiento(ViajeAsiento viajeAsiento) { this.viajeAsiento = viajeAsiento; }
    public Pasajero getPasajero() { return pasajero; }
    public void setPasajero(Pasajero pasajero) { this.pasajero = pasajero; }
    public BigDecimal getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(BigDecimal precioPagado) { this.precioPagado = precioPagado; }
}
