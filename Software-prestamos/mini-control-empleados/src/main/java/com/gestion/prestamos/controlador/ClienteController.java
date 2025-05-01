package com.gestion.prestamos.controlador;

import com.gestion.prestamos.entidades.Cliente;
import com.gestion.prestamos.servicio.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Obtener todos los clientes o los recientes si se pasa recent=true
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "recent", required = false) Boolean recent) {
        try {
            if (Boolean.TRUE.equals(recent)) {
                return ResponseEntity.ok(clienteService.findRecentClientes(5)); // Últimos 5 clientes con facturaCount
            }
            return ResponseEntity.ok(clienteService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener clientes: " + e.getMessage()));
        }
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.save(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.update(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Contar todos los clientes
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(clienteService.count());
    }
}