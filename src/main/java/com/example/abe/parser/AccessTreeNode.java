package com.example.abe.parser;

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
    public String preC_b64; // pour sérialisation JSON


    public void prepareForSerialization() {
    if (preC != null) {
        preC_b64 = java.util.Base64.getEncoder().encodeToString(preC.toBytes());
    }
    if (left != null) left.prepareForSerialization();
    if (right != null) right.prepareForSerialization();
}
public void reconstructPreC(it.unisa.dia.gas.jpbc.Pairing pairing) {
    if (preC_b64 != null) {
        byte[] bytes = java.util.Base64.getDecoder().decode(preC_b64);
        preC = pairing.getZr().newElementFromBytes(bytes).getImmutable();
    }
    if (left != null) left.reconstructPreC(pairing);
    if (right != null) right.reconstructPreC(pairing);
}

    public boolean isLeaf() {
        return operator == null;
    }
    public SerializableAccessTreeNode toSerializable() {
    SerializableAccessTreeNode s = new SerializableAccessTreeNode();
    s.attr = this.attr;
    s.operator = this.operator;
    s.xIndex = this.xIndex;
    s.preC_b64 = this.preC_b64;
    if (left != null) s.left = left.toSerializable();
    if (right != null) s.right = right.toSerializable();
    return s;
}
public static AccessTreeNode fromSerializable(SerializableAccessTreeNode s, it.unisa.dia.gas.jpbc.Pairing pairing) {
    AccessTreeNode node = new AccessTreeNode();
    node.attr = s.attr;
    node.operator = s.operator;
    node.xIndex = s.xIndex;
    node.preC_b64 = s.preC_b64;

    if (s.preC_b64 != null) {
        byte[] bytes = java.util.Base64.getDecoder().decode(s.preC_b64);
        node.preC = pairing.getZr().newElementFromBytes(bytes).getImmutable();
    }

    if (s.left != null) node.left = fromSerializable(s.left, pairing);
    if (s.right != null) node.right = fromSerializable(s.right, pairing);
    return node;
}

}

