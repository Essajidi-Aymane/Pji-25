package com.example.abe.model;

import com.example.abe.parser.AccessTreeNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PreCiphertext {

    public final Element C;             // C0
    public final String encryptedMsg;   // Message chiffré
    public final AccessTreeNode root;   // Politique d’accès sous forme d’arbre

    public PreCiphertext(Element C, String encryptedMessage, AccessTreeNode root) {
        this.C = C;
        this.encryptedMsg = encryptedMessage;
        this.root = root;
    }

    @Override
    public String toString() {
        return "PreCiphertext{\n" +
                "C=" + C + ",\n" +
                "encryptedMsg='" + encryptedMsg + '\'' + ",\n" +
                "policy=" + new Gson().toJson(root) + "\n}";
    }

    public String toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("C", Base64.getEncoder().encodeToString(C.toBytes()));
        map.put("encryptedMsg", encryptedMsg);
        map.put("root", new Gson().toJson(root));  
        return new Gson().toJson(map);
    }

    public static PreCiphertext fromJson(String json, Pairing pairing) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(json, mapType);

        Element C = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(map.get("C"))).getImmutable();
        String encryptedMsg = map.get("encryptedMsg");

        AccessTreeNode root = gson.fromJson(map.get("root"), AccessTreeNode.class);

        return new PreCiphertext(C, encryptedMsg, root);
    }
}
