package com.gestion.factus.controlador;

import com.gestion.factus.entidades.Factura;
import com.gestion.factus.entidades.Item;
import com.gestion.factus.entidades.Tributo;
import com.gestion.factus.repositorios.FacturaRepository;
import com.gestion.factus.servicio.FactusApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private final FactusApiClient factusApiClient;
    @Autowired
    private FacturaRepository facturaRepository;

    public FacturaController(FactusApiClient factusApiClient) {
        this.factusApiClient = factusApiClient;
    }

    // Define el logger
    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);
    private JEditorPane request;

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<?> listarFacturas() {
        try {
            List<Map<String, Object>> simplifiedFacturas = new ArrayList<>();
            List<Factura> facturas = facturaRepository.findAll(); // Usar repositorio local en lugar de Factus

            for (Factura f : facturas) {
                Map<String, Object> facturaMap = new HashMap<>();
                facturaMap.put("id", f.getId());
                facturaMap.put("numero", f.getNumber());
                facturaMap.put("referenceCode", f.getReferenceCode());
                facturaMap.put("pagos", f.getFormaPago());
                facturaMap.put("metodopago", f.getMetodoPago());
                facturaMap.put("identificacion", f.getCliente() != null ? f.getCliente().getIdentificacion() : null);
                facturaMap.put("documentName", f.getDocumentName());
                facturaMap.put("graphicRepresentationName", f.getGraphicRepresentationName());
                facturaMap.put("status", f.getStatus());
                facturaMap.put("createdAt", f.getCreatedAt());
                facturaMap.put("total", f.getTotal());
                // Nuevos campos
                facturaMap.put("municipio", f.getMunicipio());
                facturaMap.put("fechaVencimiento", f.getFechaVencimiento());
                facturaMap.put("inc", f.getInc());
                facturaMap.put("tributos", f.getTributos().stream()
                        .map(t -> Map.of("tributeId", t.getTributeId(), "rate", t.getRate(), "amount", t.getAmount()))
                        .collect(Collectors.toList()));

                if (f.getCliente() != null) {
                    Map<String, Object> clienteMap = new HashMap<>();
                    clienteMap.put("id", f.getCliente().getId());
                    clienteMap.put("nombre", f.getCliente().getNombre());
                    clienteMap.put("identificacion", f.getCliente().getIdentificacion());
                    clienteMap.put("correo", f.getCliente().getCorreo());
                    clienteMap.put("direccion", f.getCliente().getDireccion());
                    clienteMap.put("municipioId", f.getCliente().getMunicipioId());
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

    @PostMapping(value = "/crear", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> crearFactura(@RequestBody Factura factura, HttpServletRequest request) {
        logger.info("Received Content-Type: {}", request.getContentType());
        logger.info("Factura payload: {}", factura);
        try {
            if (factura == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "La factura no puede ser nula"));
            }
            factusApiClient.eliminarTodasLasFacturasPendientes();
            String respuestaFactus = factusApiClient.crearFacturaEnFactus(factura);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Factura creada exitosamente");
            response.put("id", factura.getId());
            response.put("referenceCode", factura.getReferenceCode());
            response.put("respuestaFactus", respuestaFactus);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Runtime error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al crear la factura: " + e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getFacturaById(@PathVariable Long id) {
        try {
            Optional<Factura> facturaOptional = facturaRepository.findById(id);

            if (facturaOptional.isPresent()) {
                Factura factura = facturaOptional.get();
                Map<String, Object> facturaMap = new HashMap<>();
                facturaMap.put("id", factura.getId());
                facturaMap.put("numero", factura.getNumber());
                facturaMap.put("referenceCode", factura.getReferenceCode());
                facturaMap.put("formaPago", factura.getFormaPago());
                facturaMap.put("metodoPago", factura.getMetodoPago());
                facturaMap.put("documentName", factura.getDocumentName());
                facturaMap.put("graphicRepresentationName", factura.getGraphicRepresentationName());
                facturaMap.put("status", factura.getStatus());
                facturaMap.put("createdAt", factura.getCreatedAt());
                facturaMap.put("subtotal", factura.getSubtotal());
                facturaMap.put("totalIva", factura.getTotalIva());
                facturaMap.put("totalDescuento", factura.getTotalDescuento());
                facturaMap.put("total", factura.getTotal());
                facturaMap.put("municipio", factura.getMunicipio());
                facturaMap.put("fechaVencimiento", factura.getFechaVencimiento());
                facturaMap.put("inc", factura.getInc());
                facturaMap.put("tributos", factura.getTributos().stream()
                        .map(t -> Map.of("tributeId", t.getTributeId(), "rate", t.getRate(), "amount", t.getAmount()))
                        .collect(Collectors.toList()));

                if (factura.getCliente() != null) {
                    Map<String, Object> clienteMap = new HashMap<>();
                    clienteMap.put("id", factura.getCliente().getId());
                    clienteMap.put("nombre", factura.getCliente().getNombre());
                    clienteMap.put("identificacion", factura.getCliente().getIdentificacion());
                    clienteMap.put("correo", factura.getCliente().getCorreo());
                    clienteMap.put("direccion", factura.getCliente().getDireccion());
                    clienteMap.put("municipioId", factura.getCliente().getMunicipioId());
                    facturaMap.put("cliente", clienteMap);
                }

                facturaMap.put("items", factura.getItems().stream().map(item -> {
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

                return ResponseEntity.ok(facturaMap);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Factura no encontrada"));
            }
        } catch (Exception e) {
            logger.error("Error al obtener factura {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la factura: " + e.getMessage()));
        }
    }

   // Endpoint para obtener los datos de una factura por referenceCode
    @GetMapping("/{referenceCode}")
    public ResponseEntity<?> obtenerFactura(@PathVariable String referenceCode) {
        try {
            logger.info("Obteniendo factura con referenceCode: {}", referenceCode);
            if (referenceCode == null || referenceCode.isEmpty()) {
                logger.error("El referenceCode de factura está vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El referenceCode de factura no es válido"));
            }

            Optional<Factura> facturaOpt = facturaRepository.findByReferenceCode(referenceCode);
            if (!facturaOpt.isPresent()) {
                logger.warn("Factura con referenceCode {} no encontrada", referenceCode);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Factura no encontrada: " + referenceCode));
            }

            Factura factura = facturaOpt.get();
            logger.info("Factura encontrada: {}", factura);
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            logger.error("Error al obtener la factura con referenceCode {}: {}", referenceCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al obtener la factura: " + e.getMessage()));
        }
    }

    // Endpoint para actualizar una factura
    @PutMapping("/editar/{referenceCode}")
    @Transactional
    public ResponseEntity<?> editarFactura(@PathVariable String referenceCode, @RequestBody Factura facturaActualizada) {
        try {
            logger.info("Iniciando edición para factura con referenceCode: {}", referenceCode);
            if (referenceCode == null || referenceCode.isEmpty()) {
                logger.error("El referenceCode de factura está vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El referenceCode de factura no es válido"));
            }

            Optional<Factura> facturaOpt = facturaRepository.findByReferenceCode(referenceCode);
            if (!facturaOpt.isPresent()) {
                logger.warn("Factura con referenceCode {} no encontrada", referenceCode);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Factura no encontrada: " + referenceCode));
            }

            Factura factura = facturaOpt.get();

            // Validar estado
            if ("CREADA".equals(factura.getStatus())) {
                logger.warn("Intento de editar factura validada con referenceCode: {}", referenceCode);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "No se puede editar la factura porque está validada: " + referenceCode));
            }

            // Validar datos de entrada
            if (facturaActualizada.getCliente() == null || facturaActualizada.getItems().isEmpty()) {
                logger.error("Faltan datos requeridos: cliente o ítems");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La factura debe tener un cliente y al menos un ítem"));
            }

            // Actualizar campos principales
            factura.setCliente(facturaActualizada.getCliente());
            factura.setFormaPago(facturaActualizada.getFormaPago());
            factura.setMetodoPago(facturaActualizada.getMetodoPago());
            factura.setCreatedAt(facturaActualizada.getCreatedAt());
            factura.setFechaVencimiento(facturaActualizada.getFechaVencimiento());
            factura.setMunicipio(facturaActualizada.getMunicipio());
            factura.setNumberingRangeId(facturaActualizada.getNumberingRangeId());

            // Actualizar ítems
            factura.getItems().clear();
            for (Item item : facturaActualizada.getItems()) {
                if (item.getProducto() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                    logger.error("Ítem inválido: producto nulo o cantidad no válida");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Ítem inválido: debe tener un producto y una cantidad válida"));
                }
                item.setFactura(factura);
                factura.getItems().add(item);
            }

            // Actualizar tributos
            factura.getTributos().clear();
            for (Tributo tributo : facturaActualizada.getTributos()) {
                tributo.setFactura(factura);
                factura.getTributos().add(tributo);
            }

            // Recalcular totales
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalIva = BigDecimal.ZERO;
            BigDecimal totalDescuento = BigDecimal.ZERO;
            BigDecimal total = BigDecimal.ZERO;

            for (Item item : factura.getItems()) {
                BigDecimal itemSubtotal = item.getPrecio().multiply(item.getCantidad());
                BigDecimal itemDescuento = itemSubtotal.multiply(item.getPorcentajeDescuento().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                BigDecimal itemIva = itemSubtotal.multiply(item.getIva().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                BigDecimal itemTotal = itemSubtotal.subtract(itemDescuento).add(itemIva);

                subtotal = subtotal.add(itemSubtotal);
                totalDescuento = totalDescuento.add(itemDescuento);
                totalIva = totalIva.add(itemIva);
                total = total.add(itemTotal);

                item.setSubtotal(itemSubtotal);
                item.setIva(itemIva);
                item.setTotal(itemTotal);
            }

            factura.setSubtotal(subtotal);
            factura.setTotalIva(totalIva);
            factura.setTotalDescuento(totalDescuento);
            factura.setTotal(total);

            // Guardar cambios
            facturaRepository.save(factura);

            // Opcional: Actualizar en Factus
            String respuestaFactus = factusApiClient.actualizarFacturaEnFactus(factura.getId(), factura);
            logger.info("Factura con referenceCode {} actualizada correctamente. Respuesta Factus: {}", referenceCode, respuestaFactus);

            return ResponseEntity.ok(Map.of(
                    "message", "Factura actualizada correctamente",
                    "factura", factura,
                    "respuestaFactus", respuestaFactus
            ));
        } catch (Exception e) {
            logger.error("Error al actualizar la factura con referenceCode {}: {}", referenceCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al actualizar la factura: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/municipios.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getMunicipios() {
        try {
            ClassPathResource resource = new ClassPathResource("static/json/municipios.json");
            if (!resource.exists()) {
                logger.error("El archivo municipios.json no se encuentra en static/json/");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Archivo municipios.json no encontrado\"}");
            }
            String json = new String(Files.readAllBytes(resource.getFile().toPath()));
            return ResponseEntity.ok(json);
        } catch (IOException e) {
            logger.error("Error al leer municipios.json: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error al leer el archivo municipios.json: " + e.getMessage() + "\"}");
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

    // Contar todas las facturas
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(factusApiClient.count());
    }

    // Nuevo endpoint para facturas recientes
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> findRecentFacturas(@RequestParam(value = "recent", required = false) Boolean recent) {
        try {
            if (Boolean.TRUE.equals(recent)) {
                List<Factura> recentFacturas = facturaRepository.findTopByOrderByFechaCreacionDesc(5); // Últimas 5 facturas
                List<Map<String, Object>> simplifiedFacturas = new ArrayList<>();

                for (Factura f : recentFacturas) {
                    Map<String, Object> facturaMap = new HashMap<>();
                    facturaMap.put("id", f.getId());
                    facturaMap.put("numero", f.getNumber());
                    facturaMap.put("referenceCode", f.getReferenceCode());
                    facturaMap.put("status", f.getStatus());
                    facturaMap.put("createdAt", f.getCreatedAt());
                    facturaMap.put("total", f.getTotal());

                    if (f.getCliente() != null) {
                        Map<String, Object> clienteMap = new HashMap<>();
                        clienteMap.put("id", f.getCliente().getId());
                        clienteMap.put("nombre", f.getCliente().getNombre());
                        clienteMap.put("correo", f.getCliente().getCorreo());
                        facturaMap.put("cliente", clienteMap);
                    }

                    simplifiedFacturas.add(facturaMap);
                }

                return ResponseEntity.ok(simplifiedFacturas);
            }
            return listarFacturas(); // Si no se pasa recent=true, devolver todas las facturas
        } catch (Exception e) {
            logger.error("Error al obtener facturas recientes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener facturas recientes: " + e.getMessage()));
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
            if (e.getMessage().contains("Factura no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Factura no encontrada: " + referenceCode));
            } else if (e.getMessage().contains("está validada")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "No se puede eliminar la factura porque está validada: " + referenceCode));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al eliminar la factura: " + e.getMessage()));
        }
    }
}
