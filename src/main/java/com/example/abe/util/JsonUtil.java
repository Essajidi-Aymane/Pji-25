package com.example.abe.util;

import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.lang.reflect.Type;
import java.util.*;

public class JsonUtil {
    public static Element[] decodeElementArray(String json, Pairing pairing) {
        Gson gson = new Gson();
        String[] base64Array = gson.fromJson(json, String[].class);
        Element[] result = new Element[base64Array.length];
        for (int i = 0; i < base64Array.length; i++) {
            result[i] = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(base64Array[i])).getImmutable();
        }
        return result;
    }
    public static String encodeElementArrayMap(Map<String, Element[]> map) {
        Map<String, List<String>> base64Map = new HashMap<>();
        for (Map.Entry<String, Element[]> entry : map.entrySet()) {
            List<String> list = new ArrayList<>();
            for (Element e : entry.getValue()) {
                list.add(Base64.getEncoder().encodeToString(e.toBytes()));
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
    public static Map<String, Element[]> decodeElementArrayMap(String json, Pairing pairing) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
        Map<String, List<String>> raw = gson.fromJson(json, type);
        Map<String, Element[]> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : raw.entrySet()) {
            Element[] arr = new Element[entry.getValue().size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(entry.getValue().get(i))).getImmutable();
            }
            result.put(entry.getKey(), arr);
        }
        return result;
    }
}
