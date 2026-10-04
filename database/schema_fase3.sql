-- =====================================================================
-- Fase 3 - Entidad Trayecto y campos de licencia de conduccion en
-- Persona (Proyecto_E3_V2.pdf)
--
-- Requiere haber ejecutado antes schema_fase1.sql y schema_fase2.sql
-- (Vehiculo, Persona, etc.).
-- =====================================================================

USE PPOOII;

-- ---------------------------------------------------------------------
-- Persona
-- Campos aplicables unicamente cuando TipoPersona = 'C' (Conductor);
-- la obligatoriedad se valida en el servicio, no en la base de datos.
-- ---------------------------------------------------------------------
ALTER TABLE Persona
    ADD COLUMN IF NOT EXISTS LicenciaConduccion LONGBLOB NULL,
    ADD COLUMN IF NOT EXISTS FechaVigenciaLicencia DATE NULL;

-- ---------------------------------------------------------------------
-- Trayecto
-- Cada fila representa una parada (inicial, intermedia o final) de una
-- ruta. Varias filas comparten el mismo CodigoRuta: OrdenParada = 0 es
-- la parada inicial y el mayor OrdenParada de cada ruta es la final;
-- como maximo 5 paradas intermedias entre ambas.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Trayecto (
    Id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    PersonaId       BIGINT       NOT NULL,
    VehiculoId      BIGINT       NOT NULL,
    CodigoRuta      VARCHAR(30)  NOT NULL,
    Ubicacion       VARCHAR(255) NOT NULL,
    OrdenParada     INT          NOT NULL,
    Latitud         DOUBLE       NULL,
    Longitud        DOUBLE       NULL,
    LoginUsuario    VARCHAR(30)  NOT NULL,
    CONSTRAINT UQ_TRAYECTO_RUTA_ORDEN UNIQUE (CodigoRuta, OrdenParada),
    CONSTRAINT FK_TRAYECTO_PERSONA FOREIGN KEY (PersonaId)
        REFERENCES Persona (Id),
    CONSTRAINT FK_TRAYECTO_VEHICULO FOREIGN KEY (VehiculoId)
        REFERENCES Vehiculo (Id)
) ENGINE = InnoDB;
