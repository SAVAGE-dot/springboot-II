package com.ciberbus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "viajeasiento", uniqueConstraints = @UniqueConstraint(columnNames = {"IdViaje", "NroAsiento", "Piso"}))
public class ViajeAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdViajeAsiento")
    private Integer idViajeAsiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdViaje", nullable = false)
    private Viaje viaje;

    @Column(name = "NroAsiento", nullable = false)
    private Integer nroAsiento;

    @Column(name = "Piso", nullable = false)
    private Integer piso;

    @Column(name = "Estado", nullable = false)
    private Integer estado = 1;

    public Integer getIdViajeAsiento() { return idViajeAsiento; }
    public void setIdViajeAsiento(Integer idViajeAsiento) { this.idViajeAsiento = idViajeAsiento; }
    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) { this.viaje = viaje; }
    public Integer getNroAsiento() { return nroAsiento; }
    public void setNroAsiento(Integer nroAsiento) { this.nroAsiento = nroAsiento; }
    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
