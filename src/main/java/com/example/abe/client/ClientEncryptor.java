package com.example.abe.client;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.example.abe.crypto.PublicKey;
import com.example.abe.model.PreCiphertext;
import com.example.abe.parser.AccessPolicyParser;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        return new PreCiphertext(C, policy, treeNode);
      
    }


    public Element  getLastK() {
        return this.K;

    }

 /*    private String extractFirstAttr(String policy) {
        AccessTreeNode root = AccessPolicyParser.parse(policy);
        return getFirstAttribute(root);
    }
    private String getFirstAttribute(AccessTreeNode node) {
        if (node.isLeaf()) return node.attr;
        return getFirstAttribute(node.left);
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

       private Element H2(String input, Field zp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return zp.newElement().setFromHash(hash, 0, hash.length).getImmutable();
        } catch (Exception e) {
            throw new RuntimeException("Erreur dans H2 : " + e.getMessage());
        }
    } */
}