
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


    return new CipherText(preCT.C, preCT.encryptedMsg, preCT.root);
}
private void traverse(AccessTreeNode node, Map<String, Element[]> up) {
    if (node.isLeaf()) {
        String attr = node.attr;

        Element[] upPair = up.get(attr);
        if (upPair == null) {
            throw new IllegalArgumentException("Clé publique manquante pour l'attribut : " + attr);
        }

        Element up1 = upPair[0];
        Element up2 = upPair[1];

        node.C= up1.powZn(node.preC).getImmutable();

        // C_i′ = up2^lambda
        node.C_prime= up2.powZn(node.preC).getImmutable();

       
    } else {
        traverse(node.left,  up);
        traverse(node.right, up);
    }
}

}

     