-- Datos de prueba para P4: viajes y asientos.
-- Periodo: del 20/09/2026 al 30/11/2026 (ambas fechas incluidas).
-- El script es idempotente: puede ejecutarse más de una vez sin duplicar viajes ni asientos.
-- Rutas usadas: 6 Lima-Ica, 10 Arequipa-Cusco y 21 Trujillo-Lima.
-- Requiere que 00_bd_reserva_buses.sql y 01_datos_prueba.sql hayan sido ejecutados.

INSERT INTO viaje (
    CodigoViaje, IdRuta, IdBus, IdConductor,
    FechaSalida, HoraSalida, FechaLlegada, HoraLlegada, Tarifa, Estado
)
WITH RECURSIVE fechas AS (
    SELECT DATE('2026-09-20') AS fecha
    UNION ALL
    SELECT DATE_ADD(fecha, INTERVAL 1 DAY)
    FROM fechas
    WHERE fecha < DATE('2026-11-30')
), viajes_prueba AS (
    SELECT
        CONCAT('PR26-', DATE_FORMAT(fecha, '%m%d'), '-R06') AS codigo,
        6 AS id_ruta, 1 AS id_bus, 1 AS id_conductor,
        fecha AS fecha_salida, '06:00:00' AS hora_salida,
        fecha AS fecha_llegada, '11:00:00' AS hora_llegada,
        55.00 AS tarifa
    FROM fechas

    UNION ALL

    SELECT
        CONCAT('PR26-', DATE_FORMAT(fecha, '%m%d'), '-R10'),
        10, 2, 2,
        fecha, '07:00:00',
        fecha, '17:00:00',
        100.00
    FROM fechas

    UNION ALL

    SELECT
        CONCAT('PR26-', DATE_FORMAT(fecha, '%m%d'), '-R21'),
        21, 3, 3,
        fecha, '20:00:00',
        DATE_ADD(fecha, INTERVAL 1 DAY), '06:00:00',
        90.00
    FROM fechas
)
SELECT
    vp.codigo, vp.id_ruta, vp.id_bus, vp.id_conductor,
    vp.fecha_salida, vp.hora_salida, vp.fecha_llegada, vp.hora_llegada,
    vp.tarifa, 1
FROM viajes_prueba vp
WHERE NOT EXISTS (
    SELECT 1
    FROM viaje v
    WHERE v.CodigoViaje = vp.codigo
);

-- Genera el mapa de asientos disponible para los viajes de prueba creados arriba.
INSERT INTO viajeasiento (IdViaje, NroAsiento, Piso, Estado)
WITH RECURSIVE numeros AS (
    SELECT 1 AS numero
    UNION ALL
    SELECT numero + 1
    FROM numeros
    WHERE numero < 60
)
SELECT
    v.IdViaje,
    n.numero,
    CASE
        WHEN b.CantidadPisos = 2 AND n.numero > CEILING(b.NroAsientos / 2) THEN 2
        ELSE 1
    END,
    1
FROM viaje v
JOIN bus b ON b.IdBus = v.IdBus
JOIN numeros n ON n.numero <= b.NroAsientos
WHERE v.CodigoViaje LIKE 'PR26-%'
  AND v.FechaSalida BETWEEN DATE('2026-09-20') AND DATE('2026-11-30')
  AND v.IdRuta IN (6, 10, 21)
  AND NOT EXISTS (
      SELECT 1
      FROM viajeasiento va
      WHERE va.IdViaje = v.IdViaje
        AND va.NroAsiento = n.numero
        AND va.Piso = CASE
            WHEN b.CantidadPisos = 2 AND n.numero > CEILING(b.NroAsientos / 2) THEN 2
            ELSE 1
        END
  );

-- Verificación esperada tras la primera ejecución: 216 viajes y 10 080 asientos PR26.
SELECT
    v.IdRuta,
    COUNT(*) AS viajes_generados,
    MIN(v.FechaSalida) AS primera_fecha,
    MAX(v.FechaSalida) AS ultima_fecha
FROM viaje v
WHERE v.CodigoViaje LIKE 'PR26-%'
GROUP BY v.IdRuta
ORDER BY v.IdRuta;
