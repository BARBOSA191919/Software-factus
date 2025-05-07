package com.gestion.prestamos.controlador;

import com.gestion.prestamos.entidades.Producto;
import com.gestion.prestamos.entidades.ProductosDTO;
import com.gestion.prestamos.servicio.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> findAll() {
        logger.info("Fetching all products");
        return ResponseEntity.ok(productoService.findAll());
    }

    // Crear un producto
    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        logger.info("Creating product: {}", producto);
        return ResponseEntity.ok(productoService.save(producto));
    }

    // Crear un lote de productos
    @PostMapping("/crear-lote")
    public ResponseEntity<String> crearProductos(@RequestBody ProductosDTO productosDTO) {
        logger.info("Creating batch of products: {}", productosDTO);
        List<ProductosDTO> productos = productosDTO.getProductos();
        productoService.guardarLote(productos);
        return ResponseEntity.ok("Productos creados exitosamente");
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto producto) {
        logger.info("Updating product with id: {}", id);
        return ResponseEntity.ok(productoService.update(id, producto));
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        logger.info("Fetching product with id: {}", id);
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Contar todos los productos
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        logger.info("Counting all products");
        return ResponseEntity.ok(productoService.count());
    }

    // Obtener los productos más vendidos
    @GetMapping("/top")
    public ResponseEntity<?> findTopProducts() {
        try {
            logger.info("Fetching top products");
            List<Object[]> topProducts = productoService.findTopProductsWithSalesCount();
            logger.info("Fetched {} top products", topProducts.size());

            List<Map<String, Object>> result = topProducts.stream().limit(10).map(record -> {
                Producto p = (Producto) record[0];
                Long salesCount;
                try {
                    salesCount = record[1] instanceof Long ? (Long) record[1] : ((Number) record[1]).longValue();
                } catch (Exception e) {
                    logger.warn("Error casting salesCount for product id {}: {}", p.getId(), e.getMessage());
                    salesCount = 0L;
                }

                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", p.getId() != null ? p.getId() : 0);
                productMap.put("name", p.getName() != null ? p.getName() : "N/A");
                productMap.put("price", p.getPrice() != null ? p.getPrice() : 0.0);
                productMap.put("excluded", p.getExcluded() != null ? p.getExcluded() : false);
                productMap.put("salesCount", salesCount);
                return productMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching top products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cargar los productos más vendidos: " + e.getMessage()));
        }
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting product with id: {}", id);
            productoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar producto: " + e.getMessage()));
        }
    }
}