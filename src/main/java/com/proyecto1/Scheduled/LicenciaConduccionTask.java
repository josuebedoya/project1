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

import com.proyecto1.Entities.EstadoConductor;
import com.proyecto1.Entities.Persona;
import com.proyecto1.Entities.TipoPersona;
import com.proyecto1.Entities.VehiculoConductor;
import com.proyecto1.Repository.PersonaRepository;
import com.proyecto1.Repository.VehiculoConductorRepository;

// Cada 2 minutos: verifica la vigencia de la licencia de los conductores registrados y, si
// esta vencida, restringe (RO) su relacion con cada vehiculo al que esta asociado.
@Component
public class LicenciaConduccionTask {

    @Autowired
    @Qualifier("IPersonaRepo")
    private PersonaRepository personaRepository;

    @Autowired
    @Qualifier("IVehiculoConductorRepo")
    private VehiculoConductorRepository vehiculoConductorRepository;

    private static final Logger logger = LogManager.getLogger(LicenciaConduccionTask.class);

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void verificarVigenciaLicencias() {
        LocalDate hoy = LocalDate.now();
        List<Persona> conductores = personaRepository.findByTipoPersona(TipoPersona.C);

        for (Persona conductor : conductores) {
            if (conductor.getFechaVigenciaLicencia() == null
                    || !conductor.getFechaVigenciaLicencia().isBefore(hoy)) {
                continue;
            }
            List<VehiculoConductor> relaciones = vehiculoConductorRepository.findByPersonaId(conductor.getId());
            for (VehiculoConductor relacion : relaciones) {
                if (relacion.getEstado() != EstadoConductor.RO) {
                    relacion.setEstado(EstadoConductor.RO);
                    vehiculoConductorRepository.save(relacion);
                    logger.info("CONDUCTOR {} RESTRINGIDO PARA OPERAR (RO) POR LICENCIA VENCIDA EL {} - VEHICULO {}",
                            conductor.getIdentificacion(), conductor.getFechaVigenciaLicencia(),
                            relacion.getVehiculo().getPlaca());
                }
            }
        }
    }
}
