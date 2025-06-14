    package com.example.abe.crypto;

    import java.lang.reflect.Type;
    import java.util.ArrayList;
    import java.util.Base64;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;
    import com.google.gson.Gson;


    import it.unisa.dia.gas.jpbc.Element;
    import it.unisa.dia.gas.jpbc.Pairing;
    public class TK {
        

        private  Element D ; 
        private  Map<String, Element[]> tkMap ; 


        public TK(Element D ,  Map<String, Element[]> tkMap ) {
            this.D=D; 
            this.tkMap = tkMap;
        }
        public TK() { 
            
        }
        public Element getD() {
            return D;
        }
        public Map<String, Element[]> getTkMap() {
            return tkMap;
        }
        public String toJson() {
            Map<String,String> map = new HashMap<>(); 
            Gson gson = new Gson();
            map.put("D", Base64.getEncoder().encodeToString(D.toBytes()));
            map.put("tkmap", gson.toJson(encodeMapOfElements(tkMap)));

            return gson.toJson(map); 
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
        public  void fromJson(String json,Pairing pairing){
            Gson gson = new Gson(); 
                Type mapType = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> raw = gson.fromJson(json, mapType);

                Element d = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(raw.get("D"))).getImmutable();
                        Type mapArrayType = new TypeToken<Map<String, List<String>>>() {}.getType();

                Map<String, List<String>> tkRaw = gson.fromJson(raw.get("tkmap"), mapArrayType);

                Map<String, Element[]> tkMap = decodeMapOfElements(tkRaw, pairing);
                this.D=d; 
                this.tkMap= tkMap;


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
