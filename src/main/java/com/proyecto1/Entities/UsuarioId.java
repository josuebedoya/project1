package com.proyecto1.Entities;

import java.io.Serializable;
import java.util.Objects;

// Llave primaria compuesta de Usuario: (idpersona, login).
// Garantiza que una persona tenga uno y solo un usuario asociado.
public class UsuarioId implements Serializable {

    private Long idpersona;
    private String login;

    public UsuarioId() {
    }

    public UsuarioId(Long idpersona, String login) {
        this.idpersona = idpersona;
        this.login = login;
    }

    public Long getIdpersona() {
        return idpersona;
    }

    public void setIdpersona(Long idpersona) {
        this.idpersona = idpersona;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsuarioId)) {
            return false;
        }
        UsuarioId that = (UsuarioId) o;
        return Objects.equals(idpersona, that.idpersona) && Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpersona, login);
    }
}
