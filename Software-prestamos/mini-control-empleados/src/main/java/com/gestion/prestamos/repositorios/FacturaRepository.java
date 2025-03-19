package com.gestion.prestamos.repositorios;

import com.gestion.prestamos.entidades.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Factura findByNumber(String referenceCode); // Buscar por referenceCode

}