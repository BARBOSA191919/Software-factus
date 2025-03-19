package com.gestion.prestamos.util.reportes;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gestion.prestamos.entidades.Prestamo;

public class PrestamoExporterExcel {

	private XSSFWorkbook libro;
	private XSSFSheet hoja;

	private List<Prestamo> listaPrestamos;

	public PrestamoExporterExcel(List<Prestamo> listaPrestamos) {
		this.listaPrestamos = listaPrestamos;
		libro = new XSSFWorkbook();
		hoja = libro.createSheet("Préstamos");
	}


	private void escribirCabeceraDeTabla() {
		Row fila = hoja.createRow(0);

		CellStyle estilo = libro.createCellStyle();
		XSSFFont fuente = libro.createFont();
		fuente.setBold(true);
		fuente.setFontHeight(16);
		estilo.setFont(fuente);

		// Cabeceras actualizadas
		Cell celda = fila.createCell(0);
		celda.setCellValue("ID");
		celda.setCellStyle(estilo);

		celda = fila.createCell(1);
		celda.setCellValue("Nombre");
		celda.setCellStyle(estilo);

		celda = fila.createCell(2);
		celda.setCellValue("Dirección");
		celda.setCellStyle(estilo);

		celda = fila.createCell(3);
		celda.setCellValue("Teléfono");
		celda.setCellStyle(estilo);

		celda = fila.createCell(4);
		celda.setCellValue("Monto del Préstamo");
		celda.setCellStyle(estilo);

		celda = fila.createCell(5);
		celda.setCellValue("Seguro");
		celda.setCellStyle(estilo);

		celda = fila.createCell(6);
		celda.setCellValue("Fecha del Préstamo");
		celda.setCellStyle(estilo);

		celda = fila.createCell(7);
		celda.setCellValue("Fecha de Pago");
		celda.setCellStyle(estilo);

		celda = fila.createCell(8);
		celda.setCellValue("Pago Diario");
		celda.setCellStyle(estilo);


		celda = fila.createCell(9);
		celda.setCellValue("Total a pagar");
		celda.setCellStyle(estilo);

		celda = fila.createCell(10);
		celda.setCellValue("Ganancias de prestamista");
		celda.setCellStyle(estilo);
	}

	private void escribirDatosDeLaTabla() {
		int numeroFilas = 1;

		CellStyle estilo = libro.createCellStyle();
		XSSFFont fuente = libro.createFont();
		fuente.setFontHeight(14);
		estilo.setFont(fuente);

		for (Prestamo prestamo : listaPrestamos) {
			Row fila = hoja.createRow(numeroFilas++);

			Cell celda = fila.createCell(0);
			celda.setCellValue(prestamo.getId());
			hoja.autoSizeColumn(0);
			celda.setCellStyle(estilo);

			celda = fila.createCell(1);
			celda.setCellValue(prestamo.getNombre());
			hoja.autoSizeColumn(1);
			celda.setCellStyle(estilo);

			celda = fila.createCell(2);
			celda.setCellValue(prestamo.getDireccion()); // Añadir dirección
			hoja.autoSizeColumn(2);
			celda.setCellStyle(estilo);

			celda = fila.createCell(3);
			celda.setCellValue(prestamo.getTelefono()); // Añadir teléfono
			hoja.autoSizeColumn(3);
			celda.setCellStyle(estilo);

			celda = fila.createCell(4);
			celda.setCellValue(prestamo.getMontoPrestamo());
			hoja.autoSizeColumn(4);
			celda.setCellStyle(estilo);

			celda = fila.createCell(5);
			celda.setCellValue(prestamo.getSeguro());
			hoja.autoSizeColumn(5);
			celda.setCellStyle(estilo);

			celda = fila.createCell(6);
			celda.setCellValue(prestamo.getFechaPrestamo().toString());
			hoja.autoSizeColumn(6);
			celda.setCellStyle(estilo);

			celda = fila.createCell(7);
			celda.setCellValue(prestamo.getFechaPago().toString());
			hoja.autoSizeColumn(7);
			celda.setCellStyle(estilo);

			celda = fila.createCell(8);
			celda.setCellValue(prestamo.getPagoDiario());
			hoja.autoSizeColumn(8);
			celda.setCellStyle(estilo);

			celda = fila.createCell(9);
			celda.setCellValue(prestamo.getTotal());
			hoja.autoSizeColumn(9);
			celda.setCellStyle(estilo);

			celda = fila.createCell(10);
			celda.setCellValue(prestamo.getGananciasPrestamista());
			hoja.autoSizeColumn(10);
			celda.setCellStyle(estilo);
		}
	}

	public void exportar(HttpServletResponse response) throws IOException {
		escribirCabeceraDeTabla();
		escribirDatosDeLaTabla();

		ServletOutputStream outPutStream = response.getOutputStream();
		libro.write(outPutStream);

		libro.close();
		outPutStream.close();
	}
}
