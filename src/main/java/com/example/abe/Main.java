package com.example.abe;


import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import com.example.abe.crypto.* ;

import java.util.Arrays;

// test simple pour vérifier les dependencies
public class Main {
    public static void main(String[] args) {
        Pairing pairing = PairingFactory.getPairing("params/a.properties");
        try {
            TA ta = new TA();
            ta.setup();

            String user = "ayman123";
            UserKeys keys = ta.keygen(user);

            System.out.println("EK = " + keys.EK);
            System.out.println("DK = " + keys.DK);
            System.out.println("TK = " + Arrays.toString(keys.TK.get(user)));
            System.out.println("UK = " + Arrays.toString(keys.UK.get(user)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
