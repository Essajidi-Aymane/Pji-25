package com.example.abe.crypto;

import java.util.Map;

import it.unisa.dia.gas.jpbc.Element;

public class TK {
    

    private final Element D ; 
     private final Map<String, Element[]> tkMap ; 


    public TK(Element D ,  Map<String, Element[]> tkMap ) {
        this.D=D; 
        this.tkMap = tkMap;
    }
    public Element getD() {
        return D;
    }
    public Map<String, Element[]> getTkMap() {
        return tkMap;
    }

}
