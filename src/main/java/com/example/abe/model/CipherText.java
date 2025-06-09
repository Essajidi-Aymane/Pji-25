package com.example.abe.model;

import com.example.abe.parser.AccessTreeNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CipherText  {

    public final Element C0;
    public final String encMsg;
    public final AccessTreeNode policy;

    public CipherText(Element c0, String encMsg, AccessTreeNode policy) {
        this.C0 = c0;
        this.encMsg = encMsg;
        this.policy = policy;
    }

    @Override
    public String toString() {
        return "CipherText{" +
                "\nC0=" + C0 +
                ",\nencMsg='" + encMsg + '\'' +
                ",\npolicy=" + new Gson().toJson(policy) +
                "\n}";
    }

    public String toJson() {
        Gson gson = new Gson();
        Map<String, String> data = new HashMap<>();
        data.put("C0", Base64.getEncoder().encodeToString(C0.toBytes()));
        data.put("encMsg", encMsg);
        data.put("policy", gson.toJson(policy));
        return gson.toJson(data);
    }

    public static CipherText fromJson(String json, Pairing pairing) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(json, mapType);

        Element C0 = pairing.getG1()
                .newElementFromBytes(Base64.getDecoder().decode(data.get("C0")))
                .getImmutable();

        String encMsg = data.get("encMsg");

        AccessTreeNode policy = gson.fromJson(data.get("policy"), AccessTreeNode.class);

        return new CipherText(C0, encMsg, policy);
    }
}
