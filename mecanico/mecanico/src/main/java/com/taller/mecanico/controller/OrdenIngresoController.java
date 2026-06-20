package com.taller.mecanico.controller;

import com.taller.mecanico.dto.OrdenIngresoRequest;
import com.taller.mecanico.entity.Cliente;
import com.taller.mecanico.entity.OrdenIngreso;
import com.taller.mecanico.entity.Vehiculo;
import com.taller.mecanico.repository.ClienteRepository;
import com.taller.mecanico.repository.OrdenIngresoRepository;
import com.taller.mecanico.repository.VehiculoRepository;
import com.taller.mecanico.service.OrdenIngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador MVC: sirve vistas Thymeleaf (@GetMapping con "return nombreVista")
 * y procesa formularios (@PostMapping). El Frontend (Thymeleaf) consumirá estos
 * endpoints directamente desde sus archivos .html.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/ordenes")
public class OrdenIngresoController {

    private final OrdenIngresoService ordenIngresoService;
    private final OrdenIngresoRepository ordenIngresoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    /**
     * Muestra el formulario de creación de una nueva Orden de Ingreso.
     * El Frontend debe crear una vista llamada "ordenes/nueva-orden.html"
     * dentro de src/main/resources/templates/
     */
    @GetMapping("/nueva")
    public String mostrarFormularioNuevo(Model model) {
        // Le pasamos un objeto vacío para que Thymeleaf pueda usar th:object en el form
        model.addAttribute("ordenRequest", new OrdenIngresoRequest());

        // Lista de clientes existentes, útil para un <select> de "cliente ya registrado"
        model.addAttribute("clientes", clienteRepository.findAll());

        return "ordenes/nueva-orden";
    }

    /**
     * Procesa el envío del formulario y crea la Orden completa
     * (Cliente + Vehículo + Orden + Estados) usando el servicio transaccional.
     */
    @PostMapping("/guardar")
    public String guardarOrden(@ModelAttribute("ordenRequest") OrdenIngresoRequest request, Model model) {
        try {
            OrdenIngreso ordenCreada = ordenIngresoService.crearOrdenCompleta(request);
            model.addAttribute("orden", ordenCreada);
            // El Frontend debe crear "ordenes/orden-confirmada.html"
            return "ordenes/orden-confirmada";
        } catch (Exception e) {
            // Si algo falla, volvemos al formulario mostrando el error
            model.addAttribute("error", "Error al guardar la orden: " + e.getMessage());
            model.addAttribute("clientes", clienteRepository.findAll());
            return "ordenes/nueva-orden";
        }
    }

    /**
     * Lista todas las órdenes registradas.
     * El Frontend debe crear "ordenes/lista-ordenes.html"
     */
    @GetMapping("/lista")
    public String listarOrdenes(Model model) {
        List<OrdenIngreso> ordenes = ordenIngresoRepository.findAll();
        model.addAttribute("ordenes", ordenes);
        return "ordenes/lista-ordenes";
    }

    /**
     * Muestra el detalle de una orden específica (útil para el generador de PDF).
     */
    @GetMapping("/{id}")
    public String verDetalleOrden(@PathVariable String id, Model model) {
        OrdenIngreso orden = ordenIngresoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));
        model.addAttribute("orden", orden);
        return "ordenes/detalle-orden";
    }

    // ──────────────────────────────────────────────────────────
    // ENDPOINTS REST (JSON) — útiles para pruebas con Postman
    // y para que el Frontend haga llamadas AJAX/fetch si lo necesita
    // ──────────────────────────────────────────────────────────

    /**
     * Versión API REST: recibe JSON y devuelve JSON.
     * Pruébalo en Postman con POST a: http://localhost:8080/ordenes/api
     */
    @PostMapping("/api")
    @ResponseBody
    public OrdenIngreso crearOrdenApi(@RequestBody OrdenIngresoRequest request) {
        return ordenIngresoService.crearOrdenCompleta(request);
    }

    /**
     * Devuelve todas las órdenes en formato JSON.
     * Pruébalo en Postman con GET a: http://localhost:8080/ordenes/api
     */
    @GetMapping("/api")
    @ResponseBody
    public List<OrdenIngreso> listarOrdenesApi() {
        return ordenIngresoRepository.findAll();
    }

    /**
     * Devuelve todos los clientes en JSON (útil para poblar un <select> vía fetch).
     */
    @GetMapping("/api/clientes")
    @ResponseBody
    public List<Cliente> listarClientesApi() {
        return clienteRepository.findAll();
    }

    /**
     * Devuelve todos los vehículos en JSON.
     */
    @GetMapping("/api/vehiculos")
    @ResponseBody
    public List<Vehiculo> listarVehiculosApi() {
        return vehiculoRepository.findAll();
    }
}