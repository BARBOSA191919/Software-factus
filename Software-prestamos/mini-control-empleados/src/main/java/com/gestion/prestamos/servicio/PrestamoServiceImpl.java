package com.gestion.prestamos.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.prestamos.entidades.Prestamo;
import com.gestion.prestamos.repositorios.PrestamoRepository;

@Service
public class PrestamoServiceImpl implements PrestamoService {

	@Autowired
	private PrestamoRepository prestamoRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> findAll() {
		return (List<Prestamo>) prestamoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Prestamo> findAll(Pageable pageable) {
		return prestamoRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Prestamo empleado) {
		prestamoRepository.save(empleado);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		prestamoRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Prestamo findOne(Long id) {
		return prestamoRepository.findById(id).orElse(null);
	}

	@Autowired
	private PrestamoRepository prestamoRepositorio;

	public Prestamo obtenerPrestamo(Long id) {
		return prestamoRepositorio.findById(id).orElse(null);
	}

	public void guardarPrestamo(Prestamo prestamo) {
		prestamoRepositorio.save(prestamo);
	}
}
	

