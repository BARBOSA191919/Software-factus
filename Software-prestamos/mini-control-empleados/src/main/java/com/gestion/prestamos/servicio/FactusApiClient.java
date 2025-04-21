package com.gestion.prestamos.servicio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.prestamos.config.FactusConfig;
import com.gestion.prestamos.entidades.*;
import com.gestion.prestamos.repositorios.FacturaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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
    // Define el logger
    private static final Logger logger = LoggerFactory.getLogger(FactusApiClient.class);

    @Autowired
    private FacturaRepository facturaRepository;
    public String obtenerTokenDeAcceso() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        logger.info("Configuración Factus: URL={}", factusConfig.getUrl());
        logger.info("Intentando autenticación con email: {}", factusConfig.getEmail());

        try {
            String encodedPassword = URLEncoder.encode(factusConfig.getPassword(), StandardCharsets.UTF_8.toString());
            String body = String.format(
                    "grant_type=password&username=%s&password=%s&client_id=%s&client_secret=%s",
                    factusConfig.getEmail(),
                    encodedPassword,
                    factusConfig.getClientId(),
                    factusConfig.getClientSecret()
            );

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            logger.info("Endpoint de autenticación: {}", factusConfig.getUrl() + "/oauth/token");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    factusConfig.getUrl() + "/oauth/token",
                    request,
                    String.class
            );

            logger.info("Respuesta de autenticación: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("access_token")) {
                    String token = root.get("access_token").asText();
                    logger.info("Token obtenido exitosamente: {}", token);
                    return token;
                } else {
                    throw new RuntimeException("Token de acceso no encontrado en la respuesta");
                }
            } else {
                throw new RuntimeException("Error al obtener token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Excepción al obtener token: {}", e.getMessage(), e);
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException httpEx = (HttpClientErrorException) e;
                logger.error("Status code: {}, Response body: {}", httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
            }
            throw new RuntimeException("Error al comunicarse con el servicio de autenticación: " + e.getMessage());
        }
    }

    @Transactional
    public String crearFacturaEnFactus(Factura factura) {
        // Validar factura antes de procesarla
        if (factura == null || factura.getCliente() == null || factura.getItems() == null || factura.getItems().isEmpty()) {
            throw new IllegalArgumentException("La factura, cliente o items no pueden ser nulos o vacíos");
        }

        // Asociar items y guardar localmente
        factura.getItems().forEach(item -> item.setFactura(factura));
        facturaRepository.save(factura);

        // Convertir a DTO para Factus
        FactusFacturaDTO factusDTO = FacturaMapper.convertirAFactusDTO(factura);

        // Configurar la solicitud a Factus
        String token = obtenerTokenDeAcceso();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        String facturaJson;
        try {
            facturaJson = mapper.writeValueAsString(factusDTO);
            System.out.println("JSON enviado a Factus: " + facturaJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar la factura: " + e.getMessage(), e);
        }

        HttpEntity<String> request = new HttpEntity<>(facturaJson, headers);
        String apiUrl = factusConfig.getUrl() + "/v1/bills/validate";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            System.out.println("Respuesta de Factus: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode rootNode = mapper.readTree(response.getBody());
                // Cambiar de data.number a data.bill.number
                String facturaNumber = rootNode.path("data").path("bill").path("number").asText();

                if (facturaNumber == null || facturaNumber.isEmpty()) {
                    throw new RuntimeException("El número de factura devuelto por Factus es nulo o vacío");
                }

                factura.setNumber(facturaNumber);
                factura.setStatus("VALIDADA");
                facturaRepository.save(factura);

                return response.getBody();
            } else {
                throw new RuntimeException("Error al crear factura en Factus: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error en la llamada a Factus: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al parsear la respuesta de Factus: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear la factura: " + e.getMessage(), e);
        }
    }

    public class FacturaMapper {

        public static FactusFacturaDTO convertirAFactusDTO(Factura factura) {
            FactusFacturaDTO dto = new FactusFacturaDTO();

            // Forzar document a "01" para cumplir con Factus
            dto.setDocument("01"); // Siempre "01" para factura electrónica

            // Asegurar numbering_range_id
            dto.setNumbering_range_id(factura.getNumberingRangeId() != null ? factura.getNumberingRangeId().intValue() : 128);

            dto.setReference_code(factura.getReferenceCode() != null ? factura.getReferenceCode() : "REF-" + System.currentTimeMillis());
            dto.setObservation(factura.getObservation() != null ? factura.getObservation() : "");
            dto.setPayment_form(factura.getFormaPago() != null && factura.getFormaPago().equalsIgnoreCase("Contado") ? "1" : "2");
            dto.setPayment_method_code(factura.getMetodoPago() != null && factura.getMetodoPago().equalsIgnoreCase("Efectivo") ? "10" : "31");

            // Fecha de vencimiento (due_date)
            if (factura.getFechaVencimiento() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dto.setDue_date(sdf.format(factura.getFechaVencimiento()));
            } else {
                dto.setDue_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }

            // Número de líneas (lines_count)
            dto.setLines_count(factura.getNumeroLineas());

            // Descuento total (discount_total)
            dto.setDiscount_total(factura.getTotalDescuento() != null ? factura.getTotalDescuento().doubleValue() : 0.0);

            // Tributos (IVA e INC)
            List<FactusFacturaDTO.TributeDTO> tributes = new ArrayList<>();
            if (factura.getTributos() != null && !factura.getTributos().isEmpty()) {
                for (Tributo tributo : factura.getTributos()) {
                    FactusFacturaDTO.TributeDTO tributeDTO = new FactusFacturaDTO.TributeDTO();
                    tributeDTO.setTribute_id(tributo.getTributeId());
                    tributeDTO.setRate(tributo.getRate() != null ? tributo.getRate() : 0.0);
                    tributeDTO.setAmount(tributo.getAmount() != null ? tributo.getAmount().doubleValue() : 0.0);
                    tributes.add(tributeDTO);
                }
            }
            if (factura.getInc() != null && factura.getInc().compareTo(BigDecimal.ZERO) > 0) {
                FactusFacturaDTO.TributeDTO incTribute = new FactusFacturaDTO.TributeDTO();
                incTribute.setTribute_id("03");
                incTribute.setRate(8.0); // Ajusta según tu configuración
                incTribute.setAmount(factura.getInc().doubleValue());
                tributes.add(incTribute);
            }
            dto.setTributes(tributes);

            // Cliente
            FactusFacturaDTO.CustomerDTO customerDTO = new FactusFacturaDTO.CustomerDTO();
            Cliente cliente = factura.getCliente();
            if (cliente != null) {
                customerDTO.setIdentification_document_id("3");
                customerDTO.setIdentification(cliente.getIdentificacion() != null ? cliente.getIdentificacion() : "1003865544");
                customerDTO.setNames(cliente.getNombre() != null ? cliente.getNombre() : "");
                customerDTO.setEmail(cliente.getCorreo() != null && !cliente.getCorreo().isEmpty() ? cliente.getCorreo() : "sin_correo@example.com");
                customerDTO.setAddress(cliente.getDireccion() != null ? cliente.getDireccion() : "Sin dirección");
                customerDTO.setPhone(cliente.getTelefono() != null ? cliente.getTelefono() : ""); // Añadir teléfono
                customerDTO.setLegal_organization_id(cliente.getTipoCliente() != null && cliente.getTipoCliente().equalsIgnoreCase("Persona Jurídica") ? "2" : "1");
                customerDTO.setTribute_id("21");
                // Usar factura.getMunicipio() y validar que no sea nulo
                customerDTO.setMunicipality_id(factura.getMunicipio() != null ? factura.getMunicipio() : cliente.getMunicipioId());
                if (customerDTO.getMunicipality_id() == null) {
                    throw new IllegalArgumentException("El municipioId no puede ser nulo");
                }
            }
            dto.setCustomer(customerDTO);

            // Ítems
            List<FactusFacturaDTO.ItemDTO> itemsDTO = new ArrayList<>();
            if (factura.getItems() != null) {
                for (Item item : factura.getItems()) {
                    if (item != null && item.getProducto() != null) {
                        FactusFacturaDTO.ItemDTO itemDTO = new FactusFacturaDTO.ItemDTO();
                        itemDTO.setCode_reference(item.getProducto().getId() != null ? item.getProducto().getId().toString() : "N/A");
                        itemDTO.setName(item.getProducto().getName() != null ? item.getProducto().getName() : "Producto sin nombre");
                        itemDTO.setPrice(item.getPrecio() != null ? item.getPrecio().doubleValue() : 0.0);
                        itemDTO.setQuantity(item.getCantidad() != null ? item.getCantidad().intValue() : 1);
                        itemDTO.setDiscount_rate(item.getPorcentajeDescuento() != null ? item.getPorcentajeDescuento().doubleValue() : 0.0);
                        itemDTO.setTax_rate(item.getProducto().getTaxRate() != null ? item.getProducto().getTaxRate() : 19.0);
                        itemDTO.setUnit_measure_id("70");
                        itemDTO.setStandard_code_id("1");
                        itemDTO.setIs_excluded(item.getProducto().getExcluded() != null && item.getProducto().getExcluded() ? 1 : 0);
                        itemDTO.setTribute_id("1");
                        itemsDTO.add(itemDTO);
                    }
                }
            }
            dto.setItems(itemsDTO);

            return dto;
        }
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


    public void validarFactura(String number) {
        try {
            String token = obtenerTokenDeAcceso(); // Esto funciona según el log: "Respuesta de autenticación: 200 OK"
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    factusConfig.getUrl() + "/v1/bills/validate/" + number, // Posiblemente este endpoint está mal
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al validar la factura: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al validar la factura: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error al validar la factura: " + e.getMessage());
        }
    }

    public byte[] descargarFacturaPdf(String number) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);

            String apiUrl = factusConfig.getUrl() + "/v1/bills/download-pdf/" + number;
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            System.out.println("Respuesta de la API de Factus: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                if (rootNode.has("data") && rootNode.get("data").has("pdf_base_64_encoded")) {
                    String pdfBase64 = rootNode.get("data").get("pdf_base_64_encoded").asText();
                    return Base64.getDecoder().decode(pdfBase64);
                } else {
                    throw new RuntimeException("El PDF no está disponible en la respuesta");
                }
            } else {
                throw new RuntimeException("Error al descargar el PDF: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar el PDF: " + e.getMessage());
        }
    }

    // Metodo para descargar XML (ya proporcionado anteriormente)
    public String descargarFacturaXml(String number) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    factusConfig.getUrl() + "/v1/bills/download-xml/" + number,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                if (rootNode.has("data") && rootNode.get("data").has("xml_base_64_encoded")) {
                    return rootNode.get("data").get("xml_base_64_encoded").asText();
                } else {
                    throw new RuntimeException("El XML no está disponible en la respuesta");
                }
            } else {
                throw new RuntimeException("Error al descargar el XML: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar el XML: " + e.getMessage());
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


    public String eliminarFacturaPendiente(String referenceCode) {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    factusConfig.getUrl() + "/v1/bills/delete/" + referenceCode,
                    HttpMethod.DELETE,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al eliminar la factura: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la factura: " + e.getMessage());
        }
    }

    public void eliminarTodasLasFacturasPendientes() {
        try {
            // Obtener la lista de facturas pendientes
            List<Map<String, String>> facturasPendientes = obtenerFacturasPendientes();

            // Eliminar cada factura pendiente
            for (Map<String, String> factura : facturasPendientes) {
                String referenceCode = factura.get("reference_code");
                eliminarFacturaPendiente(referenceCode);
                System.out.println("Factura eliminada: " + referenceCode);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar facturas pendientes: " + e.getMessage());
        }
    }

    public List<Map<String, String>> obtenerFacturasPendientes() {
        try {
            String token = obtenerTokenDeAcceso();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    factusConfig.getUrl() + "/v1/bills/pending",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode facturasNode = root.path("data");

                List<Map<String, String>> facturasPendientes = new ArrayList<>();
                for (JsonNode facturaNode : facturasNode) {
                    Map<String, String> factura = new HashMap<>();
                    factura.put("id", facturaNode.path("id").asText());
                    factura.put("reference_code", facturaNode.path("reference_code").asText());
                    factura.put("status", facturaNode.path("status").asText());
                    facturasPendientes.add(factura);
                }

                return facturasPendientes;
            } else {
                throw new RuntimeException("Error al obtener facturas pendientes: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener facturas pendientes: " + e.getMessage());
        }
    }


    @Transactional
    public String eliminarFactura(String referenceCode) {
        try {
            // Verificar si la factura existe localmente
            Optional<Factura> facturaOpt = facturaRepository.findByReferenceCode(referenceCode);
            if (!facturaOpt.isPresent()) {
                logger.warn("Factura con referenceCode {} no encontrada en la base de datos local", referenceCode);
                throw new RuntimeException("Factura no encontrada localmente: " + referenceCode);
            }
            Factura factura = facturaOpt.get();

            // Obtener token
            String token = obtenerTokenDeAcceso();
            logger.info("Token de acceso obtenido para eliminar factura: {}", token);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            logger.info("Headers enviados a Factus: {}", headers);

            HttpEntity<String> request = new HttpEntity<>(headers);
            String url = factusConfig.getUrl() + "/v1/bills/destroy/reference/" + referenceCode;
            logger.info("Llamando a Factus API para eliminar factura: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Actualizar estado en la base de datos local
                factura.setStatus("ELIMINADA");
                facturaRepository.save(factura);
                logger.info("Factura {} eliminada exitosamente en Factus y actualizada localmente", referenceCode);
                return response.getBody();
            } else {
                logger.error("Error al eliminar factura: {} - {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Error al eliminar la factura: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP al eliminar factura: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Factura no encontrada en Factus: " + referenceCode);
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Error de autenticación: Token inválido o sin permisos");
            }
            throw new RuntimeException("Error al eliminar la factura: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar factura: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar la factura: " + e.getMessage());
        }
    }
}

