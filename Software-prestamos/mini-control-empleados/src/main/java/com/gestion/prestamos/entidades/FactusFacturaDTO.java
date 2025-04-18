package com.gestion.prestamos.entidades;

import java.util.List;

public class FactusFacturaDTO {

    private String document; // "01" para factura electrónica
    private Integer numbering_range_id;
    private String reference_code;
    private String observation;
    private String payment_form; // "1" Contado, "2" Crédito
    private String payment_method_code; // "10" Efectivo, "31" Transferencia
    private CustomerDTO customer;
    private List<ItemDTO> items;

    // Nuevos campos
    private String due_date; // Fecha de vencimiento en formato ISO (ej. "2025-05-01")
    private Integer lines_count; // Número de líneas
    private Double discount_total; // Descuento total de la factura
    private List<TributeDTO> tributes; // Tributos adicionales, como INC

    // Clase interna para tributos
    public static class TributeDTO {
        private String tribute_id; // Ej. "03" para INC
        private Double rate; // Tasa del tributo
        private Double amount; // Monto del tributo

        public String getTribute_id() {
            return tribute_id;
        }

        public void setTribute_id(String tribute_id) {
            this.tribute_id = tribute_id;
        }

        public Double getRate() {
            return rate;
        }

        public void setRate(Double rate) {
            this.rate = rate;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }

    public void setNumbering_range_id(int i) {
    }

    public void setPayment_due_date(String format) {
    }



    // Nested CustomerDTO class
    public static class CustomerDTO {
        private String identification_document_id; // "3" Cédula
        private String identification;
        private String names;
        private String email;
        private String address;
        private String legalOrganizationId; // ID de organización legal
        private String tribute_id;
        private Integer municipality_id; // Código DANE del municipio (ej. "05001" para Medellín)

        // Getters y setters


        public Integer getMunicipality_id() {
            return municipality_id;
        }

        public void setMunicipality_id(Integer municipality_id) {
            this.municipality_id = municipality_id;
        }

        // Resto de getters y setters sin cambios
        public String getIdentification_document_id() {
            return identification_document_id;
        }

        public void setIdentification_document_id(String identification_document_id) {
            this.identification_document_id = identification_document_id;
        }

        public String getIdentification() {
            return identification;
        }

        public void setIdentification(String identification) {
            this.identification = identification;
        }

        public String getLegalOrganizationId() {
            return legalOrganizationId;
        }

        public void setLegalOrganizationId(String legalOrganizationId) {
            this.legalOrganizationId = legalOrganizationId;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTribute_id() {
            return tribute_id;
        }

        public void setTribute_id(String tribute_id) {
            this.tribute_id = tribute_id;
        }

        public void setCompany(String nombre) {
            this.names = nombre; // Ajuste para manejar nombres de empresas
        }

        public void setLegal_organization_id(String number) {
            this.legalOrganizationId = number;
        }

    }

    // Nested ItemDTO class
    public static class ItemDTO {
        private String code_reference; // ID o código del producto
        private String name;
        private Double price;
        private Integer quantity;
        private Double discount_rate; // Porcentaje de descuento
        private Double tax_rate; // Porcentaje de IVA
        private String unit_measure_id; // "70" Unidad
        private String standard_code_id; // "1" por defecto
        private Integer is_excluded; // 0 No excluido, 1 Excluido
        private String tribute_id; // "1" IVA

        // Getters and setters
        public String getCode_reference() {
            return code_reference;
        }

        public void setCode_reference(String code_reference) {
            this.code_reference = code_reference;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getDiscount_rate() {
            return discount_rate;
        }

        public void setDiscount_rate(Double discount_rate) {
            this.discount_rate = discount_rate;
        }

        public Double getTax_rate() {
            return tax_rate;
        }

        public void setTax_rate(Double tax_rate) {
            this.tax_rate = tax_rate;
        }

        public String getUnit_measure_id() {
            return unit_measure_id;
        }

        public void setUnit_measure_id(String unit_measure_id) {
            this.unit_measure_id = unit_measure_id;
        }

        public String getStandard_code_id() {
            return standard_code_id;
        }

        public void setStandard_code_id(String standard_code_id) {
            this.standard_code_id = standard_code_id;
        }

        public Integer getIs_excluded() {
            return is_excluded;
        }

        public void setIs_excluded(Integer is_excluded) {
            this.is_excluded = is_excluded;
        }

        // Getter
        public String getTribute_id() {
            return tribute_id;
        }

        // Setter
        public void setTribute_id(String tribute_id) {
            this.tribute_id = tribute_id;
        }
    }

    // Getters and setters for the main class
    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getReference_code() {
        return reference_code;
    }

    public void setReference_code(String reference_code) {
        this.reference_code = reference_code;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getPayment_form() {
        return payment_form;
    }

    public void setPayment_form(String payment_form) {
        this.payment_form = payment_form;
    }

    public String getPayment_method_code() {
        return payment_method_code;
    }

    public void setPayment_method_code(String payment_method_code) {
        this.payment_method_code = payment_method_code;
    }

    public Integer getNumbering_range_id() {
        return numbering_range_id;
    }

    public void setNumbering_range_id(Integer numbering_range_id) {
        this.numbering_range_id = numbering_range_id;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public Integer getLines_count() {
        return lines_count;
    }

    public void setLines_count(Integer lines_count) {
        this.lines_count = lines_count;
    }

    public Double getDiscount_total() {
        return discount_total;
    }

    public void setDiscount_total(Double discount_total) {
        this.discount_total = discount_total;
    }

    public List<TributeDTO> getTributes() {
        return tributes;
    }

    public void setTributes(List<TributeDTO> tributes) {
        this.tributes = tributes;
    }
}