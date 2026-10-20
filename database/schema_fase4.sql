-- =====================================================================
-- Fase 4 - Cargue masivo de trayectos desde Excel (Proyecto_E4_V2.pdf)
--
-- Requiere haber ejecutado antes schema_fase1.sql, schema_fase2.sql y schema_fase3.sql.
--
-- Flujo: la app (TrayectoCargueServiceImpl) lee la plantilla Excel fila por fila y por
-- cada una invoca sp_trayecto_cargue (traslado sin alteracion a Trayecto_tmp); luego,
-- una sola vez por cargue, invoca sp_trayecto_validar (marca VALIDADO/ERROR) y
-- sp_trayecto_procesar (traslada lo VALIDADO a la tabla transaccional Trayecto).
-- =====================================================================

USE PPOOII;

-- ---------------------------------------------------------------------
-- Trayecto_tmp
-- Staging del cargue. Replica los campos de Trayecto pero como texto plano (identificacion
-- de persona y placa de vehiculo en vez de FK, valores crudos de cada celda de Excel), ya
-- que el cargue debe trasladar el archivo "sin ningun tipo de alteracion"; la validacion de
-- tipos/existencia se hace despues, en sp_trayecto_validar.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Trayecto_tmp (
    Id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    IdCargue                BIGINT        NOT NULL,
    PersonaIdentificacion   VARCHAR(20)   NULL,
    VehiculoPlaca           VARCHAR(6)    NULL,
    CodigoRuta              VARCHAR(30)   NULL,
    Ubicacion               VARCHAR(255)  NULL,
    OrdenParada             VARCHAR(10)   NULL,
    Latitud                 VARCHAR(30)   NULL,
    Longitud                VARCHAR(30)   NULL,
    LoginUsuario            VARCHAR(30)   NULL,
    Estado                  VARCHAR(10)   NOT NULL,
    Observacion             TEXT          NULL,
    CONSTRAINT CK_TRAYECTOTMP_ESTADO CHECK (Estado IN ('CARGADO', 'VALIDADO', 'PROCESADO', 'ERROR'))
) ENGINE = InnoDB;

CREATE INDEX IX_TRAYECTOTMP_IDCARGUE ON Trayecto_tmp (IdCargue);

-- ---------------------------------------------------------------------
-- Procedimientos almacenados
-- ---------------------------------------------------------------------
DELIMITER $$

-- Procedimiento de Cargue: inserta una fila cruda en Trayecto_tmp, sin validar ni transformar
-- (mas alla de recibirla ya como texto, que es como llega cada celda del Excel).
DROP PROCEDURE IF EXISTS sp_trayecto_cargue$$
CREATE PROCEDURE sp_trayecto_cargue(
    IN p_IdCargue BIGINT,
    IN p_PersonaIdentificacion VARCHAR(20),
    IN p_VehiculoPlaca VARCHAR(6),
    IN p_CodigoRuta VARCHAR(30),
    IN p_Ubicacion VARCHAR(255),
    IN p_OrdenParada VARCHAR(10),
    IN p_Latitud VARCHAR(30),
    IN p_Longitud VARCHAR(30),
    IN p_LoginUsuario VARCHAR(30)
)
BEGIN
    INSERT INTO Trayecto_tmp (
        IdCargue, PersonaIdentificacion, VehiculoPlaca, CodigoRuta, Ubicacion,
        OrdenParada, Latitud, Longitud, LoginUsuario, Estado, Observacion
    ) VALUES (
        p_IdCargue, p_PersonaIdentificacion, p_VehiculoPlaca, p_CodigoRuta, p_Ubicacion,
        p_OrdenParada, p_Latitud, p_Longitud, p_LoginUsuario, 'CARGADO', ''
    );
END$$

-- Procedimiento de Validacion: aplica sobre un cargue las mismas reglas de negocio exigidas
-- para crear un trayecto por API (conductor/vehiculo existentes, documentos habilitados,
-- conductor en PO, formato numerico, tamano de ruta entre 2 y 7 paradas, etc.). Cada regla
-- que falla acumula su mensaje en Observacion; lo que no tiene ningun error queda VALIDADO.
DROP PROCEDURE IF EXISTS sp_trayecto_validar$$
CREATE PROCEDURE sp_trayecto_validar(IN p_IdCargue BIGINT)
BEGIN
    -- 1) OrdenParada debe ser un entero.
    UPDATE Trayecto_tmp
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'OrdenParada no es un número válido. ')
    WHERE IdCargue = p_IdCargue
      AND Estado = 'CARGADO'
      AND NOT (OrdenParada REGEXP '^[0-9]+$');

    -- 2) Latitud/Longitud, si vienen diligenciadas, deben ser decimales.
    UPDATE Trayecto_tmp
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'Latitud/Longitud no son números válidos. ')
    WHERE IdCargue = p_IdCargue
      AND Estado = 'CARGADO'
      AND (
            (Latitud IS NOT NULL AND Latitud <> '' AND NOT (Latitud REGEXP '^-?[0-9]+(\\.[0-9]+)?$'))
         OR (Longitud IS NOT NULL AND Longitud <> '' AND NOT (Longitud REGEXP '^-?[0-9]+(\\.[0-9]+)?$'))
      );

    -- 3) El conductor debe existir y ser de tipo C.
    UPDATE Trayecto_tmp t
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'El conductor no existe o no es de tipo conductor. ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND NOT EXISTS (
            SELECT 1 FROM Persona p
            WHERE p.Identificacion = t.PersonaIdentificacion AND p.TipoPersona = 'C'
      );

    -- 4) El vehiculo debe existir.
    UPDATE Trayecto_tmp t
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'El vehículo no existe. ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND NOT EXISTS (SELECT 1 FROM Vehiculo v WHERE v.Placa = t.VehiculoPlaca);

    -- 5) El login de usuario que registra el trayecto debe existir.
    UPDATE Trayecto_tmp t
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'El login de usuario no existe. ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND NOT EXISTS (SELECT 1 FROM Usuario u WHERE u.Login = t.LoginUsuario);

    -- 6) Todos los documentos asociados al vehiculo deben estar Habilitados.
    UPDATE Trayecto_tmp t
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'El vehículo tiene documentos sin habilitar. ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND EXISTS (SELECT 1 FROM Vehiculo v WHERE v.Placa = t.VehiculoPlaca)
      AND EXISTS (
            SELECT 1 FROM VehiculoDocumento vd
            JOIN Vehiculo v ON v.Id = vd.VehiculoId
            WHERE v.Placa = t.VehiculoPlaca AND vd.Estado <> 'HABILITADO'
      );

    -- 7) El conductor debe poder operar (PO) ese vehiculo especifico.
    UPDATE Trayecto_tmp t
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'El conductor no puede operar (PO) ese vehículo. ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND NOT EXISTS (
            SELECT 1 FROM VehiculoConductor vc
            JOIN Persona p ON p.Id = vc.PersonaId
            JOIN Vehiculo v ON v.Id = vc.VehiculoId
            WHERE p.Identificacion = t.PersonaIdentificacion
              AND v.Placa = t.VehiculoPlaca
              AND vc.Estado = 'PO'
      );

    -- 8) El codigo de ruta no puede repetir uno que ya exista en la tabla transaccional.
    UPDATE Trayecto_tmp t
    SET Estado = 'ERROR',
        Observacion = CONCAT(Observacion, 'El código de ruta ya existe en Trayecto. ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND EXISTS (SELECT 1 FROM Trayecto tr WHERE tr.CodigoRuta = t.CodigoRuta);

    -- 9) Cada codigo de ruta del cargue debe tener entre 2 (inicial+final) y 7 paradas
    --    (inicial + hasta 5 intermedias + final).
    UPDATE Trayecto_tmp t
    JOIN (
        SELECT CodigoRuta, COUNT(*) AS Total
        FROM Trayecto_tmp
        WHERE IdCargue = p_IdCargue
        GROUP BY CodigoRuta
    ) r ON r.CodigoRuta = t.CodigoRuta
    SET t.Estado = 'ERROR',
        t.Observacion = CONCAT(t.Observacion, 'La ruta debe tener entre 2 y 7 paradas (inicial, hasta 5 intermedias y final). ')
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'CARGADO'
      AND (r.Total < 2 OR r.Total > 7);

    -- 10) Lo que no acumulo ningun error queda validado.
    UPDATE Trayecto_tmp
    SET Estado = 'VALIDADO'
    WHERE IdCargue = p_IdCargue
      AND Estado = 'CARGADO';
END$$

-- Procedimiento de Procesado: traslada a Trayecto las filas VALIDADO de un cargue
-- (resolviendo identificacion->PersonaId y placa->VehiculoId) y las marca como PROCESADO.
-- Las filas en ERROR no se tocan.
DROP PROCEDURE IF EXISTS sp_trayecto_procesar$$
CREATE PROCEDURE sp_trayecto_procesar(IN p_IdCargue BIGINT)
BEGIN
    INSERT INTO Trayecto (PersonaId, VehiculoId, CodigoRuta, Ubicacion, OrdenParada, Latitud, Longitud, LoginUsuario)
    SELECT p.Id, v.Id, t.CodigoRuta, t.Ubicacion, CAST(t.OrdenParada AS UNSIGNED),
           NULLIF(t.Latitud, ''), NULLIF(t.Longitud, ''), t.LoginUsuario
    FROM Trayecto_tmp t
    JOIN Persona p ON p.Identificacion = t.PersonaIdentificacion
    JOIN Vehiculo v ON v.Placa = t.VehiculoPlaca
    WHERE t.IdCargue = p_IdCargue
      AND t.Estado = 'VALIDADO';

    UPDATE Trayecto_tmp
    SET Estado = 'PROCESADO'
    WHERE IdCargue = p_IdCargue
      AND Estado = 'VALIDADO';
END$$

DELIMITER ;
