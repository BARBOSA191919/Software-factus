package com.gestion.prestamos.util.reportes;

import java.awt.Color;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.gestion.prestamos.entidades.Prestamo;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PrestamoExporterPDF {

	private List<Prestamo> listaPrestamos;

	public PrestamoExporterPDF(List<Prestamo> listaPrestamos) {
		this.listaPrestamos = listaPrestamos;
	}

	private void escribirCabeceraDeLaTabla(PdfPTable tabla) {
		PdfPCell celda = new PdfPCell();

		celda.setBackgroundColor(new Color(211, 211, 211, 211));
		celda.setPadding(5);

		Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
		fuente.setColor(Color.black);

		// Encabezados de la tabla actualizados
		celda.setPhrase(new Phrase("ID", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Nombre del Solicitante", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Dirección", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Teléfono", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Monto del Préstamo", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Tasa de interés", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Seguro", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Fecha del Préstamo", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Fecha de Pago", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Pago Diario", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Total a pagar", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Ganancias de prestamista", fuente));
		tabla.addCell(celda);
	}

	private void escribirDatosDeLaTabla(PdfPTable tabla) {
		for (Prestamo prestamo : listaPrestamos) {
			tabla.addCell(String.valueOf(prestamo.getId()));
			tabla.addCell(prestamo.getNombre());
			tabla.addCell(prestamo.getDireccion()); // Añadir dirección
			tabla.addCell(prestamo.getTelefono()); // Añadir teléfono
			tabla.addCell(String.valueOf(prestamo.getMontoPrestamo()));
			tabla.addCell(String.valueOf(prestamo.getTasaInteres()));
			tabla.addCell(String.valueOf(prestamo.getSeguro()));
			tabla.addCell(prestamo.getFechaPrestamo().toString());
			tabla.addCell(prestamo.getFechaPago().toString());
			tabla.addCell(String.valueOf(prestamo.getPagoDiario()));
			tabla.addCell(String.valueOf(prestamo.getTotal()));
			tabla.addCell(String.valueOf(prestamo.getGananciasPrestamista()));
		}
	}

	public void exportar(HttpServletResponse response) throws DocumentException, IOException {
		// Configurar documento
		Document documento = new Document(PageSize.A4.rotate()); // Orientación horizontal para mejor visualización
		documento.setMargins(20, 20, 40, 40); // Márgenes más amplios
		PdfWriter writer = PdfWriter.getInstance(documento, response.getOutputStream());

		// Configurar metadata del documento
		documento.addTitle("Reporte de Préstamos");
		documento.addCreator("Sistema de Gestión");
		documento.open();

		// Estilos de fuentes
		Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fuenteTitulo.setSize(16);
		fuenteTitulo.setColor(new Color(0, 0, 0)); // Azul corporativo

		Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA);
		fuenteSubtitulo.setSize(12);
		fuenteSubtitulo.setColor(new Color(16, 16, 16)); // Gris

		// Agregar título
		Paragraph titulo = new Paragraph("Lista de Préstamos", fuenteTitulo);
		titulo.setAlignment(Element.ALIGN_CENTER);
		titulo.setSpacingAfter(10);
		documento.add(titulo);

		// Agregar fecha usando LocalDateTime
		String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		Paragraph fecha = new Paragraph("Fecha de generación: " + fechaActual, fuenteSubtitulo);
		fecha.setAlignment(Element.ALIGN_RIGHT);
		fecha.setSpacingAfter(20);
		documento.add(fecha);

		// Configurar tabla
		PdfPTable tabla = new PdfPTable(12);
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(10);

		// Configurar anchos relativos de columnas
		float[] anchosColumnas = new float[]{
				1.0f,  // ID
				2.5f,  // Nombre
				2.5f,  // Apellido
				2.5f,  // Email
				2.0f,  // Teléfono
				1.4f,  // Fecha
				2.0f,  // Monto
				2.0f,  // Estado
				2.0f,  // Plazo
				2.0f,  // Tasa
				2.0f,  // Cuota
				2.0f   // Total
		};
		tabla.setWidths(anchosColumnas);

		// Establecer propiedades de la tabla
		tabla.getDefaultCell().setPadding(5);
		tabla.getDefaultCell().setBorderWidth(0.5f);
		tabla.getDefaultCell().setBorderColor(new Color(0, 0, 0)); // Gris claro
		tabla.setHeaderRows(1);

		escribirCabeceraDeLaTabla(tabla);
		escribirDatosDeLaTabla(tabla);

		documento.add(tabla);

		// Agregar pie de página
		Paragraph footer = new Paragraph("Documento generado automáticamente", fuenteSubtitulo);
		footer.setAlignment(Element.ALIGN_CENTER);
		documento.add(footer);

		documento.close();
	}
}
