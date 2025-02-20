package com.example.model;

import java.security.PrivateKey;

import javax.persistence.*;

import com.example.utils.KeyUtils;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase a partir de la cual se generará la entidad User. 
 */
@Entity
public class User {

    // Atributos de la clase.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String passwd;
    @Column(length = 2048)
    private String privateKey;
     
    // Constructor.
    public User(String username, String passwd, PrivateKey privateKey) {
        this.username = username;
        this.passwd = passwd;
        this.privateKey = KeyUtils.privateKeyToString(privateKey);
    }
    
    public User() {
    	
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = KeyUtils.privateKeyToString(privateKey);
    }
 
}

