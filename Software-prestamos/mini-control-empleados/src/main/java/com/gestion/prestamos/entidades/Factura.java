package com.gestion.prestamos.entidades;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private String documentName;
    private String number;
    private String graphicRepresentationName;
    private String formaPago;
    @JsonProperty("metodo")
    @Column(name = "metodo_pago")
    private String metodoPago;
    private Date createdAt;


    @Column(name = "municipio")
    private Integer municipio;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tributo> tributos = new ArrayList<>();

    @Column(name = "fecha_vencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "inc")
    private BigDecimal inc;


    private String document;
    @Column(name = "numbering_range_id")
    private Long numberingRangeId;
    private String referenceCode;
    private String observation;

    // Totales
    private BigDecimal subtotal;
    private BigDecimal totalIva;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Item> items = new ArrayList<>();

    public Factura() {
        this.number = "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = "PENDIENTE";
        this.createdAt = new Date();
    }

    public void addItem(Item item) {
        items.add(item);
        item.setFactura(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public Integer getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Integer municipio) {
        this.municipio = municipio;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getInc() {
        return inc;
    }

    public void setInc(BigDecimal inc) {
        this.inc = inc;
    }

    public String getGraphicRepresentationName() {
        return graphicRepresentationName;
    }

    public void setGraphicRepresentationName(String graphicRepresentationName) {
        this.graphicRepresentationName = graphicRepresentationName;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = totalIva;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }


    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Long getNumberingRangeId() {
        return numberingRangeId;
    }

    public void setNumberingRangeId(Long numberingRangeId) {
        this.numberingRangeId = numberingRangeId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    // Metodo para calcular número de líneas
    public int getNumeroLineas() {
        return items != null ? items.size() : 0;
    }

    public List<Tributo> getTributos() {
        return tributos;
    }

    public void setTributos(List<Tributo> tributos) {
        this.tributos = tributos;
    }
}