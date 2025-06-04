package com.example.abe.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PreCiphertext {

    public final Element C0 ;
    public final String encryptedMsg  ;
    public final String policy ;
    public PreCiphertext(Element C0, String encryptedMessage, String policy) {
        this.C0 = C0;
        this.encryptedMsg = encryptedMessage;
        this.policy = policy;
    }
 
    @Override
    public String toString() {
        return "PreCiphertext{\n" +
                "C0=" + C0 + ",\n" +
                "encryptedMessage='" + encryptedMsg+ '\'' + ",\n" +
                "policy='" + policy + '\'' + "\n}";
    }
    public String toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("C0", Base64.getEncoder().encodeToString(C0.toBytes()));
        map.put("encryptedMsg", encryptedMsg);
        map.put("policy", policy);
        return new Gson().toJson(map);
    }
    public static PreCiphertext fromJson(String json, Pairing pairing) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(json, type);

        Element C0 = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(map.get("C0"))).getImmutable();
        String msg = map.get("encryptedMsg");
        String policy = map.get("policy");

        return new PreCiphertext(C0, msg, policy);
    }



}
