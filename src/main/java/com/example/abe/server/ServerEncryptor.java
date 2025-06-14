
package com.example.abe.server;
import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Map;
public class ServerEncryptor {

    private final Pairing pairing;


    public ServerEncryptor(Pairing pairing) {
        this.pairing = pairing;
    }



public CipherText outEncrypt(PreCiphertext preCT, Map<String, Element[]> up) {

    // Parcours récursif de l'arbre pour construire C_i et C_i′
    traverse(preCT.root, up);
    preCT.root.prepareForSerialization();

    return new CipherText(preCT.C, preCT.encryptedMsg, preCT.root);
}


public void traverse(AccessTreeNode node, Map<String, Element[]> up) {
    if (node.isLeaf()) {
        String attr = node.attr;

        Element[] upPair = up.get(attr);
      
    
        node.C = upPair[0].powZn(node.preC).getImmutable();
        node.C_prime = upPair[1].powZn(node.preC).getImmutable();

    } else {
        if (node.left != null) traverse(node.left, up);
        if (node.right != null) traverse(node.right, up);
    }
}



}