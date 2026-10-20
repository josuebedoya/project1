-- =====================================================================
-- Datos de prueba consolidados (Fases 1-4)
--
-- Ejecutar DESPUES de schema_fase1.sql, schema_fase2.sql, schema_fase3.sql y
-- schema_fase4.sql, sobre una base ya migrada. Usa INSERT IGNORE: es seguro
-- volver a correr este script, los duplicados (por Placa/Identificacion/
-- Codigo/Login/ApiKey) simplemente se omiten sin error.
-- =====================================================================

USE PPOOII;

-- ---------------------------------------------------------------------
-- Documento (parametrico)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO Documento (Codigo, Nombre, TipoVehiculoAplica, Obligatoriedad, Descripcion) VALUES
('SOAT',       'SOAT',                 'AM', 'RR', 'Seguro obligatorio de accidentes de tránsito'),
('TECNO',      'Tecnomecánica',        'AM', 'RR', 'Revisión técnico mecánica'),
('TARJETA_OP', 'Tarjeta de Operación', 'A',  'RA', 'Tarjeta de operación para automóviles');

-- ---------------------------------------------------------------------
-- Vehiculo
-- ---------------------------------------------------------------------
INSERT IGNORE INTO Vehiculo (TipoVehiculo, Placa, TipoServicio, TipoCombustible, CapacidadPasajeros, Color, Modelo, Marca, Linea) VALUES
('AUTOMOVIL',   'ABC123', 'Pu', 'GASOLINA', 4, '#FFFFFF', 2022, 'Toyota',    'Corolla'),
('MOTOCICLETA', 'XYZ12A', 'Pr', 'GAS',      1, '#000000', 2021, 'Yamaha',    'FZ'),
('AUTOMOVIL',   'DEF456', 'Pu', 'GASOLINA', 4, '#FFFFFF', 2022, 'Chevrolet', 'Spark'),
('AUTOMOVIL',   'GHI789', 'Pu', 'GASOLINA', 4, '#FFFFFF', 2022, 'Renault',   'Logan'),
('AUTOMOVIL',   'JKL012', 'Pu', 'GASOLINA', 4, '#FFFFFF', 2022, 'Mazda',     '2'),
('AUTOMOVIL',   'MNO345', 'Pu', 'GASOLINA', 4, '#FFFFFF', 2022, 'Nissan',    'Versa'),
('AUTOMOVIL',   'PQR678', 'Pu', 'GASOLINA', 4, '#FFFFFF', 2022, 'Kia',       'Rio');

-- ---------------------------------------------------------------------
-- VehiculoDocumento (todos Habilitados, para que Trayecto pase la validación)
-- XYZ12A queda sin conductor asociado a propósito: sirve para probar el
-- caso "vehículo sin conductores".
-- ---------------------------------------------------------------------
INSERT IGNORE INTO VehiculoDocumento (VehiculoId, DocumentoId, FechaExpedicion, FechaVencimiento, Estado)
SELECT v.Id, d.Id, '2025-01-01', '2026-12-31', 'HABILITADO'
FROM Vehiculo v JOIN Documento d ON d.Codigo = 'SOAT'
WHERE v.Placa IN ('ABC123', 'XYZ12A', 'DEF456', 'GHI789', 'JKL012', 'MNO345', 'PQR678');

INSERT IGNORE INTO VehiculoDocumento (VehiculoId, DocumentoId, FechaExpedicion, FechaVencimiento, Estado)
SELECT v.Id, d.Id, '2025-01-01', '2026-12-31', 'HABILITADO'
FROM Vehiculo v JOIN Documento d ON d.Codigo = 'TECNO'
WHERE v.Placa = 'ABC123';

-- ---------------------------------------------------------------------
-- Persona: 6 conductores (tipo C, licencia vigente) + 3 administrativos (tipo A)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO Persona (Identificacion, TipoIdentificacion, Nombres, Apellidos, CorreoElectronico, TipoPersona, LicenciaConduccion, FechaVigenciaLicencia) VALUES
('1001001001', 'CC', 'Carlos',    'Ramírez', 'carlos.ramirez@test.com', 'C', 'licencia-de-prueba', '2027-01-01'),
('1001001002', 'CC', 'Conductor', 'Dos',     'conductor2@test.com',     'C', 'licencia-de-prueba', '2027-01-01'),
('1001001003', 'CC', 'Conductor', 'Tres',    'conductor3@test.com',     'C', 'licencia-de-prueba', '2027-01-01'),
('1001001004', 'CC', 'Conductor', 'Cuatro',  'conductor4@test.com',     'C', 'licencia-de-prueba', '2027-01-01'),
('1001001005', 'CC', 'Conductor', 'Cinco',   'conductor5@test.com',     'C', 'licencia-de-prueba', '2027-01-01'),
('1001001006', 'CC', 'Conductor', 'Seis',    'conductor6@test.com',     'C', 'licencia-de-prueba', '2027-01-01');

INSERT IGNORE INTO Persona (Identificacion, TipoIdentificacion, Nombres, Apellidos, CorreoElectronico, TipoPersona) VALUES
('2002002002', 'CC', 'Ana',       'Gómez',     'ana.gomez@test.com',    'A'),
('9001001001', 'CC', 'Operador',  'Uno',       'operador01@test.com',   'A'),
('9001001002', 'CC', 'Supervisor','Uno',       'supervisor@test.com',   'A');

-- ---------------------------------------------------------------------
-- Usuario (solo para las personas Administrativas)
-- Password para los 3 = "admin" (hash BCrypt real, verificado con la misma
-- libreria que usa la app). ApiKey fija y facil de copiar para pruebas.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO Usuario (IdPersona, Login, Password, ApiKey)
SELECT p.Id, 'admin', '$2a$10$rjFTyGPidXSp.HfF0qFkcOj6zGqlzNIAOu/KytRM3ob6ir9ODZXFu', 'a1111111-1111-1111-1111-111111111111'
FROM Persona p WHERE p.Identificacion = '2002002002';

INSERT IGNORE INTO Usuario (IdPersona, Login, Password, ApiKey)
SELECT p.Id, 'operador01', '$2a$10$rjFTyGPidXSp.HfF0qFkcOj6zGqlzNIAOu/KytRM3ob6ir9ODZXFu', 'a2222222-2222-2222-2222-222222222222'
FROM Persona p WHERE p.Identificacion = '9001001001';

INSERT IGNORE INTO Usuario (IdPersona, Login, Password, ApiKey)
SELECT p.Id, 'supervisor', '$2a$10$rjFTyGPidXSp.HfF0qFkcOj6zGqlzNIAOu/KytRM3ob6ir9ODZXFu', 'a3333333-3333-3333-3333-333333333333'
FROM Persona p WHERE p.Identificacion = '9001001002';

-- ---------------------------------------------------------------------
-- VehiculoConductor: cada conductor puede operar (PO) su vehiculo correspondiente
-- ---------------------------------------------------------------------
INSERT IGNORE INTO VehiculoConductor (VehiculoId, PersonaId, FechaAsociacion, Estado)
SELECT v.Id, p.Id, '2025-01-01', 'PO'
FROM Vehiculo v JOIN Persona p ON TRUE
WHERE (v.Placa = 'ABC123' AND p.Identificacion = '1001001001')
   OR (v.Placa = 'DEF456' AND p.Identificacion = '1001001002')
   OR (v.Placa = 'GHI789' AND p.Identificacion = '1001001003')
   OR (v.Placa = 'JKL012' AND p.Identificacion = '1001001004')
   OR (v.Placa = 'MNO345' AND p.Identificacion = '1001001005')
   OR (v.Placa = 'PQR678' AND p.Identificacion = '1001001006');

-- ---------------------------------------------------------------------
-- Trayecto: una ruta de ejemplo con 2 paradas (Carlos + ABC123)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO Trayecto (PersonaId, VehiculoId, CodigoRuta, Ubicacion, OrdenParada, Latitud, Longitud, LoginUsuario)
SELECT p.Id, v.Id, 'RUTA-001', 'Conservatorio del Tolima, Ibagué, Tolima', 0, 4.4389, -75.2322, 'admin'
FROM Persona p JOIN Vehiculo v ON TRUE
WHERE p.Identificacion = '1001001001' AND v.Placa = 'ABC123';

INSERT IGNORE INTO Trayecto (PersonaId, VehiculoId, CodigoRuta, Ubicacion, OrdenParada, Latitud, Longitud, LoginUsuario)
SELECT p.Id, v.Id, 'RUTA-001', 'Parque Deportivo, Ibagué, Tolima', 1, 4.4278, -75.1989, 'admin'
FROM Persona p JOIN Vehiculo v ON TRUE
WHERE p.Identificacion = '1001001001' AND v.Placa = 'ABC123';
