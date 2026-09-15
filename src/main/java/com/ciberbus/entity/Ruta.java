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
@Table(name = "ruta")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdRuta")
    private Integer idRuta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CiudadPartida", nullable = false)
    private Ciudad ciudadPartida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CiudadLlegada", nullable = false)
    private Ciudad ciudadLlegada;

    @Column(name = "HorasEstimadas", nullable = false, precision = 4, scale = 2)
    private BigDecimal horasEstimadas;

    @Column(name = "Estado", nullable = false)
    private Integer estado = 1;

    public Integer getIdRuta() { return idRuta; }
    public void setIdRuta(Integer idRuta) { this.idRuta = idRuta; }
    public Ciudad getCiudadPartida() { return ciudadPartida; }
    public void setCiudadPartida(Ciudad ciudadPartida) { this.ciudadPartida = ciudadPartida; }
    public Ciudad getCiudadLlegada() { return ciudadLlegada; }
    public void setCiudadLlegada(Ciudad ciudadLlegada) { this.ciudadLlegada = ciudadLlegada; }
    public BigDecimal getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(BigDecimal horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
