-- Datos ficticios base para CiberBus.
-- Ejecutar después de 00_bd_reserva_buses.sql y antes de 02_viajes_prueba_sep_nov_2026.sql.
USE bd_reserva_buses;

INSERT INTO ciudad (IdCiudad, Ciudad, Departamento, Estado) VALUES
 (1, 'Lima', 'Lima', 1), (2, 'Arequipa', 'Arequipa', 1),
 (3, 'Cusco', 'Cusco', 1), (4, 'Trujillo', 'La Libertad', 1),
 (5, 'Chiclayo', 'Lambayeque', 1), (6, 'Piura', 'Piura', 1),
 (7, 'Ica', 'Ica', 1), (8, 'Huancayo', 'Junin', 1),
 (9, 'Ayacucho', 'Ayacucho', 1), (10, 'Abancay', 'Apurimac', 1),
 (11, 'Huaraz', 'Ancash', 1), (12, 'Puno', 'Puno', 1),
 (13, 'Tacna', 'Tacna', 1), (14, 'Tumbes', 'Tumbes', 1),
 (15, 'Tarapoto', 'San Martin', 1);

INSERT INTO turno (IdTurno, Nombre, HoraInicio, HoraFin, Estado) VALUES
 (1, 'Turno diurno', '06:00:00', '14:00:00', 1),
 (2, 'Turno nocturno', '14:00:00', '22:00:00', 1);

INSERT INTO bus (IdBus, NroPlaca, Marca, TipoBus, CantidadPisos, NroAsientos, Estado) VALUES
 (1, 'ABC-101', 'Mercedes-Benz', 'Semi Cama', 1, 40, 1),
 (2, 'ABC-102', 'Scania', 'Cama', 2, 56, 1),
 (3, 'ABC-103', 'Volvo', 'Semi Cama', 2, 44, 1),
 (4, 'ABC-104', 'Mercedes-Benz', 'Cama', 2, 60, 1),
 (5, 'ABC-105', 'Scania', 'Economico', 1, 48, 1),
 (6, 'ABC-106', 'Volvo', 'Cama', 2, 58, 1);

INSERT INTO usuario (
 IdUsuario, TipoDocumento, NroDocumento, Nombre, Apellido, Correo, Clave,
 Telefono, FechaNacimiento, Nacionalidad, Genero, Rol, Estado
) VALUES
 (1, 'DNI', '70000001', 'Ana', 'Torres', 'ana.torres@ciberbus.pe', 'clave123', '999100001', '1995-04-15', 'Peruana', 'F', 'CLIENTE', 1),
 (2, 'DNI', '70000002', 'Bruno', 'Flores', 'bruno.flores@ciberbus.pe', 'clave123', '999100002', '1992-08-21', 'Peruana', 'M', 'CLIENTE', 1),
 (3, 'DNI', '70000003', 'Carla', 'Ramos', 'carla.ramos@ciberbus.pe', 'clave123', '999100003', '1990-12-03', 'Peruana', 'F', 'ADMIN', 1);

INSERT INTO pasajero (
 IdPasajero, TipoDocumento, NroDocumento, Nombre, Apellido, Correo,
 Telefono, FechaNacimiento, Nacionalidad, Genero, Estado
) VALUES
 (1, 'DNI', '71000001', 'Ana', 'Torres', 'ana.torres@ciberbus.pe', '999100001', '1995-04-15', 'Peruana', 'F', 1),
 (2, 'DNI', '71000002', 'Bruno', 'Flores', 'bruno.flores@ciberbus.pe', '999100002', '1992-08-21', 'Peruana', 'M', 1),
 (3, 'DNI', '71000003', 'Daniela', 'Vega', 'daniela.vega@ciberbus.pe', '999100003', '1998-02-12', 'Peruana', 'F', 1);

INSERT INTO conductor (
 IdConductor, TipoDocumento, NroDocumento, Nombre, Apellido, Correo,
 Telefono, CategoriaLicencia, VencimientoLicencia, IdTurno, DiaDescanso, Estado
) VALUES
 (1, 'DNI', '72000001', 'Carlos', 'Ramirez', 'carlos.ramirez@ciberbus.pe', '988100001', 'A-IIIc', '2029-06-30', 1, 'Domingo', 1),
 (2, 'DNI', '72000002', 'Luis', 'Gutierrez', 'luis.gutierrez@ciberbus.pe', '988100002', 'A-IIIc', '2028-09-15', 1, 'Lunes', 1),
 (3, 'DNI', '72000003', 'Jorge', 'Quispe', 'jorge.quispe@ciberbus.pe', '988100003', 'A-IIIc', '2029-01-20', 2, 'Martes', 1),
 (4, 'DNI', '72000004', 'Miguel', 'Torres', 'miguel.torres@ciberbus.pe', '988100004', 'A-IIIc', '2028-04-10', 2, 'Miercoles', 1),
 (5, 'DNI', '72000005', 'Renato', 'Salazar', 'renato.salazar@ciberbus.pe', '988100005', 'A-IIIc', '2029-11-05', 1, 'Jueves', 1),
 (6, 'DNI', '72000006', 'Edgar', 'Mendoza', 'edgar.mendoza@ciberbus.pe', '988100006', 'A-IIIc', '2028-07-22', 2, 'Viernes', 1);

INSERT INTO ruta (IdRuta, CiudadPartida, CiudadLlegada, HorasEstimadas, Estado) VALUES
 (1, 1, 2, 16.00, 1), (2, 1, 3, 22.00, 1), (3, 1, 4, 10.00, 1),
 (4, 1, 5, 12.00, 1), (5, 1, 6, 16.00, 1), (6, 1, 7, 5.00, 1),
 (7, 1, 8, 8.00, 1), (8, 1, 10, 14.00, 1), (9, 1, 11, 8.00, 1),
 (10, 2, 3, 10.00, 1), (11, 2, 12, 6.00, 1), (12, 2, 13, 6.00, 1),
 (13, 4, 5, 3.00, 1), (14, 5, 6, 3.00, 1), (15, 6, 14, 5.00, 1),
 (16, 3, 15, 24.00, 1), (17, 8, 9, 7.00, 1), (18, 9, 10, 6.00, 1),
 (19, 2, 1, 16.00, 1), (20, 3, 1, 22.00, 1), (21, 4, 1, 10.00, 1),
 (22, 5, 1, 12.00, 1), (23, 6, 1, 16.00, 1), (24, 7, 1, 5.00, 1),
 (25, 8, 1, 8.00, 1);

-- No se insertan reservas ni detalle de reserva aquí: dependen de viajes y asientos.
