package com.example.model;

import java.sql.Date;
import javax.persistence.*;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase a partir de la cual se generará la entidad Print, almacenando la huella firmada de cada mensaje. 
 */
@Entity
public class Print {

    // Atributos de la clase.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    private String receiver;
    private Date time;
    @Column(length = (600))
    private String signedMessageHash;
    
    // Constructor.
    public Print(String sender, String receiver, Date time, String signedMessageHash) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.signedMessageHash = signedMessageHash;
    }

    public Print(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSignedMessageHash() {
        return signedMessageHash;
    }

    public void setSignedMessageHash(String newSignedMessageHash) {
        this.signedMessageHash = newSignedMessageHash;
    }    
}
