package com.example.abe.model;

import com.example.abe.parser.AccessTreeNode;
import com.example.abe.parser.SerializableAccessTreeNode;
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
        root.prepareForSerialization();
        SerializableAccessTreeNode serialRoot = root.toSerializable();

Map<String, Object> map = new HashMap<>();
map.put("C", Base64.getEncoder().encodeToString(C.toBytes()));
map.put("encryptedMsg", encryptedMsg);
map.put("root", serialRoot);

return new Gson().toJson(map);  

    }


 public static PreCiphertext fromJson(String json, Pairing pairing) {
    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
    Map<String, Object> map = gson.fromJson(json, mapType);

    String cBase64 = (String) map.get("C");
    Element C = pairing.getG1()
        .newElementFromBytes(Base64.getDecoder().decode(cBase64))
        .getImmutable();

    String encryptedMsg = (String) map.get("encryptedMsg");

    SerializableAccessTreeNode serialRoot = gson.fromJson(
        gson.toJson(map.get("root")),
        SerializableAccessTreeNode.class
    );

    AccessTreeNode root = AccessTreeNode.fromSerializable(serialRoot, pairing);
    System.out.println("[PreCiphertext.fromJson] root.C_b64 = " + root.left.C_b64);
System.out.println("[PreCiphertext.fromJson] root.C     = " + root.left.C);


    return new PreCiphertext(C, encryptedMsg, root);
} 


}
