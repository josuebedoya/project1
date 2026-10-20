package com.proyecto1.Services.Interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.proyecto1.DTO.CargueTrayectoResponse;
import com.proyecto1.Entities.TrayectoTmp;

public interface ITrayectoCargueService {

    CargueTrayectoResponse cargarExcel(MultipartFile archivo);

    List<TrayectoTmp> consultarCargue(Long idCargue);
}
