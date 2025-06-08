package com.example.abe.model;

import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import com.google.gson.Gson;
import it.unisa.dia.gas.jpbc.Pairing;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
public  class CipherText {


    public  final Element C0 ;
    public final Element C1 ;
    public final String encMsg ;
    public final Map<String, Element> policyComponents ;
    public final String policy;

    public CipherText(Element c0 ,Element c1 , String encMsg ,  Map<String, Element> policyComponents,String  policy) {
    this.C0 = c0 ;
    this.C1 = c1 ;
    this.encMsg = encMsg ;
    this.policyComponents = policyComponents  ;
    this.policy = policy ;
    }

    @Override
    public String toString() {
        return "CipherText{" +
                "\nC0=" + C0 +
                ",\nC1=" + C1 +
                ",\nencMsg='" + encMsg + '\'' +
                ",\npolicyComponents=" + policyComponents +
                "\n}";
    }


    public String toJson() {
        Map<String, String> data = new HashMap<>();
        data.put("C0", Base64.getEncoder().encodeToString(C0.toBytes()));
        data.put("C1", Base64.getEncoder().encodeToString(C1.toBytes()));
        data.put("encMsg", encMsg);
        data.put("policy", policy);

        Map<String, String> policyMap = new HashMap<>();
        for (Map.Entry<String, Element> entry : policyComponents.entrySet()) {
            policyMap.put(entry.getKey(), Base64.getEncoder().encodeToString(entry.getValue().toBytes()));
        }
        Gson gson = new Gson();
        data.put("policyComponents", gson.toJson(policyMap));

        return gson.toJson(data);
    }
      
public static CipherText fromJson(String json, Pairing pairing) {
    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
    Map<String, Object> data = gson.fromJson(json, mapType);

    Element C0 = pairing.getG1().newElementFromBytes(
        Base64.getDecoder().decode((String) data.get("C0"))).getImmutable();

    Element C1 = pairing.getGT().newElementFromBytes(
        Base64.getDecoder().decode((String) data.get("C1"))).getImmutable();

    String encMsg = (String) data.get("encMsg");
    String policy = (String) data.get("policy");

    Object pcObj = data.get("policyComponents");

    Map<String, String> rawComponents;

    if (pcObj instanceof String str) {
        rawComponents = gson.fromJson(str, new TypeToken<Map<String, String>>(){}.getType());
    } else {
        rawComponents = gson.fromJson(gson.toJson(pcObj), new TypeToken<Map<String, String>>(){}.getType());
    }

    Map<String, Element> components = new HashMap<>();
    for (Map.Entry<String, String> entry : rawComponents.entrySet()) {
        Element e = pairing.getG1()
            .newElementFromBytes(Base64.getDecoder().decode(entry.getValue()))
            .getImmutable();
        components.put(entry.getKey(), e);
    }

    return new CipherText(C0, C1, encMsg, components, policy);
}

    

}
