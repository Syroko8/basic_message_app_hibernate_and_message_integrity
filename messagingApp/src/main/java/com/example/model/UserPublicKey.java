package com.example.model;

import java.security.PublicKey;

import javax.persistence.*;

import com.example.utils.KeyUtils;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase a partir de la cual se generará la entidad PublicKey, almacenando las claves públicas de todos los usuarios. 
 */
@Entity
public class UserPublicKey {

    // Atributos de la clase.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String owner;
    @Column(length = 2048)
    private String publicKey;
     
    // Constructor.
    public UserPublicKey(String owner, PublicKey publicKey) {
        this.owner = owner;
        this.publicKey = KeyUtils.publicKeyToString(publicKey);
    }

    public UserPublicKey(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = KeyUtils.publicKeyToString(publicKey);
    }
}
