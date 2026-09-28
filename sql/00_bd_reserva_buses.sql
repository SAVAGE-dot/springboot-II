-- Estructura completa y limpia de CiberBus.
-- Ejecutar primero; este archivo reinicia la base de datos.
DROP DATABASE IF EXISTS bd_reserva_buses;
CREATE DATABASE bd_reserva_buses
CHARACTER SET utf8mb4
COLLATE utf8mb4_spanish_ci;

USE bd_reserva_buses;

-- =====================================================
-- TABLA: ciudad
-- =====================================================
CREATE TABLE ciudad (
    IdCiudad INT AUTO_INCREMENT PRIMARY KEY,
    Ciudad VARCHAR(100) NOT NULL,
    Departamento VARCHAR(100) NOT NULL,
    Estado INT NOT NULL DEFAULT 1
);

-- =====================================================
-- TABLA: turno
-- =====================================================
CREATE TABLE turno (
    IdTurno INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    HoraInicio TIME NOT NULL,
    HoraFin TIME NOT NULL,
    Estado INT NOT NULL DEFAULT 1
);

-- =====================================================
-- TABLA: bus
-- =====================================================
CREATE TABLE bus (
    IdBus INT AUTO_INCREMENT PRIMARY KEY,
    NroPlaca VARCHAR(15) NOT NULL,
    Marca VARCHAR(50) NOT NULL,
    TipoBus VARCHAR(50) NOT NULL,
    CantidadPisos INT NOT NULL,
    NroAsientos INT NOT NULL,
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT uq_bus_nroplaca UNIQUE (NroPlaca)
);

-- =====================================================
-- TABLA: usuario
-- =====================================================
CREATE TABLE usuario (
    IdUsuario INT AUTO_INCREMENT PRIMARY KEY,
    TipoDocumento VARCHAR(30) NOT NULL,
    NroDocumento VARCHAR(20) NOT NULL,
    Nombre VARCHAR(50) NOT NULL,
    Apellido VARCHAR(50) NOT NULL,
    Correo VARCHAR(100),
    Clave VARCHAR(100),
    Telefono VARCHAR(20),
    FechaNacimiento DATE,
    Nacionalidad VARCHAR(50),
    Genero VARCHAR(10),
    Rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT uq_usuario_documento UNIQUE (NroDocumento),
    CONSTRAINT uq_usuario_correo UNIQUE (Correo)
);

-- =====================================================
-- TABLA: pasajero
-- =====================================================
CREATE TABLE pasajero (
    IdPasajero INT AUTO_INCREMENT PRIMARY KEY,
    TipoDocumento VARCHAR(30) NOT NULL,
    NroDocumento VARCHAR(20) NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Correo VARCHAR(150),
    Telefono VARCHAR(20),
    FechaNacimiento DATE,
    Nacionalidad VARCHAR(50),
    Genero VARCHAR(20),
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT uq_pasajero_documento UNIQUE (NroDocumento),
    CONSTRAINT uq_pasajero_correo UNIQUE (Correo)
);

-- =====================================================
-- TABLA: conductor
-- =====================================================
CREATE TABLE conductor (
    IdConductor INT AUTO_INCREMENT PRIMARY KEY,
    TipoDocumento VARCHAR(20) NOT NULL,
    NroDocumento VARCHAR(20) NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Correo VARCHAR(150),
    Telefono VARCHAR(20),
    CategoriaLicencia VARCHAR(20) NOT NULL,
    VencimientoLicencia DATE NOT NULL,
    IdTurno INT NOT NULL,
    DiaDescanso VARCHAR(20),
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT uq_conductor_documento UNIQUE (NroDocumento),
    CONSTRAINT uq_conductor_correo UNIQUE (Correo),
    CONSTRAINT fk_conductor_turno
        FOREIGN KEY (IdTurno) REFERENCES turno(IdTurno)
);

-- =====================================================
-- TABLA: ruta
-- =====================================================
CREATE TABLE ruta (
    IdRuta INT AUTO_INCREMENT PRIMARY KEY,
    CiudadPartida INT NOT NULL,
    CiudadLlegada INT NOT NULL,
    HorasEstimadas DECIMAL(4,2) NOT NULL,
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_ruta_ciudad_partida
        FOREIGN KEY (CiudadPartida) REFERENCES ciudad(IdCiudad),
    CONSTRAINT fk_ruta_ciudad_llegada
        FOREIGN KEY (CiudadLlegada) REFERENCES ciudad(IdCiudad),
    CONSTRAINT chk_ruta_ciudades_diferentes
        CHECK (CiudadPartida <> CiudadLlegada)
);

-- =====================================================
-- TABLA: viaje
-- =====================================================
CREATE TABLE viaje (
    IdViaje INT AUTO_INCREMENT PRIMARY KEY,
    CodigoViaje VARCHAR(20) NOT NULL,
    IdRuta INT NOT NULL,
    IdBus INT NOT NULL,
    IdConductor INT NOT NULL,
    FechaSalida DATE NOT NULL,
    HoraSalida TIME NOT NULL,
    FechaLlegada DATE NOT NULL,
    HoraLlegada TIME NOT NULL,
    Tarifa DECIMAL(6,2) NOT NULL,
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT uq_viaje_codigo UNIQUE (CodigoViaje),
    CONSTRAINT fk_viaje_ruta
        FOREIGN KEY (IdRuta) REFERENCES ruta(IdRuta),
    CONSTRAINT fk_viaje_bus
        FOREIGN KEY (IdBus) REFERENCES bus(IdBus),
    CONSTRAINT fk_viaje_conductor
        FOREIGN KEY (IdConductor) REFERENCES conductor(IdConductor)
);

-- =====================================================
-- TABLA: viajeasiento
-- =====================================================
CREATE TABLE viajeasiento (
    IdViajeAsiento INT AUTO_INCREMENT PRIMARY KEY,
    IdViaje INT NOT NULL,
    NroAsiento INT NOT NULL,
    Piso INT NOT NULL,
    Estado INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_viajeasiento_viaje
        FOREIGN KEY (IdViaje) REFERENCES viaje(IdViaje),
    CONSTRAINT uq_viajeasiento UNIQUE (IdViaje, NroAsiento, Piso)
);

-- =====================================================
-- TABLA: reserva
-- =====================================================
CREATE TABLE reserva (
    IdReserva INT AUTO_INCREMENT PRIMARY KEY,
    IdUsuario INT NOT NULL,
    IdViaje INT NOT NULL,
    CodigoReserva VARCHAR(20) NOT NULL,
    FechaReserva DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MontoTotal DECIMAL(8,2) NOT NULL,
    MetodoPago VARCHAR(50) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    CONSTRAINT uq_reserva_codigo UNIQUE (CodigoReserva),
    CONSTRAINT fk_reserva_usuario
        FOREIGN KEY (IdUsuario) REFERENCES usuario(IdUsuario),
    CONSTRAINT fk_reserva_viaje
        FOREIGN KEY (IdViaje) REFERENCES viaje(IdViaje)
);

-- =====================================================
-- TABLA: detallereserva
-- =====================================================
CREATE TABLE detallereserva (
    IdDetalleReserva INT AUTO_INCREMENT PRIMARY KEY,
    IdReserva INT NOT NULL,
    IdViajeAsiento INT NOT NULL,
    IdPasajero INT NOT NULL,
    PrecioPagado DECIMAL(6,2) NOT NULL,
    CONSTRAINT fk_detallereserva_reserva
        FOREIGN KEY (IdReserva) REFERENCES reserva(IdReserva),
    CONSTRAINT fk_detallereserva_viajeasiento
        FOREIGN KEY (IdViajeAsiento) REFERENCES viajeasiento(IdViajeAsiento),
    CONSTRAINT fk_detallereserva_pasajero
        FOREIGN KEY (IdPasajero) REFERENCES pasajero(IdPasajero),
    CONSTRAINT uq_detalle_asiento UNIQUE (IdReserva, IdViajeAsiento),
    CONSTRAINT uq_detalle_pasajero UNIQUE (IdReserva, IdPasajero)
);
