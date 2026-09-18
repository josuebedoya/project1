package com.proyecto1.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto1.Entities.Usuario;
import com.proyecto1.Entities.UsuarioId;

@Repository("IUsuarioRepo")
public interface UsuarioRepository extends JpaRepository<Usuario, UsuarioId> {

    Optional<Usuario> findByLogin(String login);

    Optional<Usuario> findByIdpersona(Long idpersona);

    Optional<Usuario> findByApikey(String apikey);
}
