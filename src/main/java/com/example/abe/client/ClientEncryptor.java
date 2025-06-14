package com.example.abe.client;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import com.example.abe.crypto.PublicKey;
import com.example.abe.model.PreCiphertext;
import com.example.abe.parser.AccessPolicyParser;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;



public class ClientEncryptor {
    private final Pairing pairing ;
    private Element K ; 

    public ClientEncryptor(Pairing pairing) { 
        this.pairing= pairing ; 
    }


    public PreCiphertext preEncrypt(String msg, String policy, Element ek ){
        Field Zp = pairing.getZr();
        Element s = Zp.newRandomElement().getImmutable();

        AccessTreeNode treeNode = AccessPolicyParser.parse(policy, s , ek, Zp );
      
        PublicKey pb = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("pb.key"))) {
            pb = (PublicKey) in.readObject();
            System.out.println("Clé publique chargée depuis pb.key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element C = pb.getH(pairing).powZn(s);
        K= pb.getE_gg_alpha(pairing).powZn(s); 
        System.out.println("la clé K : "+ K);
 byte[] hash = hashElement(K);
    byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
    byte[] xor = new byte[msgBytes.length];
    for (int i = 0; i < msgBytes.length; i++) {
        xor[i] = (byte) (msgBytes[i] ^ hash[i % hash.length]);
    }

    String encMsg = Base64.getEncoder().encodeToString(xor);


        return new PreCiphertext(C, encMsg, treeNode);
      
    }

    private byte[] hashElement(Element e) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(e.toBytes());
    } catch (Exception ex) {
        throw new RuntimeException("Erreur de hash", ex);
    }
}


    public Element  getLastK() {
        return this.K;

    }

 

}