package com.gestion.prestamos.repositorios;

import com.gestion.prestamos.entidades.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    Caja findTopByOrderByIdDesc();

}
