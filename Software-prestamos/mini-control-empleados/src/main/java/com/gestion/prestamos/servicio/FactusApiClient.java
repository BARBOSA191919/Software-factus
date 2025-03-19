package com.gestion.prestamos.servicio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.prestamos.config.FactusConfig;
import com.gestion.prestamos.entidades.Factura;
import com.gestion.prestamos.entidades.FactusFacturaDTO;
import com.gestion.prestamos.entidades.Item;
import com.gestion.prestamos.repositorios.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FactusApiClient {

    private final RestTemplate restTemplate;
    private final FactusConfig factusConfig;

    @Autowired
    public FactusApiClient(RestTemplate restTemplate, FactusConfig factusConfig) {
        this.restTemplate = restTemplate;
        this.factusConfig = factusConfig;
    }


    @Autowired
    private FacturaRepository facturaRepository;

    public String obtenerTokenDeAcceso() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // Asegúrate de que el contenido sea form-urlencoded
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // Aceptar respuesta JSON

        System.out.println("Configuración Factus: URL=" + factusConfig.getUrl());
        System.out.println("Intentando autenticación con email: " + factusConfig.getEmail());

        try {
            // Codificar la contraseña para manejar caracteres especiales como %
            String encodedPassword = URLEncoder.encode(factusConfig.getPassword(), StandardCharsets.UTF_8.toString());

            // Construir el cuerpo de la solicitud
            String body = String.format(
                    "grant_type=password&username=%s&password=%s&client_id=%s&client_secret=%s",
                    factusConfig.getEmail(),
                    encodedPassword,
                    factusConfig.getClientId(),
                    factusConfig.getClientSecret()
            );

            // Crear la entidad de la solicitud con el cuerpo y los headers
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            // Imprimir el endpoint para depuración (no imprimas el cuerpo en producción)
            System.out.println("Endpoint: " + factusConfig.getUrl() + "/oauth/token");

            // Hacer la solicitud POST al endpoint de autenticación
            ResponseEntity<String> response = restTemplate.postForEntity(
                    factusConfig.getUrl() + "/oauth/token",
                    request,
                    String.class
            );

            // Imprimir el código de estado de la respuesta
            System.out.println("Respuesta de autenticación: " + response.getStatusCode());

            // Procesar la respuesta
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parsear la respuesta JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                // Verificar si la respuesta contiene el token de acceso
                if (root.has("access_token")) {
                    return root.get("access_token").asText(); // Devolver el token de acceso
                } else {
                    throw new RuntimeException("Token de acceso no encontrado en la respuesta");
                }
            } else {
                // Lanzar una excepción si el código de estado no es 200 OK
                throw new RuntimeException("Error al obtener token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            // Manejar excepciones y proporcionar detalles adicionales
            System.err.println("Excepción al obtener token: " + e.getMessage());

            // Si es un error HTTP, imprimir detalles adicionales
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException httpEx = (HttpClientErrorException) e;
                System.err.println("Status code: " + httpEx.getStatusCode());
                System.err.println("Response headers: " + httpEx.getResponseHeaders());
                System.err.println("Response body (if available): " + httpEx.getResponseBodyAsString());
            }

            // Imprimir el stack trace para depuración
            e.printStackTrace();

            // Lanzar una excepción con un mensaje descriptivo
            throw new RuntimeException("Error al comunicarse con el servicio de autenticación: " + e.getMessage());
        }
    }
    public String obtenerEstadoFactura(String referenceCode) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    factusConfig.getUrl() + "/v1/bills/show/" + referenceCode, // Usar el referenceCode
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                return root.path("status").asText(); // Devuelve el estado de la factura
            } else {
                throw new RuntimeException("Error al obtener el estado de la factura: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el estado de la factura: " + e.getMessage());
        }
    }

    @Transactional
    public String crearFacturaEnFactus(Factura factura) {
        // Primero guardar localmente
        factura = facturaRepository.save(factura);


        // Asociar items
        if (factura.getItems() != null) {
            for (Item item : factura.getItems()) {
                item.setFactura(factura);
            }
        }

        factura = facturaRepository.save(factura);


        // Convertir a DTO para Factus
        FactusFacturaDTO factusDTO = convertirAFactusDTO(factura);

        String token = obtenerTokenDeAcceso();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convertir la factura a JSON
        ObjectMapper mapper = new ObjectMapper();
        String facturaJson;
        try {
            facturaJson = mapper.writeValueAsString(factura);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar la factura: " + e.getMessage());
        }

        HttpEntity<String> request = new HttpEntity<>(facturaJson, headers);
        String apiUrl = factusConfig.getUrl() + "/v1/bills/validate";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al crear factura: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la factura: " + e.getMessage());
        }
    }

    private FactusFacturaDTO convertirAFactusDTO(Factura factura) {
        FactusFacturaDTO dto = new FactusFacturaDTO();

        // Valores básicos de la factura
        dto.setDocument("01"); // Factura electrónica de venta
        dto.setNumbering_range_id(8); // Usa el valor correcto según tu configuración
        dto.setReference_code(factura.getNumber());
        dto.setObservation(factura.getDocumentName() != null ?
                factura.getDocumentName().substring(0, Math.min(factura.getDocumentName().length(), 250)) : "");
        dto.setPayment_form(factura.getFormaPago() != null ? factura.getFormaPago() : "1");
        dto.setPayment_method_code(factura.getMetodoPago() != null ? factura.getMetodoPago() : "10");

        // Si el pago es a crédito (payment_form = 2), incluir payment_due_date
        if ("2".equals(dto.getPayment_form())) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 30);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dto.setPayment_due_date(dateFormat.format(cal.getTime()));
        }

        // Periodo de facturación
        if (factura.getBillingPeriod() != null) {
            FactusFacturaDTO.BillingPeriodDTO billingDTO = new FactusFacturaDTO.BillingPeriodDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            billingDTO.setStart_date(dateFormat.format(factura.getBillingPeriod().getStartDate()));
            billingDTO.setEnd_date(dateFormat.format(factura.getBillingPeriod().getEndDate()));
            billingDTO.setStart_time("00:00:00");
            billingDTO.setEnd_time("23:59:59");

            dto.setBilling_period(billingDTO);
        }

        // CLIENTE
        FactusFacturaDTO.CustomerDTO customerDTO = new FactusFacturaDTO.CustomerDTO();
        customerDTO.setIdentification_document_id("3"); // ID para cédula
        customerDTO.setIdentification(factura.getCliente() != null && factura.getCliente().getIdentificacion() != null ?
                factura.getCliente().getIdentificacion() : "1234567890");

        // Nombre según tipo de cliente
        if (factura.getCliente() != null && factura.getCliente().getTipoCliente() != null) {
            customerDTO.setCompany(factura.getCliente().getNombre() != null ?
                    factura.getCliente().getNombre() : "Cliente Empresarial");
        } else {
            customerDTO.setNames(factura.getCliente() != null && factura.getCliente().getNombre() != null ?
                    factura.getCliente().getNombre() : "Cliente Individual");
        }

        customerDTO.setEmail(factura.getCliente() != null && factura.getCliente().getCorreo() != null ?
                factura.getCliente().getCorreo() : "cliente@ejemplo.com");
        customerDTO.setPhone(factura.getCliente() != null && factura.getCliente().getTelefono() != null ?
                factura.getCliente().getTelefono() : "1234567890");
        customerDTO.setAddress(factura.getCliente() != null && factura.getCliente().getDireccion() != null ?
                factura.getCliente().getDireccion() : "Dirección por defecto");
        customerDTO.setLegal_organization_id("2"); // Código para persona natural

        // IMPORTANTE: Asegurar que tribute_id esté presente y correctamente asignado
        customerDTO.setTribute_id("21"); // Campo obligatorio para responsable de IVA

        dto.setCustomer(customerDTO);

        // ITEMS
        List<FactusFacturaDTO.ItemDTO> itemsDTO = new ArrayList<>();

        if (factura.getItems() != null && !factura.getItems().isEmpty()) {
            for (Item item : factura.getItems()) {
                FactusFacturaDTO.ItemDTO itemDTO = new FactusFacturaDTO.ItemDTO();

                itemDTO.setCode_reference(item.getProducto() != null && item.getProducto().getId() != null ?
                        item.getProducto().getId().toString() : "DEFAULT_CODE");
                itemDTO.setName(item.getProducto() != null && item.getProducto().getName() != null ?
                        item.getProducto().getName() : "Producto por defecto");
                itemDTO.setPrice(item.getPrecio() != null ? item.getPrecio() : Double.valueOf(0));
                itemDTO.setQuantity(item.getCantidad() != null ? item.getCantidad().intValue() : 1);
                itemDTO.setDiscount_rate(item.getPorcentajeDescuento() != null ? item.getPorcentajeDescuento() : 0);

                Double taxRate = 0.0;
                try {
                    if (item != null && item.getProducto() != null && item.getProducto().getTaxRate() != null) {
                        taxRate = item.getProducto().getTaxRate();
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener taxRate: " + e.getMessage());
                }
                itemDTO.setTax_rate(taxRate);

                itemDTO.setUnit_measure_id("70"); // Unidades
                itemDTO.setStandard_code_id("1"); // Código estándar
                itemDTO.setIs_excluded(item.getProducto() != null &&
                        item.getProducto().getExcluded() != null &&
                        item.getProducto().getExcluded() ? 1 : 0);

                // IMPORTANTE: Asegurar que tribute_id esté presente y correctamente asignado
                itemDTO.setTribute_id("1"); // Campo obligatorio para IVA

                itemsDTO.add(itemDTO);
            }
        } else {
            // Item por defecto
            FactusFacturaDTO.ItemDTO defaultItem = new FactusFacturaDTO.ItemDTO();
            defaultItem.setCode_reference("DEFAULT_CODE");
            defaultItem.setName("Producto por defecto");
            defaultItem.setPrice(Double.valueOf(0));
            defaultItem.setQuantity(1);
            defaultItem.setDiscount_rate(0.0);
            defaultItem.setTax_rate(0.0);
            defaultItem.setUnit_measure_id("70");
            defaultItem.setStandard_code_id("1");
            defaultItem.setIs_excluded(0);

            // IMPORTANTE: Asegurar que tribute_id esté presente en el item por defecto
            defaultItem.setTribute_id("1");

            itemsDTO.add(defaultItem);
        }

        dto.setItems(itemsDTO);

        return dto;
    }

    public String actualizarFacturaEnFactus(Long id, Factura factura) {
        String token = obtenerTokenDeAcceso();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Factura> request = new HttpEntity<>(factura, headers);

        ResponseEntity<String> response = restTemplate.exchange(factusConfig.getUrl() + "/facturas/" + id, HttpMethod.PUT, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al actualizar la factura en Factus");
        }
    }

    public String eliminarFacturaEnFactus(Long id) {
        String token = obtenerTokenDeAcceso();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(factusConfig.getUrl() + "/facturas/" + id, HttpMethod.DELETE, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al eliminar la factura en Factus");
        }
    }

    public String validarFactura(String referenceCode) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Empty body as per documentation
            HttpEntity<String> request = new HttpEntity<>("", headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    factusConfig.getUrl() + "/v1/bills/validate/" + referenceCode, // Usar el referenceCode
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Actualizar el estado local si es necesario
                Factura factura = facturaRepository.findByNumber(referenceCode); // Buscar por referenceCode
                if (factura != null) {
                    factura.setStatus("VALIDADA");
                    facturaRepository.save(factura);
                }
                return response.getBody();
            } else {
                throw new RuntimeException("Error al validar la factura: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al validar la factura: " + e.getMessage());
        }
    }

    @Transactional
    public byte[] descargarFacturaPdf(String referenceCode) {
        final int MAX_REINTENTOS = 5;
        final int TIEMPO_ESPERA_BASE_MS = 2000; // 2 segundos

        try {
            // Obtener la factura de la base de datos local usando el referenceCode
            Factura factura = facturaRepository.findByNumber(referenceCode);
            if (factura == null) {
                throw new RuntimeException("Factura no encontrada en la base de datos");
            }

            // Obtener token de acceso
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);

            // Implementar mecanismo de reintento con espera exponencial
            Exception ultimaExcepcion = null;

            for (int intento = 0; intento < MAX_REINTENTOS; intento++) {
                try {
                    // Construir la URL para descargar el PDF
                    String apiUrl = factusConfig.getUrl() + "/v1/bills/download-pdf/" + referenceCode;

                    // Realizar la solicitud GET a la API de Factus
                    ResponseEntity<String> response = restTemplate.exchange(
                            apiUrl,
                            HttpMethod.GET,
                            request,
                            String.class
                    );

                    // Verificar si la respuesta es exitosa (código 200)
                    if (response.getStatusCode() == HttpStatus.OK) {
                        // Parsear la respuesta JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(response.getBody());

                        // Extraer el PDF codificado en base64
                        String pdfBase64 = rootNode.path("data").path("pdf_base_64_encoded").asText();

                        // Decodificar el PDF desde base64
                        return Base64.getDecoder().decode(pdfBase64);
                    } else {
                        // Manejar errores en la respuesta
                        throw new RuntimeException("Error al descargar el PDF: " + response.getStatusCode());
                    }
                } catch (HttpClientErrorException e) {
                    ultimaExcepcion = e;
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        System.out.println("Factura aún no procesada en Factus. Reintentando...");
                    } else {
                        System.err.println("Error HTTP: " + e.getStatusCode() + " - " + e.getStatusText());
                        System.err.println("Cuerpo de respuesta: " + e.getResponseBodyAsString());
                    }

                    // Esperar con retroceso exponencial antes de reintentar
                    long tiempoEspera = TIEMPO_ESPERA_BASE_MS * (long)Math.pow(2, intento);
                    System.out.println("Esperando " + tiempoEspera + "ms antes de reintentar...");
                    Thread.sleep(tiempoEspera);

                } catch (Exception e) {
                    ultimaExcepcion = e;
                    System.err.println("Error en intento " + (intento + 1) + ": " + e.getMessage());

                    // Esperar con retroceso exponencial antes de reintentar
                    long tiempoEspera = TIEMPO_ESPERA_BASE_MS * (long)Math.pow(2, intento);
                    System.out.println("Esperando " + tiempoEspera + "ms antes de reintentar...");
                    Thread.sleep(tiempoEspera);
                }
            }

            // Si llegamos aquí, todos los intentos fallaron
            throw new RuntimeException("Error al descargar el PDF después de " + MAX_REINTENTOS + " intentos: " +
                    (ultimaExcepcion != null ? ultimaExcepcion.getMessage() : "Error desconocido"));

        } catch (Exception e) {
            throw new RuntimeException("Error al descargar el PDF de la factura: " + e.getMessage());
        }
    }
    @Transactional
    public List<Factura> obtenerFacturas() {
        try {
            // Usar una consulta específica para cargar explícitamente las relaciones
            List<Factura> facturas = facturaRepository.findAll();

            // Asegurarse de que Hibernate cargue las relaciones
            for (Factura factura : facturas) {
                if (factura.getItems() != null) {
                    factura.getItems().size(); // Forzar la carga de ítems
                }
                if (factura.getCliente() != null) {
                    factura.getCliente().getId(); // Forzar la carga del cliente
                }
            }

            return facturas;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Añade este metodo a tu clase FactusApiClient
    private String convertirAJson(Object objeto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(objeto);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al convertir a JSON";
        }
    }

    // Method to get detailed information for a specific invoice
    public Map<String, Object> obtenerDetalleFactura(Long id) {
        try {
            // First, get the invoice from the local database to get the reference number
            Optional<Factura> facturaOptional = facturaRepository.findById(id);
            if (!facturaOptional.isPresent()) {
                throw new RuntimeException("Factura no encontrada en la base de datos local");
            }

            Factura factura = facturaOptional.get();
            String referenceCode = factura.getNumber();


            if (referenceCode == null || referenceCode.isEmpty()) {
                throw new RuntimeException("La factura no tiene un número de referencia válido");
            }

            // Now get the detailed information from Factus API
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);

            String apiUrl = factusConfig.getUrl() + "/v1/bills/show/" + referenceCode;
            System.out.println("Obteniendo detalle de factura desde: " + apiUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());

                // Convert to a Map for easier handling
                Map<String, Object> facturaDetalle = new HashMap<>();

                // Extract basic invoice information
                if (rootNode.has("data")) {
                    JsonNode dataNode = rootNode.get("data");

                    // Add basic invoice information
                    facturaDetalle.put("id", factura.getId());
                    facturaDetalle.put("referenceCode", referenceCode);

                    // Add status and dates
                    if (dataNode.has("status")) {
                        facturaDetalle.put("status", dataNode.get("status").asText());
                    }

                    if (dataNode.has("created_at")) {
                        facturaDetalle.put("createdAt", dataNode.get("created_at").asText());
                    }

                    // Add customer information
                    if (dataNode.has("customer")) {
                        JsonNode customerNode = dataNode.get("customer");
                        Map<String, Object> customerMap = new HashMap<>();

                        if (customerNode.has("identification")) {
                            customerMap.put("identification", customerNode.get("identification").asText());
                        }

                        if (customerNode.has("names")) {
                            customerMap.put("name", customerNode.get("names").asText());
                        } else if (customerNode.has("company")) {
                            customerMap.put("name", customerNode.get("company").asText());
                        }

                        if (customerNode.has("email")) {
                            customerMap.put("email", customerNode.get("email").asText());
                        }

                        if (customerNode.has("address")) {
                            customerMap.put("address", customerNode.get("address").asText());
                        }

                        facturaDetalle.put("customer", customerMap);
                    }

                    // Add items information
                    if (dataNode.has("items") && dataNode.get("items").isArray()) {
                        List<Map<String, Object>> itemsList = new ArrayList<>();
                        JsonNode itemsNode = dataNode.get("items");

                        for (JsonNode itemNode : itemsNode) {
                            Map<String, Object> itemMap = new HashMap<>();

                            if (itemNode.has("code_reference")) {
                                itemMap.put("codeReference", itemNode.get("code_reference").asText());
                            }

                            if (itemNode.has("name")) {
                                itemMap.put("name", itemNode.get("name").asText());
                            }

                            if (itemNode.has("price")) {
                                itemMap.put("price", itemNode.get("price").asDouble());
                            }

                            if (itemNode.has("quantity")) {
                                itemMap.put("quantity", itemNode.get("quantity").asInt());
                            }

                            if (itemNode.has("total")) {
                                itemMap.put("total", itemNode.get("total").asDouble());
                            }

                            itemsList.add(itemMap);
                        }

                        facturaDetalle.put("items", itemsList);
                    }

                    // Add totals information
                    if (dataNode.has("totals")) {
                        JsonNode totalsNode = dataNode.get("totals");
                        Map<String, Object> totalsMap = new HashMap<>();

                        if (totalsNode.has("gross_total")) {
                            totalsMap.put("grossTotal", totalsNode.get("gross_total").asDouble());
                        }

                        if (totalsNode.has("discounts")) {
                            totalsMap.put("discounts", totalsNode.get("discounts").asDouble());
                        }

                        if (totalsNode.has("total_with_discount")) {
                            totalsMap.put("totalWithDiscount", totalsNode.get("total_with_discount").asDouble());
                        }

                        if (totalsNode.has("taxes")) {
                            totalsMap.put("taxes", totalsNode.get("taxes").asDouble());
                        }

                        if (totalsNode.has("total")) {
                            totalsMap.put("total", totalsNode.get("total").asDouble());
                        }

                        facturaDetalle.put("totals", totalsMap);
                    }

                    // Add PDF URL if available
                    if (dataNode.has("urls") && dataNode.get("urls").has("pdf")) {
                        facturaDetalle.put("pdfUrl", dataNode.get("urls").get("pdf").asText());
                    }
                }

                return facturaDetalle;
            } else {
                throw new RuntimeException("Error al obtener detalle de la factura: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error HTTP: " + e.getStatusCode() + " - " + e.getStatusText());
            System.err.println("Cuerpo de respuesta: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al obtener detalle de la factura: " + e.getStatusCode() + " " + e.getStatusText());
        } catch (Exception e) {
            System.err.println("Error al obtener detalle de factura: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener detalle de la factura: " + e.getMessage());
        }
    }


    @Transactional
    public byte[] descargarFacturaPdf(Long id) {
        final int MAX_REINTENTOS = 5;
        final int TIEMPO_ESPERA_BASE_MS = 2000; // 2 segundos

        try {
            // 1. Obtener la factura de la base de datos
            Factura factura = facturaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada en la base de datos"));

            // 2. Verificar que tenga un número válido
            if (factura.getNumber() == null || factura.getNumber().isEmpty()) {
                throw new RuntimeException("La factura no tiene un número de referencia válido");
            }

            String facturaNumber = factura.getNumber();
            System.out.println("Intentando descargar factura con número: " + facturaNumber);

            // 3. Obtener token de acceso
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);

            // 4. Implementar mecanismo de reintento con espera exponencial
            Exception ultimaExcepcion = null;

            for (int intento = 0; intento < MAX_REINTENTOS; intento++) {
                try {
                    System.out.println("Intento " + (intento + 1) + " de " + MAX_REINTENTOS +
                            " para descargar factura " + facturaNumber);

                    // 5. Construir la URL para descargar el PDF
                    String apiUrl = factusConfig.getUrl() + "/v1/bills/download-pdf/" + facturaNumber;

                    // 6. Realizar la solicitud GET a la API de Factus
                    ResponseEntity<String> response = restTemplate.exchange(
                            apiUrl,
                            HttpMethod.GET,
                            request,
                            String.class
                    );

                    // 7. Verificar si la respuesta es exitosa (código 200)
                    if (response.getStatusCode() == HttpStatus.OK) {
                        // 8. Parsear la respuesta JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(response.getBody());

                        // 9. Extraer el PDF codificado en base64
                        String pdfBase64 = rootNode.path("data").path("pdf_base_64_encoded").asText();

                        // 10. Decodificar el PDF desde base64
                        return Base64.getDecoder().decode(pdfBase64);
                    } else {
                        // 11. Manejar errores en la respuesta
                        throw new RuntimeException("Error al descargar el PDF: " + response.getStatusCode());
                    }
                } catch (HttpClientErrorException e) {
                    ultimaExcepcion = e;
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        System.out.println("Factura aún no procesada en Factus. Reintentando...");
                    } else {
                        System.err.println("Error HTTP: " + e.getStatusCode() + " - " + e.getStatusText());
                        System.err.println("Cuerpo de respuesta: " + e.getResponseBodyAsString());
                    }

                    // 12. Esperar con retroceso exponencial antes de reintentar
                    long tiempoEspera = TIEMPO_ESPERA_BASE_MS * (long)Math.pow(2, intento);
                    System.out.println("Esperando " + tiempoEspera + "ms antes de reintentar...");
                    Thread.sleep(tiempoEspera);

                } catch (Exception e) {
                    ultimaExcepcion = e;
                    System.err.println("Error en intento " + (intento + 1) + ": " + e.getMessage());

                    // 13. Esperar con retroceso exponencial antes de reintentar
                    long tiempoEspera = TIEMPO_ESPERA_BASE_MS * (long)Math.pow(2, intento);
                    System.out.println("Esperando " + tiempoEspera + "ms antes de reintentar...");
                    Thread.sleep(tiempoEspera);
                }
            }

            // 14. Si llegamos aquí, todos los intentos fallaron
            if (ultimaExcepcion instanceof HttpClientErrorException) {
                HttpClientErrorException httpEx = (HttpClientErrorException) ultimaExcepcion;
                if (httpEx.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new RuntimeException("Después de " + MAX_REINTENTOS + " intentos, la factura aún no ha sido procesada " +
                            "completamente en Factus. Por favor, intente nuevamente más tarde.");
                }
            }

            throw new RuntimeException("Error al descargar el PDF después de " + MAX_REINTENTOS + " intentos: " +
                    (ultimaExcepcion != null ? ultimaExcepcion.getMessage() : "Error desconocido"));

        } catch (Exception e) {
            System.err.println("Error al descargar el PDF de la factura: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al descargar el PDF de la factura: " + e.getMessage());
        }
    }

    public String asegurarFacturaExisteEnFactus(Long id) {
        try {
            // 1. Obtener la factura de la base de datos local
            Factura factura = facturaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada en la base de datos"));

            String facturaId = factura.getNumber();

            // 2. Verificar si la factura ya existe en Factus
            try {
                String estado = obtenerEstadoFactura(facturaId);
                System.out.println("Factura encontrada en Factus con estado: " + estado);
                return facturaId;
            } catch (HttpClientErrorException e) {
                // Si el estado es 404, la factura no existe en Factus
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    System.out.println("Factura no encontrada en Factus. Creando nueva factura...");

                    // 3. Crear la factura en Factus
                    String resultado = crearFacturaEnFactus(factura);

                    // 4. Verificar el resultado
                    if (resultado != null && !resultado.isEmpty()) {
                        System.out.println("Factura creada exitosamente en Factus");

                        // 5. Esperar un momento para que se procese
                        Thread.sleep(2000);

                        // 6. Intentar validar la factura
                        try {
                            validarFactura(String.valueOf(id));
                            System.out.println("Factura validada exitosamente");

                            // Esperar un momento más para que se complete la validación
                            Thread.sleep(3000);
                        } catch (Exception validationEx) {
                            System.err.println("Error al validar la factura: " + validationEx.getMessage());
                            // Continuamos de todos modos, ya que la factura fue creada
                        }

                        return factura.getNumber();
                    } else {
                        throw new RuntimeException("Error al crear la factura en Factus");
                    }
                } else {
                    // Si es otro error, lo propagamos
                    throw e;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al asegurar existencia de factura en Factus: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al asegurar existencia de factura en Factus: " + e.getMessage());
        }
    }

    public byte[] descargarPdf(String pdfUrl) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    pdfUrl,
                    HttpMethod.GET,
                    request,
                    byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody(); // Devuelve el contenido del PDF
            } else {
                throw new RuntimeException("Error al descargar el PDF: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar el PDF: " + e.getMessage());
        }
    }

    private String obtenerUrlPdfDesdeRespuesta(String jsonResponse) {
        try {
            // Parsear la respuesta JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Extraer la URL del PDF
            JsonNode pdfUrlNode = rootNode.path("public_url"); // Asegúrate de que "public_url" sea el campo correcto
            if (pdfUrlNode.isMissingNode() || pdfUrlNode.asText().isEmpty()) {
                throw new RuntimeException("No se encontró la URL del PDF en la respuesta de Factus");
            }

            return pdfUrlNode.asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la respuesta de Factus: " + e.getMessage());
        }
    }

    public String obtenerUrlPdf(String facturaId) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    factusConfig.getUrl() + "/v1/bills/show/" + facturaId,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                return root.path("public_url").asText(); // Devuelve la URL del PDF
            } else {
                throw new RuntimeException("Error al obtener la URL del PDF: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la URL del PDF: " + e.getMessage());
        }
    }

    public String eliminarFacturaNoValidada(String referenceCode) {
        try {
            // Obtener el token de acceso
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Crear la solicitud HTTP
            HttpEntity<String> request = new HttpEntity<>(headers);

            // Construir la URL correctamente
            String apiUrl = factusConfig.getUrl() + "/v1/bills/destroy/reference/" + referenceCode;

            // Realizar la solicitud DELETE
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.DELETE,
                    request,
                    String.class
            );

            // Verificar si la respuesta es exitosa (código 200)
            if (response.getStatusCode() == HttpStatus.OK) {
                // Eliminar la factura de la base de datos local si es necesario
                Factura factura = facturaRepository.findByNumber(referenceCode);
                if (factura != null) {
                    facturaRepository.delete(factura);
                }
                return response.getBody(); // Devuelve la respuesta de Factus
            } else {
                throw new RuntimeException("Error al eliminar la factura: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la factura: " + e.getMessage());
        }
    }
}