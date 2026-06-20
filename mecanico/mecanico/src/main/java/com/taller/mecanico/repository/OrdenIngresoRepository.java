package com.taller.mecanico.repository;

import com.taller.mecanico.entity.OrdenIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenIngresoRepository extends JpaRepository<OrdenIngreso, String> {

    // Cuenta cuántas órdenes existen (lo usamos para generar el correlativo ORD-001, ORD-002...)
    long count();
}