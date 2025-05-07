package com.gestion.prestamos.servicio;

import com.gestion.prestamos.entidades.Producto;
import com.gestion.prestamos.entidades.ProductosDTO;
import com.gestion.prestamos.repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }


    public Producto update(Long id, Producto producto) {
        if (productoRepository.existsById(id)) {
            producto.setId(id);
            return productoRepository.save(producto);
        }
        throw new RuntimeException("Producto no encontrado");
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public void guardarLote(List<ProductosDTO> productosDTO) {
        // Convertir ProductosDTO a Producto simplificado
        List<Producto> productos = productosDTO.stream()
                .map(dto -> {
                    Producto producto = new Producto();
                    producto.setName(dto.getName());
                    producto.setPrice(dto.getPrice());
                    producto.setExcluded(dto.getExcluded());
                    producto.setTaxRate(dto.getTaxRate());
                    producto.setActivo(true); // Por defecto activo
                    return producto;
                })
                .toList();

        productoRepository.saveAll(productos);
    }

    // contar productos
    public long count() {
        return productoRepository.count();
    }

    public List<Object[]> findTopProductsWithSalesCount() {
        return productoRepository.findTopProductsWithSalesCount();
    }
}