package com.gestion.prestamos.entidades;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private String unitMeasureId; // "70" para unidad, según Factus
    private String standardCodeId; // "1" por defecto
    private Double taxRate; // Porcentaje de IVA (ej. 19.0)
    private Boolean excluded; // Excluido de IVA


    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public String getStandardCodeId() {
        return standardCodeId;
    }

    public void setStandardCodeId(String standardCodeId) {
        this.standardCodeId = standardCodeId;
    }

    public String getUnitMeasureId() {
        return unitMeasureId;
    }

    public void setUnitMeasureId(String unitMeasureId) {
        this.unitMeasureId = unitMeasureId;
    }

}