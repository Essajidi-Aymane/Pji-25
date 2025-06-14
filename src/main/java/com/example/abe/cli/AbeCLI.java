package com.example.abe.cli;

import com.example.abe.client.ClientDecryptor;
import com.example.abe.client.ClientEncryptor;
import com.example.abe.crypto.*;
import com.example.abe.model.*;
import com.example.abe.server.ServerEncryptor;
import com.example.abe.server.ServerDecryptor;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.*;


public class AbeCLI {

    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in);
        System.out.println("=== ABE CLI ===");
        System.out.print("Message à chiffrer             : ");
        String message = in.nextLine();

        System.out.print("Politique (ex: student AND iot) : ");
        String policy  = in.nextLine();

        System.out.print("Vos attributs (ex: student,iot) : ");
        Set<String> userAttrs = new HashSet<>(
                Arrays.stream(in.nextLine().split(","))
                      .map(String::trim)
                      .filter(s -> !s.isEmpty())
                      .toList());

        Pairing pairing = PairingFactory.getPairing("params/a.properties");
        TA ta = new TA(); ta.setup();

        Set<String> universe = new HashSet<>(userAttrs);
        Arrays.stream(policy.split("\\W+")).forEach(universe::add);

        UserKeys keys = ta.keygen("cli-user", userAttrs, universe);

        ClientEncryptor clientEnc = new ClientEncryptor(pairing);
        PreCiphertext pre = clientEnc.preEncrypt(message, policy, keys.EK);

        ServerEncryptor srvEnc = new ServerEncryptor(pairing);
        CipherText ct = srvEnc.outEncrypt(pre, keys.UK);

        ServerDecryptor srvDec = new ServerDecryptor(pairing);
        TransformedCT tct = srvDec.outDecrypt(ct, keys.TK, userAttrs);

        ClientDecryptor clientDec = new ClientDecryptor(pairing);
        String clear = clientDec.finalDecrypt(tct, keys.DK);

        System.out.println("\n===== Résultat =====");
        System.out.println("Message original   : " + message);
        System.out.println("Message déchiffré  : " + clear);

        
    }
}
