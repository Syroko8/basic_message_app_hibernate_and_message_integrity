package com.example.model;

import java.sql.Date;
import java.util.Calendar;

import javax.persistence.*;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase a partir de la cual se generará la entidad Message. 
 */
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    private String receiver;
    private String msg;
    private Date time;
    
    public Message(String sender, String receiver, String msg){
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        // Obtenemos la fecha de envío, obteniendo el tiempo en milisegundos e introduciendolos en la clase Date.
        Long milis = System.currentTimeMillis(); 
        this.time = new  Date(milis);
    }

    public Message(){
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
}
