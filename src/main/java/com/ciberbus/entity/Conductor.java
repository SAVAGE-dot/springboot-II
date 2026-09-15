package com.ciberbus.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "conductor")
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdConductor")
    private Integer idConductor;

    @Column(name = "TipoDocumento", nullable = false, length = 20)
    private String tipoDocumento;

    @Column(name = "NroDocumento", nullable = false, unique = true, length = 20)
    private String nroDocumento;

    @Column(name = "Nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "Apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "Correo", unique = true, length = 150)
    private String correo;

    @Column(name = "Telefono", length = 20)
    private String telefono;

    @Column(name = "CategoriaLicencia", nullable = false, length = 20)
    private String categoriaLicencia;

    @Column(name = "VencimientoLicencia", nullable = false)
    private LocalDate vencimientoLicencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdTurno", nullable = false)
    private Turno turno;

    @Column(name = "DiaDescanso", length = 20)
    private String diaDescanso;

    @Column(name = "Estado", nullable = false)
    private Integer estado = 1;

    public Integer getIdConductor() { return idConductor; }
    public void setIdConductor(Integer idConductor) { this.idConductor = idConductor; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCategoriaLicencia() { return categoriaLicencia; }
    public void setCategoriaLicencia(String categoriaLicencia) { this.categoriaLicencia = categoriaLicencia; }
    public LocalDate getVencimientoLicencia() { return vencimientoLicencia; }
    public void setVencimientoLicencia(LocalDate vencimientoLicencia) { this.vencimientoLicencia = vencimientoLicencia; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public String getDiaDescanso() { return diaDescanso; }
    public void setDiaDescanso(String diaDescanso) { this.diaDescanso = diaDescanso; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
