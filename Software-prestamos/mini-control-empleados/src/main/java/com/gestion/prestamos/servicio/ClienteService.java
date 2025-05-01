package com.gestion.prestamos.servicio;

import com.gestion.prestamos.entidades.Cliente;
import com.gestion.prestamos.repositorios.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FactusApiClient facturaService;

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    public List<Cliente> findAll() {
        logger.info("Fetching all clients");
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        logger.info("Fetching client with id: {}", id);
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        logger.info("Saving client: {}", cliente);
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente cliente) {
        logger.info("Updating client with id: {}", id);
        if (clienteRepository.existsById(id)) {
            cliente.setId(id);
            return clienteRepository.save(cliente);
        }
        throw new RuntimeException("Cliente no encontrado");
    }

    public void delete(Long id) {
        logger.info("Deleting client with id: {}", id);
        clienteRepository.deleteById(id);
    }

    public long count() {
        logger.info("Counting all clients");
        return clienteRepository.count();
    }

    public List<Map<String, Object>> findRecentClientes(int limit) {
        logger.info("Fetching top {} recent clients with factura count", limit);
        List<Object[]> recentClients = clienteRepository.findTopByOrderByIdDescWithFacturaCount(limit);
        return recentClients.stream().map(record -> {
            Cliente c = (Cliente) record[0];
            Long facturaCount = (Long) record[1];
            Map<String, Object> clientMap = new HashMap<>();
            clientMap.put("id", c.getId());
            clientMap.put("nombre", c.getNombre());
            clientMap.put("correo", c.getCorreo() != null ? c.getCorreo() : "N/A");
            clientMap.put("facturaCount", facturaCount);
            return clientMap;
        }).collect(Collectors.toList());
    }
}