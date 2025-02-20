package com.example.dao;

import java.security.PublicKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.JFrame;
import com.example.model.Message;
import com.example.model.Print;
import com.example.model.UserPublicKey;
import com.example.screens.InboxScreen;
import com.example.utils.Crypto;
import com.example.utils.Hash;
import com.example.utils.KeyUtils;
import com.example.utils.SessionManager;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase encargada de interactuar con la entidad Message en base de datos. 
 */
public class MessageDAO {

    // Atributos de la clase.
    private EntityManagerFactory emf;
    private EntityManager em;
    private Crypto myCrypto;
    private SessionManager mySessionManager;
    private JFrame[] views;
    private Hash myHash;
    
    public MessageDAO(SessionManager newSessionManager){
        this.emf = Persistence.createEntityManagerFactory("example-unit");
        this.myCrypto = new Crypto();
        this.mySessionManager = newSessionManager;
        this.myHash = new Hash();
    }
    
    public void setViews(JFrame[] views){
        this.views = views;
    }

    // Método que inicia la transacción.
    private void openEm() {
    	this.em = emf.createEntityManager();
    	this.em.getTransaction().begin();
    }
    
    // Método que cierra la transacción.
    private void closeEm() {
    	this.em.close();
    }

    // Obtiene la clave del usuario emisor.
    public PublicKey getPublicKey(String userName){

        PublicKey userPublicKey = null;

        try {
            openEm();
            System.out.println("Ejecutando query para usuario: " + userName);
            TypedQuery<UserPublicKey> sql = em.createQuery("FROM UserPublicKey WHERE owner = :username", UserPublicKey.class);
            sql.setParameter("username", userName);
            
            // Ejecutamos la sentencia.
            UserPublicKey keyObject = sql.getSingleResult();
            userPublicKey = KeyUtils.stringToPublicKey(keyObject.getPublicKey());
        } catch (NoResultException e) {
            // El nombre de usuario no existe en la base de datos.
            System.out.println("No se encontró una clave pública para el usuario: " + userName);
            return null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally{
            closeEm();
        }
        // Devolvemos el valor.
        return userPublicKey;
    }

    // Método que obtiene la hueya de un mensaje.
    public Print getPrint(String sender, String receiver, Date time){
        Print print = null;

        try {
            openEm();
            TypedQuery<Print> sql = em.createQuery("from Print where sender = :sender and receiver = :receiver and time = :time", Print.class);
            sql.setParameter("sender", sender);
            sql.setParameter("receiver", receiver);
            sql.setParameter("time", time);
            
            // Ejecutamos la sentencia.
            print = sql.getSingleResult();
        } catch (NoResultException e) {
            // Si se produce la excepción, no existe hueya del mensaje.
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // Devolvemos el valor.
        return print;
    }

    // Método que comprueba la integridad del mensaje.
    public Boolean checkMessageIntegrity(Message message){
        
        String actualMessageHashed = null;
        String messageHash = null;
        try {

            // Obtenemos la clave publica del remitente.
            PublicKey userPublicKey = getPublicKey(message.getSender());
            
            // Obtenemos el objeto Print que se generó a partir del mensaje.
            Print messagePrint = getPrint(message.getSender(), message.getReceiver(), message.getTime());
            messageHash = messagePrint.getSignedMessageHash();

            // Obtenemos una hueya a partir del mensaje que estamos evaluando.
            actualMessageHashed = myHash.hashStr(message.getMsg());

            // Comprobamos la firma del hash.
            Boolean signatureIntegrity = myCrypto.checkFirm(userPublicKey, actualMessageHashed, messagePrint.getSignedMessageHash());
            // Si el mensaje no está firmado correctamente, no se puede asegurar su integridad.
            if(!signatureIntegrity){
                return false;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }        

        return true;
    }

    // Método para enviar un mensaje.
    public String sendMsg(String destinatary, String msg){
        try {

            // Creamos un objeto mensaje que se guardará en plano.
            Message newMessage = new Message(mySessionManager.getUser().getUsername(), destinatary, msg);
            
            // Creamos una hueya del mensaje, hasheando y cifrando el mensaje con la clave privada.
            String hashedMsg = myHash.hashStr(msg);
            Print newPrint = new Print(newMessage.getSender(), destinatary, newMessage.getTime(), myCrypto.digitalFirm(
                KeyUtils.stringToPrivateKey(mySessionManager.getUser().getPrivateKey()), hashedMsg));
            
            // Almacenamos el mensaje y hueya.
            openEm();
            em.persist(newMessage);
            em.persist(newPrint);
            em.getTransaction().commit();
            closeEm();

            System.err.println("El mensaje ha sido enviado correctamente.");
            return "El mensaje ha sido enviado correctamente.";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return("Se ha producido un problema al mandar el mensaje.");
        }
    }

    // Método para obtener y devolver todos los mensajes recividos por el usuario actual.
    public void getMessages(){

        List<Message> messages = new ArrayList<Message>();

        try {
            openEm();

            TypedQuery<Message> sql = em.createQuery("from Message where receiver = :username", Message.class);
            sql.setParameter("username", mySessionManager.getUser().getUsername());

            messages = sql.getResultList();
		    closeEm();

            // Comprobamos la integridad de los mensajes.
            for(Message message : messages){
                if(!checkMessageIntegrity(message)){             
                    // Preparamos el mensaje corrupto.
                    message.setSender("Warning");
                    message.setMsg("La integridad de este mansaje ha sido corrompida.");
                }
            }

            // Insertamos los mensajes en la vista.
            InboxScreen screen = (InboxScreen) views[2];
            screen.setMessages(messages);

        } catch (NoResultException e) {
            // Si la lista es nula, no ocurre nada.
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
