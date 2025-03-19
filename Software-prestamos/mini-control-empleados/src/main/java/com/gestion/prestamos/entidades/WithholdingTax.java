package com.gestion.prestamos.entidades;

public class WithholdingTax {
    private String code;
    private String withholdingTaxRate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWithholdingTaxRate() {
        return withholdingTaxRate;
    }

    public void setWithholdingTaxRate(String withholdingTaxRate) {
        this.withholdingTaxRate = withholdingTaxRate;
    }
}
