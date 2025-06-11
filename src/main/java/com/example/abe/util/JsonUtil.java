package com.example.abe.util;

import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.google.gson.Gson;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.*;

public class JsonUtil {

public static Element[] decodeElementArray(List<String> list, Pairing pairing) {
    Element[] result = new Element[list.size()];
    for (int i = 0; i < list.size(); i++) {
        result[i] = pairing.getG1()
            .newElementFromBytes(Base64.getDecoder().decode(list.get(i)))
            .getImmutable();
    }
    return result;
}

    public static String encodeElementArrayMap(Map<String, Element[]> map) {
        Map<String, List<String>> base64Map = new HashMap<>();
        for (Map.Entry<String, Element[]> entry : map.entrySet()) {
            List<String> list = new ArrayList<>();
            for (Element e : entry.getValue()) {
                list.add(Base64.getEncoder().encodeToString(e.duplicate().toBytes()));
            }
            base64Map.put(entry.getKey(), list);
        }
        return new Gson().toJson(base64Map);
    }
    public static String encodeElementArray(Element[] arr) {
        List<String> list = new ArrayList<>();
        for (Element e : arr) {
            list.add(Base64.getEncoder().encodeToString(e.toBytes()));
        }
        return new Gson().toJson(list);
    }
    public static String wrapDecryptRequest(CipherText ct, String tkJson, Set<String> attrs) {
        Map<String, Object> map = new HashMap<>();
        map.put("cipherTextJson", ct.toJson());
        map.put("tkJson", tkJson);
        map.put("attrs", attrs);
        return new Gson().toJson(map);
    }
    public static String wrapEncryptRequest(PreCiphertext preCT, Map<String, Element[]> uk) {
        Map<String, Object> map = new HashMap<>();
        map.put("preCt", preCT.toJson());
        map.put("ukJson", encodeElementArrayMap(uk));
        return new Gson().toJson(map);
    }
    public static Map<String, Element[]> decodeElementArrayMap(Map<String, List<String>> input, Pairing pairing) {
    Map<String, Element[]> result = new HashMap<>();

    for (Map.Entry<String, List<String>> entry : input.entrySet()) {
        String attr = entry.getKey();
        List<String> encodedElements = entry.getValue();

        // Validation structurelle
        if (encodedElements == null || encodedElements.size() < 2) {
            System.err.println(" Clé manquante ou incomplète pour l'attribut : " + attr);
            throw new IllegalArgumentException("Clé incomplète pour l'attribut : " + attr);
        }

        Element[] elements = new Element[encodedElements.size()];
        for (int i = 0; i < encodedElements.size(); i++) {
            try {
                String b64 = encodedElements.get(i);
                if (b64 == null || b64.isBlank()) {
                    System.err.println(" Chaîne base64 vide pour l'attribut : " + attr + " à l'index " + i);
                    throw new IllegalArgumentException("Chaîne vide dans UK pour : " + attr);
                }
                System.out.println("🔍 Début décodage de " + attr + "[" + i + "]");
                System.out.println("    Base64 = " + b64);


                
                elements[i] = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(b64)).getImmutable();
                System.out.println("    Bytes → Element OK = " + (elements[i] != null));


                if (elements[i] == null) {
    System.err.println(" newElementFromBytes a retourné NULL pour " + attr + "[" + i + "]");
    throw new IllegalArgumentException("Reconstruction échouée pour " + attr);
}


            } catch (Exception e) {
                System.err.println(" Erreur de décodage base64 pour l'attribut : " + attr + " à l'index " + i);
                e.printStackTrace();
                throw new RuntimeException("Erreur de décodage UK[" + attr + "][" + i + "]", e);
            }
        }

        result.put(attr, elements);
    }

    return result;
}


}
