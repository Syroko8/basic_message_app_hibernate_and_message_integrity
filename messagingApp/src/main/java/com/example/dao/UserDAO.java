package com.example.dao;


import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.example.model.User;
import com.example.model.UserPublicKey;
import com.example.utils.Crypto;
import com.example.utils.Hash;
import com.example.utils.SessionManager;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase encargada de interactuar con la entidad User en base de datos. 
 */
public class UserDAO {

    // Atributos de la clase.
    private EntityManagerFactory emf;
    private EntityManager em;
    private Hash myHash;
    private SessionManager mySessionManager;
    private Crypto mCrypto;
    
    public UserDAO(SessionManager newSessionManager) {
        this.emf = Persistence.createEntityManagerFactory("example-unit");
        this.myHash = new Hash();
        this.mySessionManager = newSessionManager;
        this.mCrypto = new Crypto();
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

    // Método que comprueba que los datos introducidos son correctos.
    public boolean logIn(String userName, String passwd){

        // Obtenemos el hash de la contraseña.
        String hashedPasswd = myHash.hashStr(passwd);

        // Comprobamos que el usuario exista.
        try {
            openEm();
            System.err.println(userName + " " + hashedPasswd);
            TypedQuery<User> sql = em.createQuery("from User where username = :username and passwd = :passwd", User.class);
            sql.setParameter("username", userName);
            sql.setParameter("passwd", hashedPasswd);

            User user = sql.getSingleResult();
           
            // Almacenamos el usuario obtenido en el session manager.
            mySessionManager.setUser(user);

        } catch (NoResultException e) {
            // Si no se ha obtenido ningún usuario, el login es fallido.
            return false;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }finally {
            closeEm();
        }
        return true;
    }

    public void register(String userName, String passwd){

        // Obtenemos el hash de la contraseña.
        String hashedPasswd = myHash.hashStr(passwd);
    
        // Obtenemos las claves.
        KeyPair keyPair = this.mCrypto.generateBase64Keys(); 

        // Instanciamos un objeto user.
        User newUser = new User(userName, hashedPasswd, keyPair.getPrivate());
        
        // Instanciamos un objeto PublicKey.
        UserPublicKey userPublicKey = new UserPublicKey(userName, keyPair.getPublic());

        // Almacenamos el nuevo usuario y su clave pública.
        try {
            System.err.println(newUser.toString());
            openEm();
            em.persist(newUser); 
            em.persist(userPublicKey);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            } 
        } finally {
            closeEm();
        }
    }

    // Método que comprueba si el nombre introducido ya existe.
    public boolean userNameUsed(String userName){
        try {
            openEm();
            TypedQuery<User> sql = em.createQuery("from User where username = :username", User.class);
            sql.setParameter("username", userName);
            User user = sql.getSingleResult();

        } catch (NoResultException e) {
            // Si no encuentra el usuario, se provocará la excepción, por lo tanto el nombre no está en uso.
            return false;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }finally {
            closeEm();
        }
        System.err.println("Usuario existente");

        return true;
    }

}
