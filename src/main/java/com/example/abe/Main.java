package com.example.abe;

import com.example.abe.client.ClientDecryptor;
import com.example.abe.client.ClientEncryptor;
import com.example.abe.crypto.TA;
import com.example.abe.crypto.UserKeys;
import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.example.abe.model.TransformedCT;
import com.example.abe.parser.AccessPolicyParser;
import com.example.abe.parser.AccessTreeNode;
import com.example.abe.server.ServerDecryptor;
import com.example.abe.server.ServerEncryptor;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Arrays;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            Pairing pairing = PairingFactory.getPairing("params/a.properties");


            TA ta = new TA();
            ta.setup();
            String userId = "aymane123";
            Set<String> attrs = Set.of("student", "iot");
            UserKeys keys = ta.keygen(userId,attrs);
            for (String attr : attrs) {
                Element[] up = keys.UK.get(attr);
                System.out.println("UP for " + attr + ": " + Arrays.toString(up));
            }            String message = "Bonjour PJI";
            //String policy = "student AND iot";
            String policy = "((student AND niveau2) OR (iot AND prof))";            System.out.println("\n[TEST] Arbre de la politique : " + policy);
            AccessTreeNode root = AccessPolicyParser.parse(policy);
            printPolicyTree(root, 0);

            ClientEncryptor encryptor = new ClientEncryptor(pairing);
            PreCiphertext preCT = encryptor.preEncrypt(message, policy, keys.EK, keys.UK);            System.out.println("\n [CLIENT] PreCipherText ");
            System.out.println("C0 = " + preCT.C0);
            System.out.println("Encrypted message : " + preCT.encryptedMsg);
            System.out.println("Policy : " + preCT.policy);


            ServerEncryptor serverEncryptor = new ServerEncryptor(pairing);
            CipherText ciphertext = serverEncryptor.outEncrypt(preCT, keys.UK);
            System.out.println("[SERVER] CipherText complet  ");
            System.out.println("C1 " +ciphertext.encMsg );
            System.out.println("Policy Components  :" + ciphertext.policyComponents);

            ServerDecryptor serverDecryptor = new ServerDecryptor(pairing);
            String chosenAttr = "iot";
            Element[] tk = keys.TK.get(chosenAttr);
            if (tk == null) {
                System.out.println("Clé de transformation introuvable pour l'attribut: " + chosenAttr);
                return;
            }
            TransformedCT transCT = serverDecryptor.outDecrypt(ciphertext, tk);
            System.out.println("\n [SERVER] Transformed CipherText");
            System.out.println("transformedC1 = " + transCT.transformedC1);
            System.out.println("Encrypted Message = " + transCT.encMsg);
            ClientDecryptor decryptor = new ClientDecryptor();
            String decrypted = decryptor.finalDecrypt(transCT, keys.DK);

            System.out.println("\n===== Résultat =====");
            System.out.println("Message original   : " + message);
            System.out.println("Message déchiffré  : " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void printPolicyTree(AccessTreeNode node, int indent) {
        if (node == null) return;
        String prefix = "  ".repeat(indent);
        if (node.isLeaf()) {
            System.out.println(prefix + "- ATTR: " + node.attr);
        } else {
            System.out.println(prefix + "- OP: " + node.operator);
            printPolicyTree(node.left, indent + 1);
            printPolicyTree(node.right, indent + 1);
        }
    }

}
