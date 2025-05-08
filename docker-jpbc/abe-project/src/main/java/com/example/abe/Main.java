package com.example.abe;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
// test simple pour vérifier les dependencies
public class Main
{
    public static void main( String[] args )
    {
        Pairing pairing = PairingFactory.getPairing("params/a.properties");
        Element g = pairing.getG1().newRandomElement();
        System.out.println("Element G1 : " + g);
    }
}
