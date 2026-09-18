package com.proyecto1.Entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.IdClass;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;

// Usuarios del sistema. Solo las personas de tipo ADMINISTRATIVO tienen usuario,
// y una persona tiene uno y solo un usuario (llave primaria compuesta idpersona+login
// mas restriccion unica sobre idpersona para forzar la relacion 1 a 1).
@Entity
@Table(name = "Usuario", schema = "PPOOII", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USUARIO_IDPERSONA", columnNames = "IdPersona"),
        @UniqueConstraint(name = "UQ_USUARIO_APIKEY", columnNames = "ApiKey")
})
@IdClass(UsuarioId.class)
public class Usuario implements Serializable {

    @Id
    @Column(name = "IdPersona")
    private Long idpersona;

    @Id
    @NotBlank(message = "El login es obligatorio")
    @Column(name = "Login", nullable = false, length = 30)
    private String login;

    @MapsId("idpersona")
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "IdPersona", nullable = false, insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_PERSONA"))
    private Persona persona;

    @NotBlank(message = "El password es obligatorio")
    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "El apikey es obligatorio")
    @Column(name = "ApiKey", nullable = false, length = 255, unique = true)
    private String apikey;

    public Usuario() {
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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    @Override
    public String toString() {
        return "Usuario [idpersona=" + idpersona + ", login=" + login + ", apikey=" + apikey + "]";
    }
}
