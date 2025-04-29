package com.gestion.prestamos.controlador;

import com.gestion.prestamos.entidades.Producto;
import com.gestion.prestamos.entidades.ProductosDTO;
import com.gestion.prestamos.servicio.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Obtener todos los productos
    @GetMapping
    public List<Producto> findAll() {
        return productoService.findAll();
    }

    // Crear un producto
    @PostMapping
    public Producto create(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    // Crear un lote de productos
    @PostMapping("/crear-lote")
    public ResponseEntity<String> crearProductos(@RequestBody ProductosDTO productosDTO) {
        List<ProductosDTO> productos = productosDTO.getProductos();
        productoService.guardarLote(productos);
        return ResponseEntity.ok("Productos creados exitosamente");
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public Producto update(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.update(id, producto);
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}