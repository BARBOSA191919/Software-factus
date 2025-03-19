package com.gestion.prestamos.entidades;

import java.math.BigDecimal;
import java.util.List;

public class ProductosDTO {

    private Long id;
    private List<ProductosDTO> productos;
    private String name;
    private BigDecimal price;
    private String measureId;
    private Boolean excluded;
    private String withholdingTaxRate;
    private Double taxRate;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductosDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductosDTO> productos) {
        this.productos = productos;
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
        return excluded;
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
}