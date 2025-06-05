package com.example.abe.cli;

import com.example.abe.client.ClientDecryptor;
import com.example.abe.client.ClientEncryptor;
import com.example.abe.crypto.TA;
import com.example.abe.crypto.UserKeys;
import com.example.abe.model.*;
import com.example.abe.util.JsonUtil;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class AbeCLI {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Pairing pairing = PairingFactory.getPairing("params/a.properties");

        // Saisie utilisateur
        System.out.println("=== Terminal ABE Utilisateur ===");
        System.out.print("Entrez votre message à chiffrer : ");
        String message = scanner.nextLine();

        System.out.print("Entrez la politique d'accès (ex: student AND iot) : ");
        String policy = scanner.nextLine();

        System.out.print("Entrez vos attributs séparés par des virgules (ex: student,iot) : ");
        String attrInput = scanner.nextLine();
        Set<String> userAttrs = new HashSet<>(Arrays.asList(attrInput.split(",")));

        // Génération des clés
        TA ta = new TA(); ta.setup();
        UserKeys keys = ta.keygen("user-cli", userAttrs);

        // Pré-chiffrement local
        ClientEncryptor encryptor = new ClientEncryptor(pairing);
        PreCiphertext preCT = encryptor.preEncrypt(message, policy, keys.EK, keys.UK);

        //  Envoi au backend (encrypt)
        String encryptJson = JsonUtil.wrapEncryptRequest(preCT, keys.UK);
        String encryptResponse = post("http://localhost:8080/api/encrypt", encryptJson);
        CipherText ct = CipherText.fromJson(encryptResponse, pairing);

        System.out.println("\n Message chiffré reçu du serveur.");

        // Envoi au backend (decrypt)
        String attrForTK = userAttrs.iterator().next();
        String tkJson = JsonUtil.encodeElementArray(keys.TK.get(attrForTK));
        String decryptJson = JsonUtil.wrapDecryptRequest(ct, tkJson, userAttrs);
        String decryptResponse = post("http://localhost:8080/api/decrypt", decryptJson);
        TransformedCT trans = TransformedCT.fromJson(decryptResponse, pairing);

        // Dechiffrement local
        ClientDecryptor decryptor = new ClientDecryptor();
        String clear = decryptor.finalDecrypt(trans, keys.DK);

        System.out.println("\n===== Résultat =====");
        System.out.println("Message original   : " + message);
        System.out.println("Message déchiffré  : " + clear);
    }

    public static String post(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        int status = con.getResponseCode();
        InputStream stream = (status >= 200 && status < 300)
                ? con.getInputStream()
                : con.getErrorStream();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            if (status >= 400) throw new IOException("Erreur HTTP " + status + ": " + content);
            return content.toString();
        }
    }

}
