package com.gestion.prestamos.repositorios;


import com.gestion.prestamos.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}