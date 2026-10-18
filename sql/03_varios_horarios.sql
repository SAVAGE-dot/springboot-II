-- Varios horarios el mismo día para la misma ruta.
-- Así la búsqueda (origen + destino + fecha) muestra más de un viaje.
-- Idempotente: no duplica códigos ni asientos.
USE bd_reserva_buses;

INSERT INTO viaje (
    CodigoViaje, IdRuta, IdBus, IdConductor,
    FechaSalida, HoraSalida, FechaLlegada, HoraLlegada, Tarifa, Estado
)
SELECT v.codigo, v.id_ruta, v.id_bus, v.id_conductor,
       v.fecha_salida, v.hora_salida, v.fecha_llegada, v.hora_llegada,
       v.tarifa, 1
FROM (
    SELECT 'CB-1020-14' AS codigo, 1 AS id_ruta, 1 AS id_bus, 1 AS id_conductor,
           DATE('2026-10-20') AS fecha_salida, TIME('14:00:00') AS hora_salida,
           DATE('2026-10-20') AS fecha_llegada, TIME('22:00:00') AS hora_llegada,
           75.00 AS tarifa
    UNION ALL
    SELECT 'CB-1020-22', 1, 1, 1,
           DATE('2026-10-20'), TIME('22:00:00'),
           DATE('2026-10-21'), TIME('06:00:00'),
           80.00
    UNION ALL
    SELECT 'CB-2409-08', 1, 1, 1,
           DATE('2026-09-24'), TIME('08:00:00'),
           DATE('2026-09-24'), TIME('16:00:00'),
           55.00
    UNION ALL
    SELECT 'CB-2509-08', 1, 1, 1,
           DATE('2026-09-25'), TIME('08:00:00'),
           DATE('2026-09-25'), TIME('16:00:00'),
           55.00
) v
WHERE EXISTS (SELECT 1 FROM ruta r WHERE r.IdRuta = v.id_ruta)
  AND EXISTS (SELECT 1 FROM bus b WHERE b.IdBus = v.id_bus)
  AND EXISTS (SELECT 1 FROM conductor c WHERE c.IdConductor = v.id_conductor)
  AND NOT EXISTS (SELECT 1 FROM viaje x WHERE x.CodigoViaje = v.codigo);

INSERT INTO viajeasiento (IdViaje, NroAsiento, Piso, Estado)
SELECT v.IdViaje, n.numero, 1, 1
FROM viaje v
JOIN bus b ON b.IdBus = v.IdBus
JOIN (
    SELECT 1 AS numero UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
    UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
    UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16
    UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24
    UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28
    UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL SELECT 31 UNION ALL SELECT 32
    UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL SELECT 36
    UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40
    UNION ALL SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44
    UNION ALL SELECT 45 UNION ALL SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48
    UNION ALL SELECT 49 UNION ALL SELECT 50 UNION ALL SELECT 51 UNION ALL SELECT 52
    UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55 UNION ALL SELECT 56
    UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60
) n ON n.numero <= b.NroAsientos
WHERE v.CodigoViaje IN ('CB-1020-14', 'CB-1020-22', 'CB-2409-08', 'CB-2509-08')
  AND NOT EXISTS (
      SELECT 1
      FROM viajeasiento va
      WHERE va.IdViaje = v.IdViaje
        AND va.NroAsiento = n.numero
        AND va.Piso = 1
  );
