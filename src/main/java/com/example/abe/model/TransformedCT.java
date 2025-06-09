package com.example.abe.model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Base64;
import java.util.Map;

public class TransformedCT {

    public final Element transformedC1;
    public final String encMsg ;

    public TransformedCT(Element transformedC1 , String encMsg) {
        this.transformedC1 = transformedC1 ;
        this.encMsg = encMsg ;
    }

    public String toJson() {
        Map<String, String> jsonMap = Map.of(
                "transformedC1", Base64.getEncoder().encodeToString(transformedC1.toBytes()),
                "encMsg", encMsg
        );
        return new Gson().toJson(jsonMap);
    }
    public static TransformedCT fromJson(String json, Pairing pairing) {
        Gson gson = new Gson();
        Map<String, String> data = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());

        String encodedC1 = data.get("transformedC1");
        Element transformedC1 = pairing.getGT().newElementFromBytes(Base64.getDecoder().decode(encodedC1)).getImmutable();

        String encMsg = data.get("encMsg");
        return new TransformedCT(transformedC1, encMsg);
    }
    @Override
    public String toString() {
        return "TransformedCT{\n" +
                "transformedC1=" + transformedC1 + ",\n" +
                "encmsg='" + encMsg + "'\n}";
    }}