-- ============================================================
-- DATOS DE PRUEBA — Se ejecuta automáticamente al iniciar Spring Boot
-- (gracias a spring.sql.init.mode=always en application.properties)
-- ============================================================

-- Limpiar datos previos para evitar duplicados al reiniciar la app
DELETE FROM estados_cremallera;
DELETE FROM estados_vehiculo;
DELETE FROM ordenes_ingreso;
DELETE FROM vehiculos;
DELETE FROM clientes;

-- Reiniciar los contadores de autoincremento (IDENTITY) de PostgreSQL
ALTER SEQUENCE IF EXISTS clientes_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS vehiculos_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS estados_vehiculo_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS estados_cremallera_id_seq RESTART WITH 1;

-- ── 2 Clientes ──────────────────────────────────────────────
INSERT INTO clientes (id, nombre, telefono, relacion_vehiculo) VALUES
(1, 'Juan Pérez', '+56912345678', 'Dueño'),
(2, 'María González', '+56987654321', 'Dueña');

-- ── 2 Vehículos ─────────────────────────────────────────────
INSERT INTO vehiculos (id, patente, marca, modelo, anio, tipo_vehiculo) VALUES
(1, 'ABCD12', 'Toyota', 'Corolla', 2018, 'AUTOMOVIL'),
(2, 'WXYZ34', 'Nissan', 'NP300', 2020, 'CAMIONETA');

-- ── 2 Órdenes de Ingreso (una de vehículo, una de cremallera) ─
INSERT INTO ordenes_ingreso (id, fecha, tipo_ingreso, estado, cliente_id, vehiculo_id) VALUES
('ORD-001', NOW(), 'VEHICULO', 'PENDIENTE', 1, 1),
('ORD-002', NOW(), 'CREMALLERA', 'EN_PROCESO', 2, 2);

-- ── Estado del Vehículo para ORD-001 ────────────────────────
INSERT INTO estados_vehiculo (
    id, orden_id, rayones, golpes, obs_carroceria,
    nivel_combustible, nivel_aceite, nivel_agua,
    neumaticos_ok, obs_neumaticos, obs_generales
) VALUES (
    1, 'ORD-001', true, false, 'Rayón leve en puerta derecha',
    75, false, false,
    true, 'Neumáticos en buen estado', 'Vehículo recibido en buenas condiciones generales'
);

-- ── Estado de la Cremallera para ORD-002 ────────────────────
INSERT INTO estados_cremallera (
    id, orden_id, dientes_daniados, fugas_aceite, juego_excesivo,
    bujes_gastados, marca_cremallera, es_original, obs_cremallera
) VALUES (
    1, 'ORD-002', false, true, true,
    false, 'TRW', true, 'Presenta fuga de aceite y juego excesivo en el extremo izquierdo'
);