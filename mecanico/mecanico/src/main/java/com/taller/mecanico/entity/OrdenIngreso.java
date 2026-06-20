package com.taller.mecanico.entity;

import com.taller.mecanico.enums.EstadoOrden;
import com.taller.mecanico.enums.TipoIngreso;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ordenes_ingreso")
public class OrdenIngreso {

    // ID con formato "ORD-001", "ORD-002", etc. (lo generamos en el servicio)
    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Indica si es ingreso de vehículo completo o solo cremallera
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingreso", nullable = false)
    private TipoIngreso tipoIngreso;

    // Estado actual de la orden
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado;

    // Muchas órdenes pueden pertenecer a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // El vehículo es opcional (puede ser null si es solo cremallera)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = true)
    private Vehiculo vehiculo;

    // Relación 1 a 1 con el estado técnico del vehículo
    @OneToOne(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstadoVehiculo estadoVehiculo;

    // Relación 1 a 1 con el estado técnico de la cremallera
    @OneToOne(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstadoCremallera estadoCremallera;
}