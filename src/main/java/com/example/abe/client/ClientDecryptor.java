package com.example.abe.client;
import com.example.abe.model.TransformedCT;
import it.unisa.dia.gas.jpbc.Element;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class ClientDecryptor {

    private Element lastK ; 

    public Element getlastK() {
        return lastK;
    }

    

    public Element finalDecrypt(TransformedCT trasnct , Element dk ) {

        Element K = trasnct.transformedC1.powZn(dk).getImmutable() ;


        byte[] hash = hashElement(K) ;
        lastK = K; 

        return lastK;
    }

    private byte[] hashElement(Element e) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(e.toBytes());
        } catch (Exception ex) {
            throw new RuntimeException("Erreur de hash dans finalDecrypt", ex);
        }
    }
    private String xorDecrypt(String base64Encrypted, byte[] hash) {
        byte[] enc = Base64.getDecoder().decode(base64Encrypted);
        byte[] result = new byte[enc.length];
        for (int i = 0; i < enc.length; i++) {
            result[i] = (byte) (enc[i] ^ hash[i % hash.length]);
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    public Element getLastK() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLastK'");
    }

}
