package com.gestion.prestamos.controlador;

import com.gestion.prestamos.entidades.Caja;
import com.gestion.prestamos.servicio.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/caja")
public class CajaController {

    @Autowired
    private CajaService cajaService; // Servicio que maneja la lógica de Caja

    // Metodo para mostrar el formulario de agregar o editar dinero
    @GetMapping("/agregarDinero")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
    public String mostrarFormularioAgregarDinero(Model model) {
        Caja caja = cajaService.obtenerCaja();
        model.addAttribute("caja", caja);
        model.addAttribute("titulo", "Agregar o Editar Dinero en la Caja");
        return "agregarDinero";
    }

    @PostMapping("/agregarDinero")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
    public String procesarFormularioAgregarDinero(@RequestParam String monto, RedirectAttributes flash) {
        try {
            // Eliminar puntos de miles y reemplazar coma decimal con punto
            String montoLimpio = monto.replace(".", "").replace(",", ".");

            Double montoNumerico = Double.parseDouble(montoLimpio);

            if (montoNumerico <= 0) {
                flash.addFlashAttribute("error", "El monto debe ser mayor que cero.");
                return "redirect:/caja/agregarDinero";
            }

            Caja caja = cajaService.obtenerCaja();
            if (caja != null) {
                caja.setCantidadActual(caja.getCantidadActual() + montoNumerico);
                cajaService.guardarCaja(caja);
                flash.addFlashAttribute("success", "El monto ha sido agregado correctamente.");
            } else {
                flash.addFlashAttribute("error", "No se pudo encontrar la caja.");
            }
        } catch (NumberFormatException e) {
            flash.addFlashAttribute("error", "Formato de monto inválido.");
            return "redirect:/caja/agregarDinero";
        }

        return "redirect:/listar";
    }

    @GetMapping("/editar")
    public String editarCaja(Model model) {
        Caja caja = cajaService.obtenerCaja();
        model.addAttribute("caja", caja);
        model.addAttribute("titulo", "Editar Dinero en Caja");
        return "editarCaja"; // Nombre de la vista
    }

    @PostMapping("/editarMonto")
    public String editarMonto(@RequestParam("cantidadActual") String cantidad) {
        double cantidadNumerica = Double.parseDouble(cantidad.replace(",", ""));

        // Asegúrate de que estás obteniendo la caja correcta (por ejemplo, la que tiene id = 1)
        Caja caja = cajaService.obtenerCajaPorId(1L); // Cambia 1L al id que necesitas
        caja.setCantidadActual(cantidadNumerica); // Actualiza la cantidad actual

        // Guarda la caja actualizada
        cajaService.guardarCaja(caja);

        return "redirect:/listar"; // Redirige después de guardar
    }

}

