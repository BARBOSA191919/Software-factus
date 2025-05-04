    package com.gestion.prestamos.entidades;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonProperty;

    import javax.persistence.*;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    public class Cliente {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String tipoCliente;
        private String tipoIdentificacion;
        private String identificacion;
        private String nombre;
        private String telefono;
        private String correo;
        private String municipio;
        private Integer municipioId;
        private String direccion;
        @Column(name = "aplicaiva")
        private String aplicaIVA;
        private String legalOrganizationId;
        @Column(name = "tribute_id")
        @JsonProperty("tributeId")
        private String tributeId;

        private String company;
        private String tradeName;
        private String verificationDigit;
        private String idOrg;

        @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore // Evita serializar facturas
        private List<Factura> facturas = new ArrayList<>();


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

        public List<Factura> getFacturas() {
            return facturas;
        }

        public void setFacturas(List<Factura> facturas) {
            this.facturas = facturas;
        }
    }