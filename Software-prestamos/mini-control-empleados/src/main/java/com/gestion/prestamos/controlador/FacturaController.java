package com.gestion.prestamos.controlador;

import com.gestion.prestamos.entidades.Factura;
import com.gestion.prestamos.repositorios.FacturaRepository;
import com.gestion.prestamos.servicio.FactusApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FactusApiClient factusApiClient;
    @Autowired
    private FacturaRepository facturaRepository;

    // Define el logger
    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);


    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<?> listarFacturas() {
        try {
            List<Map<String, Object>> simplifiedFacturas = new ArrayList<>();
            List<Factura> facturas = factusApiClient.obtenerFacturas();

            for (Factura f : facturas) {
                Map<String, Object> facturaMap = new HashMap<>();
                facturaMap.put("id", f.getId());
                facturaMap.put("numero", f.getNumber());
                facturaMap.put("referenceCode", f.getReferenceCode());
                facturaMap.put("pagos", f.getFormaPago());
                facturaMap.put("metodopago", f.getMetodoPago());
                facturaMap.put("identificacion", f.getCliente().getIdentificacion());
                facturaMap.put("documentName", f.getDocumentName());
                facturaMap.put("graphicRepresentationName", f.getGraphicRepresentationName());
                facturaMap.put("status", f.getStatus());
                facturaMap.put("createdAt", f.getCreatedAt());
                facturaMap.put("total", f.getTotal());



                if (f.getCliente() != null) {
                    Map<String, Object> clienteMap = new HashMap<>();
                    clienteMap.put("id", f.getCliente().getId());
                    clienteMap.put("nombre", f.getCliente().getNombre());
                    // Agregar los campos adicionales del cliente
                    clienteMap.put("identificacion", f.getCliente().getIdentificacion());
                    clienteMap.put("correo", f.getCliente().getCorreo());
                    clienteMap.put("direccion", f.getCliente().getDireccion());
                    facturaMap.put("cliente", clienteMap);
                }

                simplifiedFacturas.add(facturaMap);
            }

            return ResponseEntity.ok(simplifiedFacturas);
        } catch (Exception e) {
            logger.error("Error al listar facturas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar facturas: " + e.getMessage()));
        }
    }

    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model, @AuthenticationPrincipal OAuth2User oAuth2User, Authentication authentication) {
        model.addAttribute("factura", new Factura());

        if (oAuth2User != null) {
            // Caso de OAuth2 (Google)
            String userName = oAuth2User.getAttribute("name");
            String userPicture = oAuth2User.getAttribute("picture");
            model.addAttribute("userName", userName != null ? userName : "Usuario");
            model.addAttribute("userPicture", userPicture != null ? userPicture : "");
        } else if (authentication != null) {
            // Caso de formLogin
            String userName = authentication.getName(); // Obtiene el nombre de usuario (por ejemplo, "admin")
            model.addAttribute("userName", userName != null ? userName : "Usuario");
            model.addAttribute("userPicture", ""); // No hay foto para formLogin
        } else {
            // Caso por defecto
            model.addAttribute("userName", "Usuario");
            model.addAttribute("userPicture", "");
        }

        return "/Facturas/Dashboard";
    }

    @PostMapping("/crear")
    @ResponseBody
    public ResponseEntity<?> crearFactura(@RequestBody Factura factura) {
        try {
            if (factura == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "La factura no puede ser nula"));
            }

            factusApiClient.eliminarTodasLasFacturasPendientes();
            String respuestaFactus = factusApiClient.crearFacturaEnFactus(factura);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Factura creada exitosamente");
            response.put("id", factura.getId());
            response.put("referenceCode", factura.getNumber());
            response.put("respuestaFactus", respuestaFactus);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al crear la factura: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFacturaById(@PathVariable Long id) {
        try {
            // Replace with your actual repository or service method
            Optional<Factura> facturaOptional = facturaRepository.findById(id);

            if (facturaOptional.isPresent()) {
                return ResponseEntity.ok(facturaOptional.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFacturaRest(@PathVariable Long id, @RequestBody Map<String, Object> facturaData) {
        try {
            // Find existing factura
            Optional<Factura> facturaOptional = facturaRepository.findById(id);
            if (!facturaOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Factura factura = facturaOptional.get();


            // Set date from JSON "fecha" field if present
            if (facturaData.containsKey("fecha") && facturaData.get("fecha") != null) {
                String fechaStr = (String) facturaData.get("fecha");
                if (!fechaStr.isEmpty()) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date fecha = format.parse(fechaStr);
                        factura.setCreatedAt(fecha); // Update createdAt with the fecha value
                    } catch (ParseException e) {
                        // Handle date parsing error
                        return ResponseEntity.badRequest().body("Formato de fecha inválido");
                    }
                }
            }

            // ... update other fields ...

            String respuesta = factusApiClient.actualizarFacturaEnFactus(id, factura);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la factura: " + e.getMessage());
        }
    }

    @GetMapping("/validar-y-descargar/{number}")
    @ResponseBody
    public ResponseEntity<?> validarYDescargarFactura(@PathVariable String number) {
        try {
            logger.info("Iniciando validación y descarga para factura número: {}", number);

            if (number == null || number.isEmpty()) {
                logger.error("El número de factura está vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El número de factura no es válido"));
            }

            // Intentar validar la factura
            logger.info("Validando factura con número: {}", number);
            try {
                factusApiClient.validarFactura(number);
                logger.info("Validación completada, esperando 2 segundos...");
                Thread.sleep(2000); // Esperar a que Factus procese la validación
            } catch (RuntimeException e) {
                logger.warn("No se pudo validar la factura número {}: {}", number, e.getMessage());
                // Continuamos para intentar descargar el PDF
            }

            // Descargar el PDF
            logger.info("Descargando PDF para factura número: {}", number);
            byte[] documentoPdf = factusApiClient.descargarFacturaPdf(number);

            if (documentoPdf != null && documentoPdf.length > 0) {
                logger.info("PDF generado correctamente para factura número: {}", number);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .header("Content-Disposition", "attachment; filename=\"factura-" + number + ".pdf\"")
                        .body(documentoPdf);
            } else {
                logger.warn("No se pudo generar el PDF para factura número: {}", number);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se pudo generar el PDF de la factura. Es posible que la factura no esté validada o no exista."));
            }
        } catch (Exception e) {
            logger.error("Error al validar y descargar la factura número {}: {}", number, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    @GetMapping("/descargar-xml/{number}")
    @ResponseBody
    public ResponseEntity<?> descargarXml(@PathVariable String number) {
        try {
            logger.info("Iniciando descarga de XML para factura número: {}", number);

            if (number == null || number.isEmpty()) {
                logger.error("El número de factura está vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El número de factura no es válido"));
            }

            logger.info("Descargando XML para factura número: {}", number);
            String xmlBase64 = factusApiClient.descargarFacturaXml(number);
            if (xmlBase64 == null || xmlBase64.isEmpty()) {
                logger.warn("No se obtuvo XML para factura número: {}", number);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se pudo obtener el XML de la factura"));
            }

            byte[] xmlBytes = Base64.getDecoder().decode(xmlBase64);
            logger.info("XML descargado y decodificado correctamente para factura número: {}", number);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml")
                    .header("Content-Disposition", "attachment; filename=\"factura-" + number + ".xml\"") // Añadí comillas al nombre del archivo
                    .body(xmlBytes);
        } catch (Exception e) {
            logger.error("Error al descargar el XML para factura número {}: {}", number, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al descargar el XML: " + e.getMessage()));
        }
    }

    @GetMapping("/ver/{id}")
    @ResponseBody
    public ResponseEntity<?> verFactura(@PathVariable Long id) {
        try {
            // Primero obtener la factura de tu base de datos local
            Optional<Factura> facturaOpt = facturaRepository.findById(id);
            if (!facturaOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Factura no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Factura factura = facturaOpt.get();

            // Verificar si la factura ya ha sido procesada en Factus
            if (factura.getStatus() != null && factura.getStatus().equals("CREADA")) {
                try {
                    // Si la factura está ya creada en Factus, intenta obtener los detalles
                    Map<String, Object> facturaDetalle = factusApiClient.obtenerDetalleFactura(id);

                    // Asegúrate de incluir referenceCode y formaPago, incluso si no vienen de Factus
                    facturaDetalle.put("referenceCode", factura.getReferenceCode() != null ? factura.getReferenceCode() : factura.getNumber());
                    facturaDetalle.put("formaPago", factura.getFormaPago());
                    facturaDetalle.put("metodoPago", factura.getMetodoPago());

                    // Agregar los ítems de la factura local al detalle
                    facturaDetalle.put("items", factura.getItems().stream().map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("id", item.getId());
                        itemMap.put("producto", item.getProducto().getName());
                        itemMap.put("cantidad", item.getCantidad());
                        itemMap.put("precio", item.getPrecio());
                        itemMap.put("porcentajeDescuento", item.getPorcentajeDescuento());
                        itemMap.put("subtotal", item.getSubtotal());
                        itemMap.put("iva", item.getIva());
                        itemMap.put("total", item.getTotal());
                        return itemMap;
                    }).collect(Collectors.toList()));

                    return ResponseEntity.ok(facturaDetalle);
                } catch (Exception e) {
                    // Si hay error al obtener la factura de Factus, devuelve información básica con ítems locales
                    Map<String, Object> facturaBasica = new HashMap<>();
                    facturaBasica.put("id", factura.getId());
                    facturaBasica.put("referenceCode", factura.getReferenceCode() != null ? factura.getReferenceCode() : factura.getNumber());
                    facturaBasica.put("documentName", factura.getDocumentName());
                    facturaBasica.put("status", factura.getStatus());
                    facturaBasica.put("createdAt", factura.getCreatedAt());
                    facturaBasica.put("formaPago", factura.getFormaPago());
                    facturaBasica.put("metodoPago", factura.getMetodoPago());
                    facturaBasica.put("items", factura.getItems().stream().map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("id", item.getId());
                        itemMap.put("producto", item.getProducto().getName());
                        itemMap.put("cantidad", item.getCantidad());
                        itemMap.put("precio", item.getPrecio());
                        itemMap.put("porcentajeDescuento", item.getPorcentajeDescuento());
                        itemMap.put("subtotal", item.getSubtotal());
                        itemMap.put("iva", item.getIva());
                        itemMap.put("total", item.getTotal());
                        return itemMap;
                    }).collect(Collectors.toList()));
                    facturaBasica.put("error", "No se pudo obtener detalle completo de Factus: " + e.getMessage());

                    return ResponseEntity.ok(facturaBasica);
                }
            } else {
                // Si la factura no ha sido procesada en Factus, devuelve información básica con ítems locales
                Map<String, Object> facturaBasica = new HashMap<>();
                facturaBasica.put("id", factura.getId());
                facturaBasica.put("referenceCode", factura.getReferenceCode() != null ? factura.getReferenceCode() : factura.getNumber());
                facturaBasica.put("documentName", factura.getDocumentName());
                facturaBasica.put("status", factura.getStatus() != null ? factura.getStatus() : "PENDIENTE");
                facturaBasica.put("createdAt", factura.getCreatedAt());
                facturaBasica.put("formaPago", factura.getFormaPago());
                facturaBasica.put("metodoPago", factura.getMetodoPago());
                facturaBasica.put("items", factura.getItems().stream().map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("producto", item.getProducto().getName());
                    itemMap.put("cantidad", item.getCantidad());
                    itemMap.put("precio", item.getPrecio());
                    itemMap.put("porcentajeDescuento", item.getPorcentajeDescuento());
                    itemMap.put("subtotal", item.getSubtotal());
                    itemMap.put("iva", item.getIva());
                    itemMap.put("total", item.getTotal());
                    return itemMap;
                }).collect(Collectors.toList()));
                facturaBasica.put("mensaje", "Esta factura aún no ha sido procesada en el sistema Factus");

                return ResponseEntity.ok(facturaBasica);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener la factura: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/descargar/{id}")
    @ResponseBody
    public ResponseEntity<?> descargarFactura(@PathVariable Long id, @RequestParam(required = false) boolean forceValidate) {
        try {
            // Obtener la factura de la base de datos local
            Optional<Factura> facturaOpt = facturaRepository.findById(id);
            if (!facturaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Factura no encontrada en el sistema"));
            }

            Factura factura = facturaOpt.get();
            String referenceCode = factura.getNumber(); // Usar el referenceCode, no el ID local

            // Validar la factura si es necesario
            if (forceValidate || "PENDIENTE".equals(factura.getStatus())) {
                try {
                    factusApiClient.validarFactura(referenceCode); // Usar el referenceCode
                    Thread.sleep(1000); // Esperar un momento para que se procese
                } catch (Exception e) {
                    System.err.println("Error al validar la factura: " + e.getMessage());
                }
            }

            // Descargar el PDF desde Factus
            byte[] documentoPdf = factusApiClient.descargarFacturaPdf(referenceCode); // Usar el referenceCode

            if (documentoPdf != null && documentoPdf.length > 0) {
                // Actualizar el estado de la factura
                if (!"CREADA".equals(factura.getStatus())) {
                    factura.setStatus("CREADA");
                    facturaRepository.save(factura);
                }

                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .header("Content-Disposition", "attachment; filename=factura-" + referenceCode + ".pdf")
                        .body(documentoPdf);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "No se pudo generar el PDF de la factura",
                                "id", id,
                                "status", factura.getStatus(),
                                "message", "La factura puede necesitar ser validada primero"
                        ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al descargar la factura: " + e.getMessage()));
        }
    }


    @GetMapping("/factura-download/{number}")
    public ResponseEntity<?> descargarFacturaPdf(@PathVariable String number) {
        try {
            // Obtener el PDF desde la API de Factus
            byte[] pdfBytes = factusApiClient.descargarFacturaPdf(number);

            // Verificar que el PDF no esté vacío
            if (pdfBytes != null && pdfBytes.length > 0) {
                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .header("Content-Disposition", "attachment; filename=factura-" + number + ".pdf")
                        .body(pdfBytes);
            } else {
                // Devuelve un JSON en caso de error
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No se pudo generar el PDF de la factura");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse);
            }
        } catch (Exception e) {
            // Log del error
            logger.error("Error al descargar el PDF de la factura " + number, e);

            // Devuelve un JSON en caso de error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al descargar el PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @DeleteMapping("/eliminar/{referenceCode}")
    @ResponseBody
    public ResponseEntity<?> eliminarFactura(@PathVariable String referenceCode) {
        try {
            logger.info("Iniciando eliminación para factura con referenceCode: {}", referenceCode);
            if (referenceCode == null || referenceCode.isEmpty()) {
                logger.error("El referenceCode de factura está vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El referenceCode de factura no es válido"));
            }
            logger.info("Eliminando factura con referenceCode: {}", referenceCode);
            String respuesta = factusApiClient.eliminarFactura(referenceCode);
            logger.info("Factura eliminada correctamente con referenceCode: {}", referenceCode);
            return ResponseEntity.ok(Map.of("message", "Factura eliminada correctamente", "respuesta", respuesta));
        } catch (RuntimeException e) {
            logger.error("Error al eliminar la factura con referenceCode {}: {}", referenceCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se pudo eliminar la factura: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la factura con referenceCode {}: {}", referenceCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al eliminar la factura: " + e.getMessage()));
        }
    }
}