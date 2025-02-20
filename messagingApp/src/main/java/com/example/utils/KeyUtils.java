package com.example.utils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Nicolás Puebla Martín
 * 
 * Clase que contiene los métodos necesarios para convertir String a PrivateKey o PublicKey y biceversa. 
 */
public class KeyUtils {

    // Método para convertir un PrivateKey en String.
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Método para convertir un PublicKey en String.
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Método para convertir un String en un PrivateKey.
    public static PrivateKey stringToPrivateKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
        return keyFactory.generatePrivate(keySpec);
    }

    // Método corregido para convertir un String en un PublicKey.
    public static PublicKey stringToPublicKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
        return keyFactory.generatePublic(keySpec);
    }
}
