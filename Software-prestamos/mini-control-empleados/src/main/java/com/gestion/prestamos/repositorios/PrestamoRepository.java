package com.gestion.prestamos.repositorios;

import com.gestion.prestamos.entidades.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {


}



