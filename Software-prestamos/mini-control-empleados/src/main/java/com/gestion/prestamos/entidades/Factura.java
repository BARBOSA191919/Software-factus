package com.gestion.prestamos.entidades;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;
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
    private String apiClientName;
    private String identification;
    private String graphicRepresentationName;
    private String formaPago;       // Forma de pago
    private String metodoPago;      // Metodo de pago
    private Date createdAt;

    // Nuevos campos
    private String document; // Factura electrónica de venta
    private Long numberingRangeId; // ID del rango de numeración
    private String referenceCode; // Código único de referencia
    private String observation; // Observación

    // Totales de la factura
    private BigDecimal subtotal;
    private BigDecimal totalIva;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    @Embedded
    private BillingPeriod billingPeriod;

    public Factura() {
        this.number = UUID.randomUUID().toString(); // Genera un UUID único
    }

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Indica que este es el lado "principal" de la relación
    private List<Item> items;

    // Asegúrate de tener este metodo helper
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

    public String getApiClientName() {
        return apiClientName;
    }

    public void setApiClientName(String apiClientName) {
        this.apiClientName = apiClientName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
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

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(BillingPeriod billingPeriod) {
        this.billingPeriod = billingPeriod;
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
}