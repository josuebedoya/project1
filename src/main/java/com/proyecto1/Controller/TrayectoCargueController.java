package com.proyecto1.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto1.DTO.CargueTrayectoResponse;
import com.proyecto1.Entities.TrayectoTmp;
import com.proyecto1.Services.Interfaces.ITrayectoCargueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/proyecto1/trayecto/cargue")
@Tag(name = "Cargue Masivo", description = "Cargue de trayectos desde una plantilla Excel vía procedimientos almacenados (Fase 4)")
@SecurityRequirement(name = "bearerAuth")
public class TrayectoCargueController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("TrayectoCargueService")
    private ITrayectoCargueService trayectoCargueService;

    @Operation(summary = "Cargar Excel de trayectos", description = "Sube una plantilla Excel (.xlsx, "
            + "campo \"archivo\") con trayectos. Cada fila se carga sin alterar (sp_trayecto_cargue), luego "
            + "se valida (sp_trayecto_validar) y las filas válidas se trasladan a la tabla Trayecto "
            + "(sp_trayecto_procesar). Devuelve el detalle de cada fila con su estado y, si aplica, el error.")
    @PostMapping
    public ResponseEntity<CargueTrayectoResponse> cargar(@RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trayectoCargueService.cargarExcel(archivo));
    }

    @Operation(summary = "Consultar resultado de un cargue", description = "Devuelve los registros de un "
            + "cargue específico (formato yyyyMMddHHmm) con su estado (CARGADO, VALIDADO, PROCESADO o ERROR) "
            + "y la observación acumulada de errores, si los tiene.")
    @GetMapping("/{idCargue}")
    public ResponseEntity<List<TrayectoTmp>> consultarCargue(@Parameter(description = "Identificador del cargue (yyyyMMddHHmm)") @PathVariable("idCargue") Long idCargue) {
        return ResponseEntity.ok(trayectoCargueService.consultarCargue(idCargue));
    }
}
