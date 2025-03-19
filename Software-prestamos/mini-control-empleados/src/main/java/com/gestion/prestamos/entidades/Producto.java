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
    private String measureId;
    private Boolean excluded;
    private String withholdingTaxRate;
    private Double taxRate;

    // Nuevos campos
    private String standardCodeId; // Estándar de adopción del contribuyente
    private String unitMeasureId; // Unidad de medida

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

    public String getMeasureId() {
        return measureId;
    }

    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }

    public Boolean getExcluded() {
        return excluded != null ? excluded : false;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public String getWithholdingTaxRate() {
        return withholdingTaxRate;
    }

    public void setWithholdingTaxRate(String withholdingTaxRate) {
        this.withholdingTaxRate = withholdingTaxRate;
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