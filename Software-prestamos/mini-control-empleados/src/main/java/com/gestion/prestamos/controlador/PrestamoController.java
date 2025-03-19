package com.gestion.prestamos.controlador;

import java.io.IOException;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import com.gestion.prestamos.entidades.Caja;
import com.gestion.prestamos.servicio.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.prestamos.entidades.Prestamo;
import com.gestion.prestamos.servicio.PrestamoService;
import com.gestion.prestamos.util.paginacion.PageRender;
import com.gestion.prestamos.util.reportes.PrestamoExporterExcel;
import com.gestion.prestamos.util.reportes.PrestamoExporterPDF;
import com.lowagie.text.DocumentException;

import static org.thymeleaf.util.NumberUtils.formatCurrency;

@Controller
public class PrestamoController {


	@Autowired
	private PrestamoService prestamoService; // Servicio que maneja los préstamos

	@Autowired
	private CajaService cajaService; // Asegúrate de inyectar el servicio de Caja

	@GetMapping("/prestamos/estado")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public ResponseEntity<?> checkPrestamoStatus(@RequestParam Long prestamoId) {
		try {
			Prestamo prestamo = prestamoService.findOne(prestamoId);
			if (prestamo == null) {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body("Préstamo no encontrado con ID: " + prestamoId);
			}

			// Verificar estado del préstamo
			actualizarEstadoPrestamo(prestamo);
			prestamoService.save(prestamo);

			Map<String, Object> statusResponse = new HashMap<>();
			statusResponse.put("totalPendiente", prestamo.getTotalPendiente());
			statusResponse.put("total", prestamo.getTotal());
			statusResponse.put("estaPagado", prestamo.estaPagado());
			statusResponse.put("fechaPago", prestamo.getFechaPago());
			statusResponse.put("estado", prestamo.getEstado());
			statusResponse.put("diasAtraso", prestamo.getDiasAtraso());

			return ResponseEntity.ok(statusResponse);
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener estado del préstamo: " + e.getMessage());
		}
	}

	private void actualizarEstadoPrestamo(Prestamo prestamo) {
		if (prestamo.estaPagado()) {
			prestamo.setEstado("PAGADO");
			prestamo.setDiasAtraso(0);
			return;
		}

		LocalDate fechaActual = LocalDate.now(); // Fecha actual
		LocalDate ultimoPago = prestamo.getFechaUltimoPago() != null ?
				prestamo.getFechaUltimoPago() :
				prestamo.getFechaPrestamo(); // Usar fecha de préstamo si no hay pagos

		// Calcular horas desde el último pago
		long horasDesdeUltimoPago = ChronoUnit.HOURS.between(ultimoPago.atStartOfDay(), fechaActual.atStartOfDay());

		if (horasDesdeUltimoPago > 24) {
			prestamo.setEstado("ATRASADO");
			prestamo.setDiasAtraso((int) (horasDesdeUltimoPago / 24)); // Convertir horas a días
		} else {
			prestamo.setEstado("ACTIVO");
			prestamo.setDiasAtraso(0);
		}
	}

	private String determinarFrecuenciaPago(Prestamo prestamo) {
		// Determinar la frecuencia basada en los pagos configurados
		if (!prestamo.getPago_mensual().equals("No cumple el mes")) {
			return "MENSUAL";
		} else if (!prestamo.getPago_quincenal().equals("No cumple la quincena")) {
			return "QUINCENAL";
		} else if (!prestamo.getPago_semanal().equals("No cumple la semana")) {
			return "SEMANAL";
		} else {
			return "DIARIO";
		}
	}

	@PostMapping("/abonar/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public String procesarAbono(@PathVariable Long id,
								@RequestParam("abono") Double montoAbono,
								RedirectAttributes flash) {
		try {
			Prestamo prestamo = prestamoService.findOne(id);
			if (prestamo == null) {
				flash.addFlashAttribute("error", "El préstamo no existe en la base de datos");
				return "redirect:/listar";
			}

			// Validar monto de abono
			if (montoAbono <= 0) {
				flash.addFlashAttribute("error", "El monto del abono debe ser mayor que cero");
				return "redirect:/listar";
			}

			// Agregar el abono
			prestamo.agregarAbono(montoAbono);
			prestamo.setFechaUltimoPago(LocalDate.now()); // Actualizar fecha del último pago

			// Actualizar estado del préstamo
			actualizarEstadoPrestamo(prestamo);

			// Guardar el préstamo actualizado
			prestamoService.save(prestamo);

			flash.addFlashAttribute("success", "Abono procesado correctamente. Nuevo total pendiente: $" +
					String.format("%.2f", prestamo.getTotalPendiente()));
		} catch (Exception e) {
			flash.addFlashAttribute("error", "Error al procesar el abono: " + e.getMessage());
		}

		return "redirect:/listar";
	}



	@GetMapping("/agregarDinero")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public String mostrarFormularioAgregarDinero(Model modelo) {
		modelo.addAttribute("titulo", "Agregar Dinero a la Caja");

		return "agregarDinero"; // Crea un nuevo HTML para esto
	}

	@PostMapping("/agregarDinero")
	public String guardarDinero(@ModelAttribute("caja") @Valid Caja caja, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "agregarDinero";  // Regresar al formulario en caso de error
		}

		// Lógica para guardar la caja
		redirectAttributes.addFlashAttribute("success", "Dinero agregado correctamente.");
		return "redirect:/listar";  // Redirigir a la lista después de guardar
	}




	@GetMapping({"/", "/listar", ""})
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public String listarPrestamos(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
		Pageable pageRequest = PageRequest.of(page, 9);
		Page<Prestamo> prestamos = prestamoService.findAll(pageRequest);
		PageRender<Prestamo> pageRender = new PageRender<>("/listar", prestamos);

		modelo.addAttribute("titulo", "Listado de préstamos");
		modelo.addAttribute("prestamos", prestamos);
		modelo.addAttribute("page", pageRender);
		modelo.addAttribute("caja", cajaService.getCajaActual());

		return "listar";
	}

	@GetMapping("/form")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public String mostrarFormularioDeRegistrarPrestamo(Model model, RedirectAttributes flash) {
		Prestamo prestamo = new Prestamo();
		Caja caja = cajaService.obtenerCaja();

		// Verificar si la caja está vacía
		if (caja.getCantidadActual() <= 0) {
			flash.addFlashAttribute("warning", "La caja está vacía, necesita agregar más dinero.");
		}

		model.addAttribute("prestamo", prestamo);
		model.addAttribute("caja", caja);
		model.addAttribute("titulo", "Registro de préstamo");

		return "form";
	}


	@PostMapping("/form")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public String guardarPrestamo(@Valid Prestamo prestamo,
								  BindingResult result,
								  Model modelo,
								  RedirectAttributes flash,
								  SessionStatus status,
								  @RequestParam(value = "montoOriginal", required = false) Double montoOriginal,
								  @RequestParam(value = "valorModificadoManualmente", required = false, defaultValue = "false") Boolean valorModificadoManualmente,
								  @RequestParam(value = "valorPagoModificado", required = false) Double valorPagoModificado)
	{
		if (result.hasErrors()) {
			modelo.addAttribute("titulo", "Registro de préstamo");
			return "form";
		}

		// Verificar saldo disponible
		if (montoOriginal == null || !montoOriginal.equals(prestamo.getMontoPrestamo())) {
			if (montoOriginal != null) {
				cajaService.agregarMonto(montoOriginal);
			}
			if (!cajaService.saldoSuficiente(prestamo.getMontoPrestamo())) {
				flash.addFlashAttribute("error", "Saldo insuficiente en la caja");
				return "form";
			}
		}

		// Verificar fechas
		if (prestamo.getFechaPrestamo() == null || prestamo.getFechaPago() == null) {
			flash.addFlashAttribute("error", "Las fechas deben estar completas");
			return "form";
		}

		// Calcular días del préstamo
		long diasDelPrestamo = ChronoUnit.DAYS.between(
				prestamo.getFechaPrestamo(),
				prestamo.getFechaPago()
		);

		if (diasDelPrestamo <= 0) {
			flash.addFlashAttribute("error", "La fecha de pago debe ser posterior a la fecha del préstamo");
			return "form";
		}

		// Calcular número de meses
		int mesesDelPrestamo = (int) ChronoUnit.MONTHS.between(
				prestamo.getFechaPrestamo(),
				prestamo.getFechaPago()
		);
		// Asegurar al menos 1 mes si el resultado es 0
		mesesDelPrestamo = Math.max(mesesDelPrestamo, 1);

		// Cálculo de intereses e inclusión de seguro
		double interes = Math.round((prestamo.getMontoPrestamo() * prestamo.getTasaInteres()) / 100);
		double seguro = 0.0;

		if (prestamo.isIncluirSeguro() && prestamo.getMontoPrestamo() >= 100000) {
			seguro = Math.round((prestamo.getMontoPrestamo() / 100000.0) * 4000.0);
		}

		// Calcular total a pagar
		double totalAPagar = prestamo.getMontoPrestamo() + interes + seguro;
		prestamo.setTotal(totalAPagar);
		prestamo.setSeguro(seguro);
		prestamo.setGananciasPrestamista(interes);

		// Si el valor fue modificado manualmente
		if (valorModificadoManualmente && valorPagoModificado != null) {
			prestamo.setPagoDiario(valorPagoModificado);
		} else {
			// Cálculo de pagos según frecuencia
			switch (prestamo.getTipoPago()) {
				case "diario":
					prestamo.setPagoDiario(Double.valueOf(Math.round(totalAPagar / diasDelPrestamo)));
					break;
				case "semanal":
					double pagoSemanal = totalAPagar / (4.0 * mesesDelPrestamo); // 4 semanas por mes
					prestamo.setPagoDiario(pagoSemanal / 7);
					prestamo.setPago_semanal(String.valueOf(Math.round(pagoSemanal)));
					break;
				case "quincenal":
					double pagoQuincenal = totalAPagar / (2.0 * mesesDelPrestamo); // 2 quincenas por mes
					prestamo.setPagoDiario(pagoQuincenal / 15);
					prestamo.setPago_quincenal(String.valueOf(Math.round(pagoQuincenal)));
					break;
				case "mensual":
					double pagoMensual = totalAPagar / mesesDelPrestamo;
					prestamo.setPagoDiario(pagoMensual / 30);
					prestamo.setPago_mensual(String.valueOf(Math.round(pagoMensual)));
					break;
			}
		}

		// Calcular pagos para diferentes periodos basados en el pago diario
		prestamo.setPago_semanal(String.valueOf(Math.round(prestamo.getPagoDiario() * 7)));
		prestamo.setPago_quincenal(String.valueOf(Math.round(prestamo.getPagoDiario() * 15)));
		prestamo.setPago_mensual(String.valueOf(Math.round(prestamo.getPagoDiario() * 30)));

		prestamo.setCantidadInicial(totalAPagar);
		prestamo.setCantidadActual(totalAPagar);

		cajaService.restarMonto(prestamo.getMontoPrestamo());
		prestamoService.save(prestamo);

		status.setComplete();
		flash.addFlashAttribute("success", "Préstamo registrado o editado con éxito");
		return "redirect:/listar";
	}

	@GetMapping("/form/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public String editarPrestamo(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		Prestamo prestamo = prestamoService.findOne(id);

		if (prestamo == null) {
			flash.addFlashAttribute("error", "El ID del préstamo no existe en la base de datos");
			return "redirect:/listar";
		}

		// Almacena el monto original del préstamo
		model.addAttribute("montoOriginal", prestamo.getMontoPrestamo());

		Caja caja = cajaService.getCajaActual();
		model.addAttribute("prestamo", prestamo);
		model.addAttribute("caja", caja);
		model.addAttribute("titulo", "Edición de préstamo");
		return "form";
	}

	@GetMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String eliminarPrestamo(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			prestamoService.delete(id);
			flash.addFlashAttribute("success", "Préstamo eliminado con éxito");
		}
		return "redirect:/listar";
	}

	@GetMapping("/exportarPDF")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public void exportarListadoDePrestamosEnPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());

		String cabecera = "Content-Disposition";
		String valor = "inline; filename=Prestamos_" + fechaActual + ".pdf";

		response.setHeader(cabecera, valor);

		List<Prestamo> prestamos = prestamoService.findAll();

		PrestamoExporterPDF exporter = new PrestamoExporterPDF(prestamos);
		exporter.exportar(response);
	}

	@GetMapping("/exportarExcel")
	@PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
	public void exportarListadoDePrestamosEnExcel(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/octet-stream");

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());

		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Prestamos_" + fechaActual + ".xlsx";

		response.setHeader(cabecera, valor);

		List<Prestamo> prestamos = prestamoService.findAll();

		PrestamoExporterExcel exporter = new PrestamoExporterExcel(prestamos);
		exporter.exportar(response);

	}



}