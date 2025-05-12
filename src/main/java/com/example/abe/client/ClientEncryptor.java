package com.example.abe.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.example.abe.model.PreCiphertext;
import it.unisa.dia.gas.jpbc.Element;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import java.util.Base64;


public class ClientEncryptor {
    private final Pairing pairing ; 

    public ClientEncryptor(Pairing pairing) { 
        this.pairing= pairing ; 
    }

    public PreCiphertext preEncrypt (String msg, String policy , Element ek , Element[] up ) {
        Field Zp = pairing.getZr();
        Field G1 = pairing.getG1();
        Field GT = pairing.getGT();

        it.unisa.dia.gas.jpbc.Element s = Zp.newRandomElement().getImmutable() ; 

        Element C0 = pairing.getG1().newElement().set(up[0]).powZn(s).getImmutable() ; 

        Element sek = ek.mul(s).getImmutable() ; 
        Element K = pairing.pairing(pairing.getG1().newElement().setToOne(), pairing.getG1().newElement().setToOne()).powZn(sek).getImmutable(); 
        byte[] hashedK =hashElement(K) ; 
        String encrypted = xorWithHash(msg, hashedK);
        return new PreCiphertext(C0, encrypted, policy);


    }
    
    private byte[] hashElement(Element e) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(e.toBytes());
        } catch (Exception ex) {
            throw new RuntimeException("Erreur de hash sur K", ex);
        }
    }

        private String xorWithHash(String message, byte[] hash) {
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[msgBytes.length];
        for (int i = 0; i < msgBytes.length; i++) {
            result[i] = (byte) (msgBytes[i] ^ hash[i % hash.length]);
        }
        return Base64.getEncoder().encodeToString(result);
    }


}
