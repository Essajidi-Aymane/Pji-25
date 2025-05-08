package com.example.abe.crypto;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

public class UserKeys {

    public final Element EK ;
    public final Element DK ;
    public final Map<String, Element[]> TK;
    public final Map<String,Element[]> UK ;

    public UserKeys(Element ek , Element dk , Map<String,Element[]> tk, Map<String,Element[]> uk) {
        this.EK= ek;
        this.DK = dk ;
        this.TK = tk ;
        this.UK = uk ;

    }
}
