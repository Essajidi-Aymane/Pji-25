package com.example.abe.model;

import it.unisa.dia.gas.jpbc.Element;
import java.util.HashMap;
import java.util.Map;
public  class CipherText {


    public  final Element C0 ;
    public final Element C1 ;
    public final String encMsg ;
    public final Map<String, Element> policyComponents ;


    public CipherText(Element c0 ,Element c1 , String encMsg ,  Map<String, Element> policyComponents) {
    this.C0 = c0 ;
    this.C1 = c1 ;
    this.encMsg = encMsg ;
    this.policyComponents = policyComponents  ;

    }

    @Override
    public String toString() {
        return "CipherText{" +
                "\nC0=" + C0 +
                ",\nC1=" + C1 +
                ",\nencMsg='" + encMsg + '\'' +
                ",\npolicyComponents=" + policyComponents +
                "\n}";
    }
}
