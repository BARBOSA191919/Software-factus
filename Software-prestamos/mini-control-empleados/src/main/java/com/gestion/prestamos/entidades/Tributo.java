package com.gestion.prestamos.entidades;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Tributo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tributeId;
    private Double rate;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTributeId() {
        return tributeId;
    }

    public void setTributeId(String tributeId) {
        this.tributeId = tributeId;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
}
