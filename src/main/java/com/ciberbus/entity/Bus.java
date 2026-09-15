package com.ciberbus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bus")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdBus")
    private Integer idBus;

    @Column(name = "NroPlaca", nullable = false, unique = true, length = 15)
    private String nroPlaca;

    @Column(name = "Marca", nullable = false, length = 50)
    private String marca;

    @Column(name = "TipoBus", nullable = false, length = 50)
    private String tipoBus;

    @Column(name = "CantidadPisos", nullable = false)
    private Integer cantidadPisos;

    @Column(name = "NroAsientos", nullable = false)
    private Integer nroAsientos;

    @Column(name = "Estado", nullable = false)
    private Integer estado = 1;

    public Integer getIdBus() { return idBus; }
    public void setIdBus(Integer idBus) { this.idBus = idBus; }
    public String getNroPlaca() { return nroPlaca; }
    public void setNroPlaca(String nroPlaca) { this.nroPlaca = nroPlaca; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getTipoBus() { return tipoBus; }
    public void setTipoBus(String tipoBus) { this.tipoBus = tipoBus; }
    public Integer getCantidadPisos() { return cantidadPisos; }
    public void setCantidadPisos(Integer cantidadPisos) { this.cantidadPisos = cantidadPisos; }
    public Integer getNroAsientos() { return nroAsientos; }
    public void setNroAsientos(Integer nroAsientos) { this.nroAsientos = nroAsientos; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
