package com.gestion.prestamos.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestion.prestamos.entidades.Prestamo;

public interface PrestamoService {

	public List<Prestamo> findAll();

	public Page<Prestamo> findAll(Pageable pageable);

	public void save(Prestamo prestamo);

	public Prestamo findOne(Long id);

	public void delete(Long id);


}

