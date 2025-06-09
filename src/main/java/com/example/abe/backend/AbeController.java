package com.example.abe.backend;

import com.example.abe.client.*;
import com.example.abe.crypto.*;
import com.example.abe.model.*;
import com.example.abe.server.*;
import com.example.abe.util.JsonUtil;
import com.google.gson.Gson;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

import java.util.*;

@RestController
@RequestMapping("/api")
public class AbeController {


    private final Pairing pairing;
    private final TA ta = new TA();              
    private final ServerEncryptor servEnc;
    private final ServerDecryptor servDec;

    private final Set<String> universe = new HashSet<>();

    public AbeController() {
        try (InputStream in = getClass().getClassLoader()
                                       .getResourceAsStream("params/a.properties")) {
            if (in == null) throw new RuntimeException("params/a.properties manquant");
            PropertiesParameters p = new PropertiesParameters(); p.load(in);
            this.pairing = PairingFactory.getPairing(p);
        } catch (Exception e) {
            throw new RuntimeException("Pairing init KO", e);
        }
        this.servEnc = new ServerEncryptor(pairing);
        this.servDec = new ServerDecryptor(pairing);
    }

    private static final Gson GSON = new Gson();

   

    @PostMapping("/setup")
    public String setup() {
        ta.setup();         
        return " Système ABE initialisé";
    }


    @PostMapping("/attrs")
    public String defineAttrs(@RequestBody Set<String> attrs) {
        universe.clear();
        universe.addAll(attrs);
        return "Attributs enregistrés : " + universe;
    }

    @GetMapping("/attrs")
    public Set<String> listAttrs() { return universe; }



    @PostMapping("/keygen")
    public Map<String,Object> keygen(@RequestBody KeygenRequest req) {

        if (req.getAttrs()==null || req.getAttrs().isEmpty())
            throw new IllegalArgumentException("attrs vide");

        UserKeys keys = ta.keygen(req.getUserId(), req.getAttrs(), universe);

        Map<String,Object> out = new HashMap<>();
        out.put("EK", b64(keys.EK));
        out.put("DK", b64(keys.DK));
        out.put("D" , b64(keys.TK.getD()));
        out.put("UK", JsonUtil.encodeElementArrayMap(keys.UK));
        out.put("TK", JsonUtil.encodeElementArrayMap(keys.TK.getTkMap()));
        return out;
    }


    public record ClientEncReq(String message,
                               String policy,
                               String ek,
                               Map<String,List<String>> uk) {}

    @PostMapping("/client/encrypt")
    public String clientEncrypt(@RequestBody ClientEncryptRequest req) {

        Element ek = fromB64G1(req.getEk());
        Map<String,Element[]> uk = JsonUtil.decodeElementArrayMap(req.getUk(), pairing);

        ClientEncryptor cli = new ClientEncryptor(pairing);
        PreCiphertext pre   = cli.preEncrypt(req.getMessage(), req.getPolicy(), ek);

        return pre.toJson();          
    }

    
public record EncryptReq(String preCt,
                         Map<String,List<String>> ukJson) {}

   
@PostMapping("/encrypt")
public String serverEncrypt(@RequestBody EncryptRequest req) {

    PreCiphertext pre = PreCiphertext.fromJson(req.getPreCt(), pairing);

    Map<String, Element[]> uk = JsonUtil.decodeElementArrayMap(req.getUkJson(), pairing);

    CipherText ct = servEnc.outEncrypt(pre, uk);
    return ct.toJson();
}



@PostMapping("/decrypt")
public String serverDecrypt(@RequestBody DecryptRequest req) {

    CipherText ct = CipherText.fromJson(req.getCipherTextJson(), pairing);

    Map<String, Element[]> tkMap = JsonUtil.decodeElementArrayMap(req.getTkJson(), pairing);
    Element D = fromB64G1(req.getD());  
    TK tk = new TK(D, tkMap);
    TransformedCT tct = servDec.outDecrypt(ct, tk, req.getAttrs());

    return tct.toJson();
}



    @PostMapping("/client/decrypt")
    public String clientDecrypt(@RequestBody ClientDecryptRequest req) {

        TransformedCT tct = TransformedCT.fromJson(req.getTransformedCt(), pairing);
        Element dk = pairing.getZr()
                .newElementFromBytes(Base64.getDecoder().decode(req.getDk()))
                .getImmutable();

        ClientDecryptor clDec = new ClientDecryptor(pairing);
        return clDec.finalDecrypt(tct, dk);   
    }


    private String b64(Element e) {
        return Base64.getEncoder().encodeToString(e.toBytes());
    }
    private Element fromB64G1(String b64) {
        return pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(b64))
                      .getImmutable();
    }
}
