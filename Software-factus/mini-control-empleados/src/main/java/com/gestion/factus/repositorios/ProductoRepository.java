package com.gestion.factus.repositorios;

import com.gestion.factus.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Contar todos los productos
    long count();

    // Obtener los productos más vendidos con su conteo de ventas (máximo 10)
    @Query("SELECT p, COALESCE(SUM(i.cantidad), 0) as salesCount " +
            "FROM Producto p " +
            "LEFT JOIN Item i ON i.producto.id = p.id " +
            "LEFT JOIN Factura f ON i.factura.id = f.id AND f.status IN ('VALIDADA', 'CREADA') " +
            "GROUP BY p.id " +
            "ORDER BY salesCount DESC, p.id ASC")
    List<Object[]> findTopProductsWithSalesCount();

}
