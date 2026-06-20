package com.taller.mecanico.dto;

import com.taller.mecanico.enums.TipoIngreso;
import com.taller.mecanico.enums.TipoVehiculo;
import lombok.Data;

@Data
public class OrdenIngresoRequest {

    // ── Datos del Cliente ────────────────────────────────
    private String clienteNombre;
    private String clienteTelefono;
    private String clienteRelacionVehiculo;

    // Si el cliente ya existe, el Frontend puede mandar su ID
    // en vez de crear uno nuevo (evita clientes duplicados)
    private Long clienteId;

    // ── Datos del Vehículo (opcional si es solo cremallera) ──
    private String vehiculoPatente;
    private String vehiculoMarca;
    private String vehiculoModelo;
    private Integer vehiculoAnio;
    private TipoVehiculo vehiculoTipo;

    // ── Datos de la Orden ─────────────────────────────────
    private TipoIngreso tipoIngreso;

    // ── Estado del Vehículo (opcional) ────────────────────
    private Boolean rayones;
    private Boolean golpes;
    private String obsCarroceria;
    private Integer nivelCombustible;
    private Boolean nivelAceiteBajo;
    private Boolean nivelAguaBajo;
    private Boolean neumaticosOk;
    private String obsNeumaticos;
    private String obsGenerales;

    // ── Estado de la Cremallera (opcional) ────────────────
    private Boolean dienteDaniados;
    private Boolean fugasAceite;
    private Boolean juegoExcesivo;
    private Boolean bujesGastados;
    private String marcaCremallera;
    private Boolean esOriginal;
    private String obsCremallera;
}