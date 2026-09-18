-- =====================================================================
-- Proyecto Final - Script de base de datos completo (Entrega 1 + Entrega 2)
--
-- Nota: la aplicacion usa spring.jpa.hibernate.ddl-auto=update, por lo
-- que Hibernate crea/actualiza estas tablas automaticamente a partir de
-- las entidades JPA. Este script se entrega como documentacion formal
-- de la estructura de base de datos y como alternativa para ejecucion
-- manual si se requiere levantar el esquema sin arrancar la aplicacion.
-- =====================================================================

USE PPOOII;

-- =====================================================================
-- ENTREGA 1 (proyect-part1.md)
-- =====================================================================

-- ---------------------------------------------------------------------
-- Vehiculo
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Vehiculo (
    Id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    TipoVehiculo        VARCHAR(15) NOT NULL,
    Placa               VARCHAR(6)  NOT NULL,
    TipoServicio        VARCHAR(2)  NOT NULL,
    TipoCombustible     VARCHAR(10) NOT NULL,
    CapacidadPasajeros  INT         NOT NULL,
    Color               VARCHAR(7)  NOT NULL,
    Modelo              INT         NOT NULL,
    Marca               VARCHAR(50) NOT NULL,
    Linea               VARCHAR(50) NOT NULL,
    CONSTRAINT UQ_VEHICULO_PLACA UNIQUE (Placa),
    CONSTRAINT CK_VEHICULO CHECK (
        TipoVehiculo IN ('AUTOMOVIL', 'MOTOCICLETA')
        AND TipoServicio IN ('Pu', 'Pr')
        AND TipoCombustible IN ('GASOLINA', 'GAS', 'DISEL')
    )
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Documento
-- Entidad parametrica/de configuracion de documentos asociables a los
-- vehiculos.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Documento (
    Id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    Codigo               VARCHAR(20)  NOT NULL,
    Nombre               VARCHAR(100) NOT NULL,
    TipoVehiculoAplica   VARCHAR(2)   NOT NULL,
    Obligatoriedad       VARCHAR(2)   NOT NULL,
    Descripcion          VARCHAR(255) NULL,
    CONSTRAINT UQ_DOCUMENTO_CODIGO UNIQUE (Codigo),
    CONSTRAINT CK_DOCUMENTO CHECK (
        TipoVehiculoAplica IN ('A', 'M', 'AM')
        AND Obligatoriedad IN ('RA', 'RM', 'RR')
    )
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- VehiculoDocumento
-- Relacion N:M entre Vehiculo y Documento. Un vehiculo tiene como
-- minimo un documento asociado o muchos documentos.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS VehiculoDocumento (
    Id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    VehiculoId        BIGINT      NOT NULL,
    DocumentoId       BIGINT      NOT NULL,
    FechaExpedicion   DATE        NOT NULL,
    FechaVencimiento  DATE        NOT NULL,
    Estado            VARCHAR(20) NOT NULL,
    CONSTRAINT FK_VEHICULODOCUMENTO_VEHICULO FOREIGN KEY (VehiculoId)
        REFERENCES Vehiculo (Id),
    CONSTRAINT FK_VEHICULODOCUMENTO_DOCUMENTO FOREIGN KEY (DocumentoId)
        REFERENCES Documento (Id),
    CONSTRAINT CK_VEHICULODOCUMENTO_ESTADO CHECK (Estado IN ('HABILITADO', 'VENCIDO', 'EN_VERIFICACION'))
) ENGINE = InnoDB;

-- =====================================================================
-- ENTREGA 2 (proyect-part-2.md)
-- Entidades Persona, Usuario, relacion Vehiculo-Persona (conductor) y
-- adicion de campo BLOB en VehiculoDocumento.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Persona
-- Datos basicos de una persona que representa un conductor o un
-- administrador del sistema.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Persona (
    Id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    Identificacion      VARCHAR(20)  NOT NULL,
    TipoIdentificacion  VARCHAR(2)   NOT NULL,
    Nombres             VARCHAR(100) NOT NULL,
    Apellidos           VARCHAR(100) NOT NULL,
    CorreoElectronico   VARCHAR(150) NOT NULL,
    TipoPersona         VARCHAR(1)   NOT NULL,
    CONSTRAINT UQ_PERSONA_IDENTIFICACION UNIQUE (Identificacion),
    CONSTRAINT CK_PERSONA_TIPOIDENTIFICACION CHECK (TipoIdentificacion IN ('CC')),
    CONSTRAINT CK_PERSONA_TIPOPERSONA CHECK (TipoPersona IN ('C', 'A'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Usuario
-- Solo las personas de tipo ADMINISTRATIVO (TipoPersona = 'A') tienen
-- usuario. Llave primaria compuesta (IdPersona, Login) y restriccion
-- unica sobre IdPersona para garantizar que una persona tenga uno y
-- solo un usuario asociado (relacion 1 a 1).
-- Regla de nemotecnia del login: primera letra del nombre + primera
-- letra del apellido + numero de identificacion (se aplica en el
-- servicio, no en el script).
-- ApiKey se genera automaticamente al crear el usuario (a cargo del
-- servicio); Password tambien se genera automaticamente en la creacion.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Usuario (
    IdPersona   BIGINT       NOT NULL,
    Login       VARCHAR(30)  NOT NULL,
    Password    VARCHAR(255) NOT NULL,
    ApiKey      VARCHAR(255) NOT NULL,
    CONSTRAINT PK_USUARIO PRIMARY KEY (IdPersona, Login),
    CONSTRAINT UQ_USUARIO_IDPERSONA UNIQUE (IdPersona),
    CONSTRAINT UQ_USUARIO_APIKEY UNIQUE (ApiKey),
    CONSTRAINT FK_USUARIO_PERSONA FOREIGN KEY (IdPersona)
        REFERENCES Persona (Id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- VehiculoConductor
-- Relacion N:M entre Vehiculo y Persona. Un vehiculo tiene como minimo
-- un conductor asociado o muchos conductores. Unicamente se asocian
-- personas de tipo CONDUCTOR (TipoPersona = 'C'), validado a nivel de
-- servicio.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS VehiculoConductor (
    Id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    VehiculoId       BIGINT      NOT NULL,
    PersonaId        BIGINT      NOT NULL,
    FechaAsociacion  DATE        NOT NULL,
    Estado           VARCHAR(2)  NOT NULL,
    CONSTRAINT FK_VEHICULOCONDUCTOR_VEHICULO FOREIGN KEY (VehiculoId)
        REFERENCES Vehiculo (Id),
    CONSTRAINT FK_VEHICULOCONDUCTOR_PERSONA FOREIGN KEY (PersonaId)
        REFERENCES Persona (Id),
    CONSTRAINT CK_VEHICULOCONDUCTOR_ESTADO CHECK (Estado IN ('PO', 'EA', 'RO'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- VehiculoDocumento
-- Adicion de campo para almacenar el documento PDF asociado, registrado
-- en BASE64 dentro de un campo BLOB.
-- ---------------------------------------------------------------------
ALTER TABLE VehiculoDocumento
    ADD COLUMN IF NOT EXISTS ArchivoPdf LONGBLOB NULL;
