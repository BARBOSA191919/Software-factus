package com.gestion.prestamos.entidades;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "caja")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double cantidadInicial;

    private Double cantidadActual;

    public Caja() {
        this.cantidadInicial = 0.000;
        this.cantidadActual = 0.000;
    }

    public Caja(Double cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
        this.cantidadActual = cantidadInicial;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCantidadInicial() {
        return cantidadInicial != null ? cantidadInicial : 0.000;
    }

    public void setCantidadInicial(Double cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public Double getCantidadActual() {
        return cantidadActual != null ? cantidadActual : 0.000;
    }

    public void setCantidadActual(Double cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public boolean saldoSuficiente(Double monto) {
        return cantidadActual >= monto;
    }

    public void restarMonto(Double monto) {
        cantidadActual -= monto;
    }

    public void agregarMonto(Double monto) {
        cantidadActual += monto;
    }
}
