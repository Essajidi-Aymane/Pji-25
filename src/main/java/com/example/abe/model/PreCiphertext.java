package com.example.abe.model;

import it.unisa.dia.gas.jpbc.Element;

public class PreCiphertext {

    public final Element C0 ;
    public final String encryptedMsg  ;
    public final String policy ;
    public PreCiphertext(Element C0, String encryptedMessage, String policy) {
        this.C0 = C0;
        this.encryptedMsg = encryptedMessage;
        this.policy = policy;
    }
 
    @Override
    public String toString() {
        return "PreCiphertext{\n" +
                "C0=" + C0 + ",\n" +
                "encryptedMessage='" + encryptedMsg+ '\'' + ",\n" +
                "policy='" + policy + '\'' + "\n}";
    }
    
}
