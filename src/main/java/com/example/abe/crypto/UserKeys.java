package com.example.abe.crypto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.lang.reflect.Type;
import java.util.*;

public class UserKeys {

    public final Element EK ;
    public final Element DK ;
    public final Map<String, Element[]> TK;
    public final Map<String,Element[]> UK ;

    public UserKeys(Element ek , Element dk , Map<String,Element[]> tk, Map<String,Element[]> uk) {
        this.EK= ek;
        this.DK = dk ;
        this.TK = tk ;
        this.UK = uk ;

    }
    public String toJson() {
        Gson gson = new Gson();
        Map<String, String> data = new HashMap<>();

        data.put("EK", Base64.getEncoder().encodeToString(EK.toBytes()));
        data.put("DK", Base64.getEncoder().encodeToString(DK.toBytes()));

        // Encode UK et TK
        data.put("UK", gson.toJson(encodeMapOfElements(UK)));
        data.put("TK", gson.toJson(encodeMapOfElements(TK)));

        return gson.toJson(data);
    }

    private Map<String, List<String>> encodeMapOfElements(Map<String, Element[]> map) {
        Map<String, List<String>> encoded = new HashMap<>();
        for (Map.Entry<String, Element[]> entry : map.entrySet()) {
            List<String> encodedArray = new ArrayList<>();
            for (Element e : entry.getValue()) {
                encodedArray.add(Base64.getEncoder().encodeToString(e.toBytes()));
            }
            encoded.put(entry.getKey(), encodedArray);
        }
        return encoded;
    }

    public static UserKeys fromJson(String json, Pairing pairing) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> raw = gson.fromJson(json, mapType);

        Element ek = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(raw.get("EK"))).getImmutable();
        Element dk = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(raw.get("DK"))).getImmutable();

        Type mapArrayType = new TypeToken<Map<String, List<String>>>() {}.getType();
        Map<String, List<String>> ukRaw = gson.fromJson(raw.get("UK"), mapArrayType);
        Map<String, List<String>> tkRaw = gson.fromJson(raw.get("TK"), mapArrayType);

        Map<String, Element[]> uk = decodeMapOfElements(ukRaw, pairing);
        Map<String, Element[]> tk = decodeMapOfElements(tkRaw, pairing);

           return new UserKeys(ek, dk, uk, tk);
    }

    private static Map<String, Element[]> decodeMapOfElements(Map<String, List<String>> raw, Pairing pairing) {
        Map<String, Element[]> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : raw.entrySet()) {
            List<Element> elements = new ArrayList<>();
            for (String b64 : entry.getValue()) {
                Element e = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(b64)).getImmutable();
                elements.add(e);
            }
            result.put(entry.getKey(), elements.toArray(new Element[0]));
        }
        return result;
    }


}
