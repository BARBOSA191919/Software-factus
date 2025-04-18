package com.gestion.prestamos.repositorios;

import com.gestion.prestamos.entidades.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByReferenceCode(String referenceCode);
    List<Factura> findByStatus(String pendiente);

}