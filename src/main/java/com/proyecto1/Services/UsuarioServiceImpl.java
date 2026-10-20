package com.proyecto1.Services;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.DTO.ApiKeyResponse;
import com.proyecto1.DTO.TokenResponse;
import com.proyecto1.Entities.Usuario;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.UsuarioRepository;
import com.proyecto1.Security.JwtUtil;
import com.proyecto1.Services.Interfaces.IUsuarioService;

@Service("UsuarioService")
public class UsuarioServiceImpl implements IUsuarioService {

    // ========== INYECCION DE DEPENDENCIAS ==========
    @Autowired
    @Qualifier("IUsuarioRepo")
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ====================== LOGS ======================
    private static final Logger logger = LogManager.getLogger(UsuarioServiceImpl.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Cambia el password de un usuario especifico (login en la URL, password nuevo en el body).
    @Override
    @Transactional
    public void cambiarPassword(String login, String nuevaPassword) {
        Usuario usuario = findByLogin(login);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        logger.info("PASSWORD ACTUALIZADO PARA EL USUARIO {}", login);
    }

    // Genera nuevamente el APIKey de un usuario especifico.
    @Override
    @Transactional
    public ApiKeyResponse regenerarApikey(String login) {
        Usuario usuario = findByLogin(login);
        String nuevaApikey = UUID.randomUUID().toString();
        usuario.setApikey(nuevaApikey);
        usuarioRepository.save(usuario);
        logger.info("APIKEY REGENERADO PARA EL USUARIO {}", login);
        return new ApiKeyResponse(usuario.getLogin(), nuevaApikey);
    }

    // Login basico: a cambio de login+password devuelve el apikey del usuario, para que el
    // frontend no tenga que guardar/pegar el apikey a mano. No crea sesion ni token nuevo;
    // el apikey ya existente sigue siendo el unico mecanismo de autenticacion de la API.
    @Override
    public ApiKeyResponse login(String login, String password) {
        Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            logger.error("ERROR LOGIN: CREDENCIALES INVALIDAS PARA EL LOGIN {}!", login);
            throw new BusinessException("Login o contraseña incorrectos");
        }
        return new ApiKeyResponse(usuario.getLogin(), usuario.getApikey());
    }

    // Autenticacion real del proyecto principal: valida login+password y devuelve un JWT, que
    // es lo que ahora exige JwtAuthFilter en lugar del apikey en el header.
    @Override
    public TokenResponse authenticate(String login, String password) {
        Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            logger.error("ERROR AUTHENTICATE: CREDENCIALES INVALIDAS PARA EL LOGIN {}!", login);
            throw new BusinessException("Login o contraseña incorrectos");
        }
        String token = jwtUtil.generarToken(usuario.getLogin());
        return new TokenResponse(usuario.getLogin(), token);
    }

    private Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con login " + login));
    }
}
