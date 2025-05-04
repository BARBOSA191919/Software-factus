package com.gestion.factus.repositorios;

import com.gestion.factus.entidades.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByReferenceCode(String referenceCode);

    // Contar todas las facturas
    long count();

    // Obtener las facturas más recientes
    @Query("SELECT f FROM Factura f ORDER BY f.fechaVencimiento DESC")
    List<Factura> findTopByOrderByFechaCreacionDesc(int limit);


    // Contar facturas por cliente
    @Query("SELECT COUNT(f) FROM Factura f WHERE f.cliente.id = :clienteId")
    long countByClienteId(Long clienteId);

}