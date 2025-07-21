package com.gestion.factus.repositorios;


import com.gestion.factus.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Obtener los clientes más recientes con su conteo de facturas
    @Query("SELECT c, COUNT(f.id) as facturaCount " +
            "FROM Cliente c " +
            "LEFT JOIN Factura f ON f.cliente.id = c.id " +
            "GROUP BY c.id " +
            "ORDER BY c.id DESC")
    List<Object[]> findTopByOrderByIdDescWithFacturaCount(int limit);
}