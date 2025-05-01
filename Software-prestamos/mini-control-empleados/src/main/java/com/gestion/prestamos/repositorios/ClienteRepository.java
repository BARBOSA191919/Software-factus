package com.gestion.prestamos.repositorios;


import com.gestion.prestamos.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query(value = "SELECT c FROM Cliente c ORDER BY c.id DESC")
    List<Cliente> findTopByOrderByIdDesc(int limit);
}