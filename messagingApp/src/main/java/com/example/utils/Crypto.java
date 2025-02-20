package com.example.utils;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import javax.crypto.*;

public class Crypto {
    public String messageCrypted(String message, PrivateKey privateKey) {
        String messageCrp = "";
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedBytes = rsaCipher.doFinal(message.getBytes("UTF-8"));
            messageCrp = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageCrp;
    }

    public String messageDecrypted(String messageCrypted, PublicKey publicKey) {
        String messageDcrp = "";
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.DECRYPT_MODE, publicKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(messageCrypted);
            byte[] decryptedBytes = rsaCipher.doFinal(encryptedBytes);
            messageDcrp = new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageDcrp;
    }

    // Método para generar un par de claves RSA (pública y privada) en Base64
    public KeyPair generateBase64Keys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            
            return keyPair;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para generar la firma digital de un mensaje
    public String digitalFirm(PrivateKey privateKey, String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            byte[] firm = signature.sign();

            return Base64.getEncoder().encodeToString(firm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para comprobar si la firma es válida
    public boolean checkFirm(PublicKey publicKey, String message, String signatureBase64) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());
            byte[] firm = Base64.getDecoder().decode(signatureBase64);

            return signature.verify(firm);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error al decodificar la firma");
        }
        return false;
    }
    
}
