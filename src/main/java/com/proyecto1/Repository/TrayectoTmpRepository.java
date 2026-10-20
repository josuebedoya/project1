package com.proyecto1.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto1.Entities.TrayectoTmp;

@Repository("ITrayectoTmpRepo")
public interface TrayectoTmpRepository extends JpaRepository<TrayectoTmp, Long> {

    List<TrayectoTmp> findByIdCargueOrderByIdAsc(Long idCargue);
}
