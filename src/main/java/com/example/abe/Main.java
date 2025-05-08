package com.example.abe;


import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import com.example.abe.crypto.* ;

import java.util.Arrays;
import java.util.Set;

// test simple pour vérifier les dependencies
public class Main {
    public static void main(String[] args) {
        Pairing pairing = PairingFactory.getPairing("params/a.properties");
        try {
            TA ta = new TA();
            ta.setup();

            String user = "ayman123";
            Set<String> attrs = Set.of("student", "iot", "niveau2");
            UserKeys keys = ta.keygen(user, attrs);

            System.out.println("EK = " + keys.EK);
            System.out.println("DK = " + keys.DK);
            for (String attr : attrs) {
                System.out.println("[" + attr + "] TK = " + Arrays.toString(keys.TK.get(attr)));
                System.out.println("[" + attr + "] UK = " + Arrays.toString(keys.UK.get(attr)));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
