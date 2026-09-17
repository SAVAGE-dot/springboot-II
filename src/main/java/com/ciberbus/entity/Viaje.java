package com.ciberbus.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdViaje")
    private Integer idViaje;
    
    // hola prueba 2

    @Column(name = "CodigoViaje", nullable = false, unique = true, length = 20)
    private String codigoViaje;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdRuta", nullable = false)
    private Ruta ruta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdBus", nullable = false)
    private Bus bus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdConductor", nullable = false)
    private Conductor conductor;

    @Column(name = "FechaSalida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "HoraSalida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "FechaLlegada", nullable = false)
    private LocalDate fechaLlegada;

    @Column(name = "HoraLlegada", nullable = false)
    private LocalTime horaLlegada;

    @Column(name = "Tarifa", nullable = false, precision = 6, scale = 2)
    private BigDecimal tarifa;

    @Column(name = "Estado", nullable = false)
    private Integer estado = 1;

    public Integer getIdViaje() { return idViaje; }
    public void setIdViaje(Integer idViaje) { this.idViaje = idViaje; }
    public String getCodigoViaje() { return codigoViaje; }
    public void setCodigoViaje(String codigoViaje) { this.codigoViaje = codigoViaje; }
    public Ruta getRuta() { return ruta; }
    public void setRuta(Ruta ruta) { this.ruta = ruta; }
    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }
    public Conductor getConductor() { return conductor; }
    public void setConductor(Conductor conductor) { this.conductor = conductor; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }
    public LocalDate getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(LocalDate fechaLlegada) { this.fechaLlegada = fechaLlegada; }
    public LocalTime getHoraLlegada() { return horaLlegada; }
    public void setHoraLlegada(LocalTime horaLlegada) { this.horaLlegada = horaLlegada; }
    public BigDecimal getTarifa() { return tarifa; }
    public void setTarifa(BigDecimal tarifa) { this.tarifa = tarifa; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
