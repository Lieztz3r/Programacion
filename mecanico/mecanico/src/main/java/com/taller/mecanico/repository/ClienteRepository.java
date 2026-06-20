package com.taller.mecanico.repository;

import com.taller.mecanico.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // JpaRepository ya provee: save(), findById(), findAll(), deleteById(), etc.
}