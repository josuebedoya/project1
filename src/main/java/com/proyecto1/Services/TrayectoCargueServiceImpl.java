package com.proyecto1.Services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto1.DTO.CargueTrayectoResponse;
import com.proyecto1.Entities.EstadoTrayectoTmp;
import com.proyecto1.Entities.TrayectoTmp;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.TrayectoTmpRepository;
import com.proyecto1.Services.Interfaces.ITrayectoCargueService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// Cargue masivo de trayectos desde una plantilla Excel. Cumple el requerimiento de la Fase 4
// apoyandose en tres procedimientos almacenados de base de datos (sp_trayecto_cargue,
// sp_trayecto_validar, sp_trayecto_procesar - ver database/schema_fase4.sql):
//   1) Cargue: traslada cada fila del Excel a Trayecto_tmp tal cual, sin validar.
//   2) Validacion: marca cada fila como VALIDADO o ERROR (con el detalle en Observacion).
//   3) Procesado: traslada las filas VALIDADO a la tabla transaccional Trayecto.
//
// Plantilla esperada (fila 1 = encabezado, se ignora): columnas en este orden ->
//   A: Identificacion del conductor | B: Placa del vehiculo | C: Codigo de ruta
//   D: Ubicacion | E: Orden de parada | F: Latitud (opcional) | G: Longitud (opcional)
//   H: Login de usuario
@Service("TrayectoCargueService")
public class TrayectoCargueServiceImpl implements ITrayectoCargueService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Qualifier("ITrayectoTmpRepo")
    private TrayectoTmpRepository trayectoTmpRepository;

    private static final Logger logger = LogManager.getLogger(TrayectoCargueServiceImpl.class);
    private static final DateTimeFormatter FORMATO_ID_CARGUE = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final int COLUMNAS_PLANTILLA = 8;

    @Override
    @Transactional
    public CargueTrayectoResponse cargarExcel(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Debe adjuntar un archivo Excel con la plantilla de trayectos");
        }

        Long idCargue = Long.parseLong(LocalDateTime.now().format(FORMATO_ID_CARGUE));
        logger.info("INICIANDO CARGUE DE TRAYECTOS - IdCargue={}", idCargue);

        try (Workbook libro = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet hoja = libro.getSheetAt(0);
            // Locale.US fuerza el punto como separador decimal, sin importar el locale del
            // servidor; de lo contrario, en locales en español formatearia "4.4326" como "4,4326"
            // y el procedimiento de validacion lo rechazaria como numero invalido.
            DataFormatter formateador = new DataFormatter(Locale.US);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null || esFilaVacia(fila, formateador)) {
                    continue;
                }
                invocarSpCargue(idCargue,
                        leerCelda(fila, 0, formateador),
                        leerCelda(fila, 1, formateador),
                        leerCelda(fila, 2, formateador),
                        leerCelda(fila, 3, formateador),
                        leerCelda(fila, 4, formateador),
                        leerCelda(fila, 5, formateador),
                        leerCelda(fila, 6, formateador),
                        leerCelda(fila, 7, formateador));
            }
        } catch (IOException e) {
            logger.error("ERROR CARGUE_EXCEL: NO SE PUDO LEER EL ARCHIVO!", e);
            throw new BusinessException("No se pudo leer el archivo Excel: " + e.getMessage());
        }

        invocarSpValidar(idCargue);
        invocarSpProcesar(idCargue);

        List<TrayectoTmp> detalle = trayectoTmpRepository.findByIdCargueOrderByIdAsc(idCargue);
        if (detalle.isEmpty()) {
            throw new BusinessException("El archivo no contiene filas de datos para cargar");
        }

        long procesados = detalle.stream().filter(t -> t.getEstado() == EstadoTrayectoTmp.PROCESADO).count();
        long conError = detalle.stream().filter(t -> t.getEstado() == EstadoTrayectoTmp.ERROR).count();

        logger.info("CARGUE DE TRAYECTOS FINALIZADO - IdCargue={}, total={}, procesados={}, conError={}",
                idCargue, detalle.size(), procesados, conError);

        return new CargueTrayectoResponse(idCargue, detalle.size(), (int) procesados, (int) conError, detalle);
    }

    @Override
    public List<TrayectoTmp> consultarCargue(Long idCargue) {
        List<TrayectoTmp> detalle = trayectoTmpRepository.findByIdCargueOrderByIdAsc(idCargue);
        if (detalle.isEmpty()) {
            throw new ResourceNotFoundException("No existe un cargue con id " + idCargue);
        }
        return detalle;
    }

    // ================ PROCEDIMIENTOS ALMACENADOS ================
    private void invocarSpCargue(Long idCargue, String personaIdentificacion, String vehiculoPlaca,
            String codigoRuta, String ubicacion, String ordenParada, String latitud, String longitud,
            String loginUsuario) {
        entityManager.createNativeQuery("CALL sp_trayecto_cargue(?,?,?,?,?,?,?,?,?)")
                .setParameter(1, idCargue)
                .setParameter(2, personaIdentificacion)
                .setParameter(3, vehiculoPlaca)
                .setParameter(4, codigoRuta)
                .setParameter(5, ubicacion)
                .setParameter(6, ordenParada)
                .setParameter(7, latitud)
                .setParameter(8, longitud)
                .setParameter(9, loginUsuario)
                .executeUpdate();
    }

    private void invocarSpValidar(Long idCargue) {
        entityManager.createNativeQuery("CALL sp_trayecto_validar(?)")
                .setParameter(1, idCargue)
                .executeUpdate();
    }

    private void invocarSpProcesar(Long idCargue) {
        entityManager.createNativeQuery("CALL sp_trayecto_procesar(?)")
                .setParameter(1, idCargue)
                .executeUpdate();
    }

    // ================ LECTURA DEL EXCEL ================
    private boolean esFilaVacia(Row fila, DataFormatter formateador) {
        for (int columna = 0; columna < COLUMNAS_PLANTILLA; columna++) {
            if (!leerCelda(fila, columna, formateador).isBlank()) {
                return false;
            }
        }
        return true;
    }

    // DataFormatter formatea la celda tal como se ve en Excel (texto o numero), sin alterar su
    // contenido, consistente con el requerimiento de que el cargue no debe transformar los datos.
    private String leerCelda(Row fila, int indice, DataFormatter formateador) {
        Cell celda = fila.getCell(indice);
        return celda == null ? "" : formateador.formatCellValue(celda).trim();
    }
}
