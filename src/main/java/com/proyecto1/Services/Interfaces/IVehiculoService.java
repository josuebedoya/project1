package com.proyecto1.Services.Interfaces;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.proyecto1.DTO.DocumentoActualizadoRequest;
import com.proyecto1.DTO.DocumentoAsociadoRequest;
import com.proyecto1.DTO.VehiculoCompletoResponse;
import com.proyecto1.DTO.VehiculoRequest;
import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.TipoVehiculo;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;

public interface IVehiculoService {

    // ============== CRUD ==============
    Vehiculo crear(VehiculoRequest request);

    Vehiculo actualizar(Vehiculo vehiculo);

    void eliminar(Long id);

    List<Vehiculo> consultarVehiculos(Pageable pageable);

    Vehiculo findById(Long id);

    // ============== DOCUMENTOS ASOCIADOS ==============
    List<VehiculoDocumento> findDocumentosByVehiculoId(Long vehiculoId);

    List<VehiculoDocumento> agregarDocumentos(Long vehiculoId, List<DocumentoAsociadoRequest> documentos);

    List<VehiculoDocumento> actualizarDocumentos(Long vehiculoId, List<DocumentoActualizadoRequest> documentos);

    // ============== BUSQUEDAS ==============
    Vehiculo findByPlaca(String placa);

    List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipoVehiculo);

    List<Vehiculo> findByDocumentoId(Long documentoId);

    List<Vehiculo> findByEstadoDocumento(EstadoDocumento estado);

    // ============== SERVICIOS PUBLICOS ==============
    VehiculoCompletoResponse findCompletoByPlaca(String placa);

    List<Vehiculo> findByDocumentosPorVencer(int dias);
}
