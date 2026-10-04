package com.proyecto1.Scheduled;

import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.VehiculoDocumento;
import com.proyecto1.Repository.VehiculoDocumentoRepository;

// Cada 2 minutos: verifica la fecha de vencimiento de los documentos asociados a los
// vehiculos y marca como VENCIDO los que ya cumplieron su fecha.
@Component
public class DocumentoVencimientoTask {

    @Autowired
    @Qualifier("IVehiculoDocumentoRepo")
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    private static final Logger logger = LogManager.getLogger(DocumentoVencimientoTask.class);

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void verificarVencimientoDocumentos() {
        LocalDate hoy = LocalDate.now();
        List<VehiculoDocumento> vencidos = vehiculoDocumentoRepository
                .findByEstadoNotAndFechaVencimientoBefore(EstadoDocumento.VENCIDO, hoy);

        for (VehiculoDocumento documento : vencidos) {
            documento.setEstado(EstadoDocumento.VENCIDO);
            vehiculoDocumentoRepository.save(documento);
            logger.info("DOCUMENTO {} DEL VEHICULO {} MARCADO COMO VENCIDO (VENCIA EL {})",
                    documento.getDocumento().getNombre(), documento.getVehiculo().getPlaca(),
                    documento.getFechaVencimiento());
        }
    }
}
