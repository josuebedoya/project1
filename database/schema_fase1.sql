-- =====================================================================
-- Fase 1 - Vehiculo, Documento y su relacion (Proyecto_E1_V2.pdf)
--
-- Nota: la aplicacion usa spring.jpa.hibernate.ddl-auto=update, por lo
-- que Hibernate crea/actualiza estas tablas automaticamente a partir de
-- las entidades JPA. Este script se entrega como documentacion formal
-- de la estructura de base de datos y como alternativa para ejecucion
-- manual si se requiere levantar el esquema sin arrancar la aplicacion.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS PPOOII 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_general_ci;

USE PPOOII;

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
