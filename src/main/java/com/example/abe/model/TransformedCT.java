package com.example.abe.model;
import it.unisa.dia.gas.jpbc.Element;

public class TransformedCT {

    public final Element transformedC1;
    public final String encMsg ;

    public TransformedCT(Element transformedC1 , String encMsg) {
        this.transformedC1 = transformedC1 ;
        this.encMsg = encMsg ;
    }


    @Override
    public String toString() {
        return "TransformedCT{\n" +
                "transformedC1=" + transformedC1 + ",\n" +
                "encmsg='" + encMsg + "'\n}";
    }}
