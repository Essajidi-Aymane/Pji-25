package com.example.abe.parser;

import java.util.Base64;

import it.unisa.dia.gas.jpbc.Element;

public class AccessTreeNode {
    public String attr;
    public String operator; 
    public AccessTreeNode left;
    public AccessTreeNode right;
    public  Element lambda; 
    public  Element preC;      
    public Element C; 
    public Element C_prime; 
    public int xIndex;
    public String preC_b64; 
    public String C_b64;
    public String C_prime_b64;



    public boolean isLeaf() {
        return operator == null;
    }
    public SerializableAccessTreeNode toSerializable() {
    SerializableAccessTreeNode s = new SerializableAccessTreeNode();
    s.attr = this.attr;
    s.operator = operator;
    s.xIndex = xIndex;
    s.preC_b64 = preC_b64;
    s.C_b64 = C_b64;
    s.C_prime_b64 = C_prime_b64;

    if (left != null) s.left = left.toSerializable();
    if (right != null) s.right = right.toSerializable();
    return s;
}
public void prepareForSerialization() {
    if (preC != null) {
        preC_b64 = Base64.getEncoder().encodeToString(preC.toBytes());
    }
    if (C != null) {
        C_b64 = Base64.getEncoder().encodeToString(C.toBytes());
    }
    if (C_prime != null) {
        C_prime_b64 = Base64.getEncoder().encodeToString(C_prime.toBytes());
    }

    // Appel récursif pour les enfants gauche et droit
    if (left != null) left.prepareForSerialization();
    if (right != null) right.prepareForSerialization();
}

public static AccessTreeNode fromSerializable(SerializableAccessTreeNode s, it.unisa.dia.gas.jpbc.Pairing pairing) {
    AccessTreeNode node = new AccessTreeNode();
    node.attr = s.attr;
    node.operator = s.operator;
    node.xIndex = s.xIndex;
    node.preC_b64 = s.preC_b64;
    node.C_b64 = s.C_b64;
    node.C_prime_b64 = s.C_prime_b64;

    if (s.preC_b64 != null) {
        byte[] bytes = java.util.Base64.getDecoder().decode(s.preC_b64);
        node.preC = pairing.getZr().newElementFromBytes(bytes).getImmutable();
    }
    if (s.C_b64 != null) {
    byte[] bytes = Base64.getDecoder().decode(s.C_b64);
    node.C = pairing.getG1().newElementFromBytes(bytes).getImmutable();
}
if (s.C_prime_b64 != null) {
    byte[] bytes = Base64.getDecoder().decode(s.C_prime_b64);
    node.C_prime = pairing.getG1().newElementFromBytes(bytes).getImmutable();
}


    if (s.left != null) node.left = fromSerializable(s.left, pairing);
    if (s.right != null) node.right = fromSerializable(s.right, pairing);
    return node;
}

}

