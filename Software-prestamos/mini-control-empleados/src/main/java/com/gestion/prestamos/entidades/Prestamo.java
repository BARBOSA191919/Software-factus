package com.gestion.prestamos.entidades;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "prestamos")
public class Prestamo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	private String nombre;  // Nombre del cliente que recibe el préstamo

	private String direccion;  // direccion

	private String telefono;  // direccion

	private boolean incluirSeguro; // Indica si se incluye seguro

	@NotNull
	private Double montoPrestamo;  // Monto del préstamo

	@NotNull
	private Double seguro ;  // Seguro del préstamo, inicializado a 4000

	private String estado = "ACTIVO";
	private int diasAtraso = 0;
	private LocalDate fechaUltimoPago;

	@NotNull
	private Boolean valorModificadoManualmente = false;
	private Double valorPagoModificado; // Changed from Double to Boolean	@NotNull
	private Double pagoDiario = 0.0 ;  // Cuota diaria a pagar
	private String pago_semanal;
	private String pago_quincenal;
	private String pago_mensual;
	private Double cantidadInicial;
	private Double cantidadActual;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaPrestamo;  // Fecha en la que se realizó el préstamo

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaPago;  // Fecha límite de pago
	private double tasaInteres;

	private Double total ;

	private Double gananciasPrestamista ;

	private Double abono;

	private String tipoPago;


	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;  // Relación con el usuario que toma el préstamo

	public Prestamo() {
	}

	public Prestamo(String nombre,String pago_semanal, Double valorPagoModificado,Boolean valorModificadoManualmente,String tipoPago, String pago_quincenal, String pago_mensual,Double cantidadActual, boolean incluirSeguro,Double cantidadInicial, String direccion,String telefono, Double tasaInteres, Double montoPrestamo, Double seguro, Double pagoDiario, LocalDate fechaPrestamo, LocalDate fechaPago, Double gananciasPrestamista, Double abono) {
		this.nombre = nombre;
		this.montoPrestamo = montoPrestamo;
		this.direccion = direccion;
		this.telefono = telefono;
		this.seguro = seguro;
		this.pagoDiario = pagoDiario;
		this.pago_semanal = pago_semanal;
		this.pago_quincenal = pago_quincenal;
		this.pago_mensual = pago_mensual;
		this.fechaPrestamo = fechaPrestamo;
		this.fechaPago = fechaPago;
		this.total = calcularTotal();
		this.cantidadActual = cantidadActual;
		this.cantidadInicial = cantidadInicial;
		this.incluirSeguro = incluirSeguro;
		this.gananciasPrestamista = gananciasPrestamista;
		this.tasaInteres = tasaInteres;
		this.abono = abono;
		this.tipoPago = tipoPago;
		this.valorModificadoManualmente = valorModificadoManualmente;
		this.valorPagoModificado=valorPagoModificado;

	}


	public String formatearPago(String pago) {
		if (pago == null || pago.trim().isEmpty() || "N/A".equals(pago)) {
			return "N/A";
		}

		try {
			// Convertir a BigDecimal
			BigDecimal valor = convertirPago(pago);

			// Si el valor es cero, devolver "N/A"
			if (valor.compareTo(BigDecimal.ZERO) == 0) {
				return "N/A";
			}

			// Configurar formato personalizado
			DecimalFormat formatter = new DecimalFormat("$ #,##0.00");

			// Formatear y devolver
			return formatter.format(valor);
		} catch (Exception e) {
			return "N/A";
		}
	}

	public BigDecimal convertirPago(String pago) {
		if (pago == null || pago.trim().isEmpty() || "N/A".equals(pago)) {
			return BigDecimal.ZERO;
		}

		try {
			// Eliminar caracteres no numéricos excepto punto y coma
			String valorLimpio = pago.replaceAll("[^0-9.,]", "")
					.replace(",", ".");

			// Manejar casos con múltiples puntos decimales
			if ((valorLimpio.indexOf('.') != valorLimpio.lastIndexOf('.'))) {
				// Si hay múltiples puntos, mantener solo el último
				valorLimpio = valorLimpio.substring(valorLimpio.lastIndexOf('.') - valorLimpio.length() + 1);
			}

			return new BigDecimal(valorLimpio)
					.setScale(2, RoundingMode.HALF_UP);
		} catch (NumberFormatException | ArithmeticException e) {
			return BigDecimal.ZERO;
		}
	}

	// Métodos adicionales para formateo consistente
	public String getPagoSemanalFormateado() {
		return formatearPago(this.pago_semanal);
	}

	public String getPagoQuincenalFormateado() {
		return formatearPago(this.pago_quincenal);
	}

	public String getPagoMensualFormateado() {
		return formatearPago(this.pago_mensual);
	}


	public String getPago_semanal() {
		return pago_semanal;
	}

	public void setPago_semanal(String pago_semanal) {
		this.pago_semanal = pago_semanal;
	}

	public String getPago_quincenal() {
		return pago_quincenal;
	}

	public void setPago_quincenal(String pago_quincenal) {
		this.pago_quincenal = pago_quincenal;
	}

	public String getPago_mensual() {
		return pago_mensual;
	}

	public void setPago_mensual(String pago_mensual) {
		this.pago_mensual = pago_mensual;
	}


	public Double getGananciasPrestamista() {
		return gananciasPrestamista;

	}

	public void agregarAbono(Double montoAbono) {
		if (montoAbono == null || montoAbono <= 0) {
			throw new IllegalArgumentException("El monto del abono debe ser mayor a cero");
		}

		// Validar que el abono no exceda el total pendiente
		double totalPendiente = getTotalPendiente();
		if (montoAbono > totalPendiente) {
			throw new IllegalArgumentException("El abono no puede ser mayor que el monto pendiente");
		}

		// Agregar el abono al acumulado
		if (this.abono == null) {
			this.abono = 0.0;
		}
		this.abono += montoAbono;

		// Actualizar la cantidad actual y el total
		this.cantidadActual = this.getTotalPendiente();
		this.total = this.calcularTotal() - this.abono;

		// Verificar si el préstamo está completamente pagado
		if (estaPagado()) {
			this.fechaPago = LocalDate.now();
		}
	}


	public double calcularTotal() {
		// Calcular el interés total
		double interes = (montoPrestamo * tasaInteres) / 100; // Asegúrate de que la tasa de interés esté en porcentaje

		// Calcular el total sin seguro
		double totalSinSeguro = montoPrestamo + interes;

		// Agregar seguro si está incluido
		if (incluirSeguro) {
			seguro = (montoPrestamo / 100000) * 4000.0; // Calcular el seguro
			totalSinSeguro += seguro; // Sumar el seguro al total
		} else {
			seguro = 0.0; // Asegúrate de que el seguro sea cero si no está incluido
		}

		// Redondear el total a dos decimales
		return Math.round(totalSinSeguro * 100.0) / 100.0;
	}

	public Double getTotalPendiente() {
		double totalCalculado = calcularTotal(); // Calcula el total actual
		double abonos = (this.abono != null) ? this.abono : 0.0; // Obtiene el total de abonos
		return Math.max(0.0, totalCalculado - abonos); // Retorna la diferencia
	}

	// Metodo para verificar si el préstamo está pagado completamente
	public boolean estaPagado() {
		return getTotalPendiente() <= 0;
	}

	// Metodo para calcular el porcentaje pagado
	public double getPorcentajePagado() {
		if (total == null || total == 0) {
			return 0.0;
		}
		double porcentaje = (abono != null ? abono : 0.0) * 100.0 / total;
		return Math.round(porcentaje * 100.0) / 100.0;
	}

	public void setMontoPrestamo(Double montoPrestamo) {
		// Redondear el monto del préstamo a múltiplos de 1000
		this.montoPrestamo = Math.round(montoPrestamo / 1000.0) * 1000.0;
	}

	public boolean isPagoRetrasado() {
		return LocalDate.now().isAfter(this.fechaPago);
	}

	public String getEstadoPago() {
		LocalDate hoy = LocalDate.now();

		if (estaPagado()) {
			return "Pagado";
		}

		if (isPagoRetrasado()) {
			return "Retrasado";
		}

		return "Al día";
	}

	// Metodo para calcular días de retraso
	public long getDiasRetraso() {
		if (!isPagoRetrasado()) {
			return 0;
		}
		return ChronoUnit.DAYS.between(fechaPago, LocalDate.now());
	}


	// Métodos para obtener fechas formateadas
	public String getFormattedFechaPrestamo() {
		return fechaPrestamo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	public String getFormattedFechaPago() {
		return fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	public Double getAbono() {
		return abono != null ? abono : 0.0;
	}

	public void setAbono(Double abono) {
		this.abono = abono;
		this.total = calcularTotal();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public @NotEmpty String getNombre() {
		return nombre;
	}

	public void setNombre(@NotEmpty String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public boolean isIncluirSeguro() {
		return incluirSeguro;
	}

	public void setIncluirSeguro(boolean incluirSeguro) {
		this.incluirSeguro = incluirSeguro;
	}

	public @NotNull Double getMontoPrestamo() {
		return montoPrestamo;
	}

	public @NotNull Double getSeguro() {
		return seguro;
	}

	public void setSeguro(@NotNull Double seguro) {
		this.seguro = seguro;
	}

	public @NotNull Double getPagoDiario() {
		return pagoDiario;
	}

	public void setPagoDiario(@NotNull Double pagoDiario) {
		this.pagoDiario = pagoDiario;
	}

	public Double getCantidadInicial() {
		return cantidadInicial;
	}

	public void setCantidadInicial(Double cantidadInicial) {
		this.cantidadInicial = cantidadInicial;
	}

	public Double getCantidadActual() {
		return cantidadActual;
	}

	public void setCantidadActual(Double cantidadActual) {
		this.cantidadActual = cantidadActual;
	}

	public @NotNull LocalDate getFechaPrestamo() {
		return fechaPrestamo;
	}

	public void setFechaPrestamo(@NotNull LocalDate fechaPrestamo) {
		this.fechaPrestamo = fechaPrestamo;
	}

	public @NotNull LocalDate getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(@NotNull LocalDate fechaPago) {
		this.fechaPago = fechaPago;
	}

	public double getTasaInteres() {
		return tasaInteres;
	}

	public void setTasaInteres(double tasaInteres) {
		this.tasaInteres = tasaInteres;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public void setGananciasPrestamista(Double gananciasPrestamista) {
		this.gananciasPrestamista = gananciasPrestamista;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getValorModificadoManualmente() {
		return valorModificadoManualmente;
	}

	public void setValorModificadoManualmente(Boolean valorModificadoManualmente) {
		this.valorModificadoManualmente = valorModificadoManualmente;
	}

	public Double getValorPagoModificado() {
		return valorPagoModificado;
	}

	public void setValorPagoModificado(Double valorPagoModificado) {
		this.valorPagoModificado = valorPagoModificado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getDiasAtraso() {
		return diasAtraso;
	}

	public void setDiasAtraso(int diasAtraso) {
		this.diasAtraso = diasAtraso;
	}

	public LocalDate getFechaUltimoPago() {
		return fechaUltimoPago;
	}

	public void setFechaUltimoPago(LocalDate fechaUltimoPago) {
		this.fechaUltimoPago = fechaUltimoPago;
	}

	// Método de utilidad para verificar si el préstamo está atrasado según su tipo de pago
	public boolean isAtrasadoSegunTipoPago() {
		if (estaPagado()) {
			return false;
		}

		LocalDate fechaReferencia = fechaUltimoPago != null ? fechaUltimoPago : fechaPrestamo;
		LocalDate fechaActual = LocalDate.now();

		switch (tipoPago.toUpperCase()) {
			case "DIARIO":
				return ChronoUnit.DAYS.between(fechaReferencia, fechaActual) > 1;
			case "SEMANAL":
				return ChronoUnit.DAYS.between(fechaReferencia, fechaActual) > 7;
			case "QUINCENAL":
				return ChronoUnit.DAYS.between(fechaReferencia, fechaActual) > 15;
			case "MENSUAL":
				return ChronoUnit.DAYS.between(fechaReferencia, fechaActual) > 30;
			default:
				return ChronoUnit.DAYS.between(fechaReferencia, fechaActual) > 1; // Por defecto, diario
		}
	}

	// Método para actualizar el estado del préstamo
	public void actualizarEstado() {
		if (estaPagado()) {
			this.estado = "PAGADO";
			this.diasAtraso = 0;
		} else if (isAtrasadoSegunTipoPago()) {
			this.estado = "ATRASADO";
			LocalDate fechaReferencia = fechaUltimoPago != null ? fechaUltimoPago : fechaPrestamo;
			this.diasAtraso = (int) ChronoUnit.DAYS.between(fechaReferencia, LocalDate.now());
		} else {
			this.estado = "ACTIVO";
			this.diasAtraso = 0;
		}
	}

}