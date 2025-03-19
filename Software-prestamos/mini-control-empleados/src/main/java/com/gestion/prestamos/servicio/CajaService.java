package com.gestion.prestamos.servicio;

import com.gestion.prestamos.entidades.Caja;
import com.gestion.prestamos.repositorios.CajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CajaService {

    @Autowired
    private CajaRepository cajaRepository;


    public Caja obtenerCaja() {
        Caja caja = cajaRepository.findTopByOrderByIdDesc();
        if (caja != null) {
            return caja;
        } else {
            // If no existing Caja object is found, create a new one with default values
            return new Caja(0.0);
        }
    }

    public void guardarCaja(Caja caja) {
        cajaRepository.save(caja); // Esto guarda los cambios en la base de datos
    }

    public Caja obtenerCajaPorId(Long id) {
        return cajaRepository.findById(id).orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    }

    // Este metodo debe devolver el estado actual de la caja
    public Caja getCajaActual() {
        return cajaRepository.findTopByOrderByIdDesc(); // Ajusta esto a tu lógica de negocio
    }

    public void agregarMonto(Double monto) {
        Caja caja = obtenerCaja();
        caja.agregarMonto(monto);
        cajaRepository.save(caja);
    }

    public boolean saldoSuficiente(Double monto) {
        Caja caja = obtenerCaja();
        return caja.saldoSuficiente(monto);
    }

    public void restarMonto(Double monto) {
        Caja caja = obtenerCaja();
        caja.restarMonto(monto);
        cajaRepository.save(caja);
    }

    public void editarMonto(Double monto) {
        Caja caja = obtenerCaja(); // Obtiene la caja actual
        if (caja != null) {
            caja.setCantidadActual(caja.getCantidadActual() + monto); // Suma el monto
            // Guarda los cambios, asumiendo que tienes un repositorio
            cajaRepository.save(caja);
        }
    }



}