package com.taller.mecanico.service;

import com.taller.mecanico.dto.OrdenIngresoRequest;
import com.taller.mecanico.entity.*;
import com.taller.mecanico.enums.EstadoOrden;
import com.taller.mecanico.enums.TipoIngreso;
import com.taller.mecanico.repository.ClienteRepository;
import com.taller.mecanico.repository.OrdenIngresoRepository;
import com.taller.mecanico.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // Lombok genera el constructor con estos 3 repositorios (inyección por constructor)
public class OrdenIngresoService {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenIngresoRepository ordenIngresoRepository;

    /**
     * Guarda una Orden de Ingreso completa: Cliente + Vehículo (opcional) + Orden + Estados.
     *
     * @Transactional asegura que TODO se guarde junto o NADA se guarde.
     * Si falla cualquier paso (ej: la patente ya existe, o un campo obligatorio
     * viene nulo), Spring revierte automáticamente (ROLLBACK) todos los inserts
     * anteriores dentro de este método. Así evitamos "datos huérfanos"
     * (ej: un cliente guardado sin su orden asociada).
     */
    @Transactional
    public OrdenIngreso crearOrdenCompleta(OrdenIngresoRequest request) {

        // ── 1. Resolver el Cliente (existente o nuevo) ────────────────
        Cliente cliente = resolverCliente(request);

        // ── 2. Resolver el Vehículo (solo si el ingreso lo requiere) ──
        Vehiculo vehiculo = null;
        if (request.getTipoIngreso() == TipoIngreso.VEHICULO) {
            vehiculo = resolverVehiculo(request);
        }

        // ── 3. Crear la Orden de Ingreso ───────────────────────────────
        OrdenIngreso orden = new OrdenIngreso();
        orden.setId(generarCorrelativo());
        orden.setFecha(LocalDateTime.now());
        orden.setTipoIngreso(request.getTipoIngreso());
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setCliente(cliente);
        orden.setVehiculo(vehiculo);

        // Guardamos la orden primero porque Estados depende de su ID (relación @OneToOne)
        OrdenIngreso ordenGuardada = ordenIngresoRepository.save(orden);

        // ── 4. Crear EstadoVehiculo si corresponde ─────────────────────
        if (request.getTipoIngreso() == TipoIngreso.VEHICULO) {
            EstadoVehiculo estadoVehiculo = new EstadoVehiculo();
            estadoVehiculo.setRayones(request.getRayones());
            estadoVehiculo.setGolpes(request.getGolpes());
            estadoVehiculo.setObsCarroceria(request.getObsCarroceria());
            estadoVehiculo.setNivelCombustible(request.getNivelCombustible());
            estadoVehiculo.setNivelAceiteBajo(request.getNivelAceiteBajo());
            estadoVehiculo.setNivelAguaBajo(request.getNivelAguaBajo());
            estadoVehiculo.setNeumaticosOk(request.getNeumaticosOk());
            estadoVehiculo.setObsNeumaticos(request.getObsNeumaticos());
            estadoVehiculo.setObsGenerales(request.getObsGenerales());
            estadoVehiculo.setOrden(ordenGuardada);

            ordenGuardada.setEstadoVehiculo(estadoVehiculo);
        }

        // ── 5. Crear EstadoCremallera si corresponde ───────────────────
        if (request.getTipoIngreso() == TipoIngreso.CREMALLERA) {
            EstadoCremallera estadoCremallera = new EstadoCremallera();
            estadoCremallera.setDienteDaniados(request.getDienteDaniados());
            estadoCremallera.setFugasAceite(request.getFugasAceite());
            estadoCremallera.setJuegoExcesivo(request.getJuegoExcesivo());
            estadoCremallera.setBujesGastados(request.getBujesGastados());
            estadoCremallera.setMarcaCremallera(request.getMarcaCremallera());
            estadoCremallera.setEsOriginal(request.getEsOriginal());
            estadoCremallera.setObsCremallera(request.getObsCremallera());
            estadoCremallera.setOrden(ordenGuardada);

            ordenGuardada.setEstadoCremallera(estadoCremallera);
        }

        // Gracias al cascade = CascadeType.ALL en OrdenIngreso,
        // este save() guarda en cascada los estados asociados.
        return ordenIngresoRepository.save(ordenGuardada);
    }

    /**
     * Si el Frontend manda un clienteId, reutilizamos el cliente existente.
     * Si no, creamos uno nuevo con los datos del formulario.
     */
    private Cliente resolverCliente(OrdenIngresoRequest request) {
        if (request.getClienteId() != null) {
            return clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Cliente no encontrado con ID: " + request.getClienteId()));
        }

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(request.getClienteNombre());
        nuevoCliente.setTelefono(request.getClienteTelefono());
        nuevoCliente.setRelacionVehiculo(request.getClienteRelacionVehiculo());

        return clienteRepository.save(nuevoCliente);
    }

    /**
     * Si la patente ya existe en la BD, reutilizamos ese vehículo.
     * Si no existe, lo creamos. Esto evita duplicar vehículos que
     * ya pasaron antes por el taller.
     */
    private Vehiculo resolverVehiculo(OrdenIngresoRequest request) {
        return vehiculoRepository.findByPatente(request.getVehiculoPatente())
                .orElseGet(() -> {
                    Vehiculo nuevoVehiculo = new Vehiculo();
                    nuevoVehiculo.setPatente(request.getVehiculoPatente());
                    nuevoVehiculo.setMarca(request.getVehiculoMarca());
                    nuevoVehiculo.setModelo(request.getVehiculoModelo());
                    nuevoVehiculo.setAnio(request.getVehiculoAnio());
                    nuevoVehiculo.setTipoVehiculo(request.getVehiculoTipo());
                    return vehiculoRepository.save(nuevoVehiculo);
                });
    }

    /**
     * Genera un ID correlativo tipo "ORD-001", "ORD-002", etc.
     * Cuenta cuántas órdenes existen y le suma 1.
     */
    private String generarCorrelativo() {
        long siguienteNumero = ordenIngresoRepository.count() + 1;
        return String.format("ORD-%03d", siguienteNumero);
    }
}