package com.example.abe.server;

import com.example.abe.model.CipherText;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import com.example.abe.model.TransformedCT;

public class ServerDecryptor {

    private final Pairing pairing ;

    public ServerDecryptor(Pairing p) {
        this.pairing = p ;

    }

    public TransformedCT outDecrypt(CipherText ct, Element[] TK) {
        Element g1_TK0 = ct.C0.getField().newElement().set(TK[0]).getImmutable();
        Element transformed = pairing.pairing(ct.C0, g1_TK0).getImmutable();
        return new TransformedCT(transformed, ct.encMsg);
    }

}
