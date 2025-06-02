package com.example.abe.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.example.abe.model.PreCiphertext;
import com.example.abe.parser.AccessPolicyParser;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import java.util.Base64;
import java.util.Map;


public class ClientEncryptor {
    private final Pairing pairing ; 

    public ClientEncryptor(Pairing pairing) { 
        this.pairing= pairing ; 
    }

    public PreCiphertext preEncrypt(String msg, String policy, Element ek, Map<String, Element[]> ukMap) {
        Field Zp = pairing.getZr();
        Element s = Zp.newRandomElement().getImmutable();

        String attr = extractFirstAttr(policy);
        Element[] up = ukMap.get(attr);

        if (up == null) {
            throw new IllegalArgumentException("Missing UK for attribute: " + attr);
        }

        // C0 = up1^s
        Element C0 = up[0].powZn(s).getImmutable();

        Element sek = ek.mul(s).getImmutable();
        Element K = pairing.pairing(pairing.getG1().newElement().setToOne(), pairing.getG1().newElement().setToOne())
                .powZn(sek).getImmutable();

        byte[] hashedK = hashElement(K);
        String encrypted = xorWithHash(msg, hashedK);

        return new PreCiphertext(C0, encrypted, policy);
    }

    private String extractFirstAttr(String policy) {
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


}
