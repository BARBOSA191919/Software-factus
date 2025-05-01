package com.gestion.prestamos.servicio;

import com.gestion.prestamos.entidades.Cliente;
import com.gestion.prestamos.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private FactusApiClient facturaService;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente cliente) {
        if (clienteRepository.existsById(id)) {
            cliente.setId(id);
            return clienteRepository.save(cliente);
        }
        throw new RuntimeException("Cliente no encontrado");
    }

    public void delete(Long id) {
        clienteRepository.deleteById(id);
    }

    // Nuevo metodo para contar clientes
    public long count() {
        return clienteRepository.count();
    }

    public List<Cliente> findRecentClientes(int limit) {
        List<Cliente> clientes = clienteRepository.findTopByOrderByIdDesc(limit);
        clientes.forEach(cliente -> cliente.setId(facturaService.countByClienteId(cliente.getId())));
        return clientes;
    }

}