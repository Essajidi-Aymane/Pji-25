package com.example.abe;

import com.example.abe.client.ClientDecryptor;
import com.example.abe.client.ClientEncryptor;
import com.example.abe.crypto.*;
import com.example.abe.model.*;
import com.example.abe.parser.AccessTreeNode;
import com.example.abe.server.*;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;


public class EndToEndTest {

    public static void main(String[] args) throws Exception {

        Pairing pairing = PairingFactory.getPairing("params/a.properties");
        Set<String> universe = Set.of("student","iot","admin","teacher");

        TA ta = new TA();                   
        ta.setup();

        Set<String> aliceAttrs = Set.of("student","iot");  // attributs côté Alice
        UserKeys aliceKeys = ta.keygen("alice", aliceAttrs, universe);

        ClientEncryptor clientEnc = new ClientEncryptor(pairing);
        String policyTxt = "(student AND iot) OR admin";
        String clear = "Hello ABE world!";
        PreCiphertext preCT = clientEnc.preEncrypt(clear, policyTxt, aliceKeys.EK);

        ServerEncryptor srvEnc = new ServerEncryptor(pairing);
        
        CipherText ct = srvEnc.outEncrypt(preCT, aliceKeys.UK);
        System.out.println(ct.C0.getField());
        System.out.println(aliceKeys.TK.getD().getField());
        ServerDecryptor srvDec = new ServerDecryptor(pairing);
        TransformedCT tct = srvDec.outDecrypt(ct, aliceKeys.TK, aliceAttrs);

        ClientDecryptor clientDec = new ClientDecryptor();
        Element k = clientDec.finalDecrypt(tct, aliceKeys.DK);

    
       

        System.out.println("Clé K (enc) : " + clientEnc.getLastK());  
        System.out.println("Clé K (dec) : " + k);
        System.out.println(clientEnc.getLastK().isEqual(k)
                ? "✅  K identiques"
                : "❌  K mismatch");

                System.out.println(clientEnc.getLastK().getField());
                System.out.println(k.getField());
    }
}
