package com.example.abe.crypto;

import java.io.Serializable;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class PublicKey implements Serializable {
  
    private static final long serialVersionUID = 1L;

    private byte[] h ; 
    private byte[] e_gg_alpha ; 
    public PublicKey(Element h , Element e_gg_alpha) { 
        this.e_gg_alpha = e_gg_alpha.toBytes(); 
        this.h = h.toBytes(); 
    }
    public Element getE_gg_alpha(Pairing pairing) {
        return pairing.getGT().newElementFromBytes(e_gg_alpha).getImmutable();
    }
    public Element getH(Pairing pairing) {
        return pairing.getG1().newElementFromBytes(h).getImmutable();
    }
}
