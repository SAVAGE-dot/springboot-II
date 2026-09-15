-- Ejecutar sobre bd_reserva_buses si la tabla usuario aún no tiene clave ni rol.
USE bd_reserva_buses;

ALTER TABLE usuario
    ADD COLUMN Clave VARCHAR(100) NULL AFTER Correo,
    ADD COLUMN Rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE' AFTER Genero;
