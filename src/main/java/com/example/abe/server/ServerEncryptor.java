package com.example.abe.server;
import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.HashMap;
import java.util.Map;
public class ServerEncryptor {

    private final Pairing pairing;


    public ServerEncryptor(Pairing pairing) {
        this.pairing = pairing;
    }

    public CipherText outEncrypt(PreCiphertext preCT, Element[] up) {
        Element C0 = preCT.C0;

        String encMsg = preCT.encryptedMsg;
        String policy = preCT.policy;


        Element C1 = pairing.pairing(C0, up[0]).getImmutable();

        Map<String, Element> policyComponents = new HashMap<>();

        for (String attr : policy.split(" AND | OR ")) {
            attr = attr.trim();
            if (!attr.isEmpty()) {
                Element hashed = pairing.getG1().newElement().setFromHash(attr.getBytes(), 0, attr.length()).getImmutable();
                Element comp = hashed.powZn(pairing.getZr().newRandomElement()).getImmutable();
                policyComponents.put(attr, comp);

            }
        }
        return new CipherText(C0,C1, encMsg , policyComponents);
    }

}


