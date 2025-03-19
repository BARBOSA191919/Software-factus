package com.gestion.prestamos.repositorios;

import com.gestion.prestamos.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {


}
