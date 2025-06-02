package com.example.abe.server;
import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.example.abe.parser.AccessPolicyParser;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.HashMap;
import java.util.Map;
public class ServerEncryptor {

    private final Pairing pairing;


    public ServerEncryptor(Pairing pairing) {
        this.pairing = pairing;
    }

    public CipherText outEncrypt(PreCiphertext preCT, Map<String, Element[]> up) {
        Element C0 = preCT.C0;
        String encMsg = preCT.encryptedMsg;
        String policy = preCT.policy;
        Element[] C1Wrapper = new Element[]{ pairing.getGT().newOneElement().getImmutable() };


        Map<String, Element> policyComponents = new HashMap<>();

        AccessTreeNode root = AccessPolicyParser.parse(policy);
        applyTreeEncryption(root, up, C0, policyComponents, C1Wrapper);
        Element C1 = C1Wrapper[0];
        return new CipherText(C0, C1, encMsg, policyComponents);
    }
    private void applyTreeEncryption(
            AccessTreeNode node,
            Map<String, Element[]> up,
            Element C0,
            Map<String, Element> policyComponents,
            Element[] C1Wrapper
    ) {
        if (node == null) return;

        if (node.isLeaf()) {
            String attr = node.attr;
            if (up.containsKey(attr)) {
                Element[] upAttr = up.get(attr);

                // Contribution au C1
                Element partial = pairing.pairing(C0, upAttr[0]).getImmutable();
                C1Wrapper[0] = C1Wrapper[0].mul(partial).getImmutable();

                Element hashed = pairing.getG1().newElement().setFromHash(attr.getBytes(), 0, attr.length()).getImmutable();
                Element comp = hashed.powZn(pairing.getZr().newRandomElement()).getImmutable();
                policyComponents.put(attr, comp);
            }
        } else {
            applyTreeEncryption(node.left, up, C0, policyComponents, C1Wrapper);
            applyTreeEncryption(node.right, up, C0, policyComponents, C1Wrapper);
        }
    }


}


