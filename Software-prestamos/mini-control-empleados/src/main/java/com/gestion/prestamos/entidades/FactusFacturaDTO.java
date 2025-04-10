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

    public void setNumbering_range_id(int i) {
    }

    public void setPayment_due_date(String format) {
    }

    // Nested BillingPeriodDTO class
    public static class BillingPeriodDTO {
        private String start_date;
        private String end_date;

        // Getters and setters
        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public void setStart_time(String time) {
        }

        public void setEnd_time(String time) {
        }
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
        private String municipio;       // Municipio



        // Getters and setters
        public String getIdentification_document_id() {
            return identification_document_id;
        }

        public void setIdentification_document_id(String identification_document_id) {
            this.identification_document_id = identification_document_id;
        }

        public String getMunicipio() {
            return municipio;
        }

        public void setMunicipio(String municipio) {
            this.municipio = municipio;
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

        public void setCompany(String nombre) {
        }

        public void setLegal_organization_id(String number) {
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
}