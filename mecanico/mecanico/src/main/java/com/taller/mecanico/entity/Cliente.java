package com.taller.mecanico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String telefono;

    // Descripción de la relación del cliente con el vehículo
    // Ej: "Dueño", "Arrendatario", "Empresa"
    @Column(name = "relacion_vehiculo")
    private String relacionVehiculo;

    // Un cliente puede tener múltiples órdenes de ingreso
    // @JsonIgnore evita el bucle infinito al serializar a JSON:
    // Cliente -> ordenes -> OrdenIngreso -> cliente -> ordenes -> ... (ciclo infinito)
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenIngreso> ordenes;
}