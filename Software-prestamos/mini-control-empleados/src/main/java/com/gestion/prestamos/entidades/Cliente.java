package com.gestion.prestamos.entidades;

import javax.persistence.*;

@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoCliente;     // Tipo cliente (Persona Natural, etc.)
    private String tipoIdentificacion; // Tipo identificación (Cédula de ciudadanía, etc.)
    private String identificacion;  // Número de identificación
    private String nombre;          // Nombre del cliente
    private String telefono;        // Teléfono
    private String correo;          // Correo electrónico
    private String municipio;       // Municipio
    private Integer municipioId; // For the municipality ID
    private String direccion;       // Dirección
    @Column(name = "aplicaiva") // Mapea el campo aplicaIVA a la columna aplicaiva en la base de datos
    private String aplicaIVA;     // Nuevos campos
    private String legalOrganizationId; // ID de organización legal
    private String tributeId;  // ID de tributo
    // Mantenemos algunos campos originales que podrían ser útiles

    private String company;
    private String tradeName;
    private String verificationDigit;
    private String idOrg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getAplicaIVA() {
        return aplicaIVA;
    }

    public void setAplicaIVA(String aplicaIVA) {
        this.aplicaIVA = aplicaIVA;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getVerificationDigit() {
        return verificationDigit;
    }

    public void setVerificationDigit(String verificationDigit) {
        this.verificationDigit = verificationDigit;
    }

    public String getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(String idOrg) {
        this.idOrg = idOrg;
    }

    public String getTributeId() {
        return tributeId;
    }

    public void setTributeId(String tributeId) {
        this.tributeId = tributeId;
    }

    public String getLegalOrganizationId() {
        return legalOrganizationId;
    }

    public void setLegalOrganizationId(String legalOrganizationId) {
        this.legalOrganizationId = legalOrganizationId;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipioId) {
        this.municipioId = municipioId;
    }
}