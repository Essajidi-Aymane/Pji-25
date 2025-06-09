
package com.example.abe.backend;

import com.example.abe.client.ClientDecryptor;
import com.example.abe.client.ClientEncryptor;
import com.example.abe.crypto.TA;
import com.example.abe.crypto.UserKeys;
import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.example.abe.model.TransformedCT;
import com.example.abe.server.ServerDecryptor;
import com.example.abe.server.ServerEncryptor;
import com.example.abe.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

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
    private final ServerEncryptor servEncryptor;
    private final ServerDecryptor servDecryptor;
    private final TA ta = new TA();
    private final Set<String> attributeUniverse = new HashSet<>();

    public AbeController() {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("params/a.properties");
            if (in == null) throw new IllegalArgumentException("Fichier params/a.properties introuvable !");
            PropertiesParameters params = new PropertiesParameters();
            params.load(in);
            this.pairing = PairingFactory.getPairing(params);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement du pairing", e);
        }

        this.servEncryptor = new ServerEncryptor(pairing);
        this.servDecryptor = new ServerDecryptor(pairing);
    }

    @PostMapping("/setup")
    public String setup() {
        try {
            ta.setup();
            return "ABE system initialized.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Setup failed: " + e.getMessage();
        }
    }

    @PostMapping("/attrs")
    public String defineAttributes(@RequestBody Set<String> attrs) {
        attributeUniverse.clear();
        attributeUniverse.addAll(attrs);
        return "Attributes defined: " + attributeUniverse;
    }

    @GetMapping("/attrs")
    public Set<String> getAttributes() {
        return attributeUniverse;
    }

    @PostMapping("/keygen")
    public Map<String, Object> keygen(@RequestBody KeygenRequest req) {
        if (req.attrs == null || req.attrs.isEmpty()) {
            throw new IllegalArgumentException("Liste d'attributs vide.");
        }

        UserKeys keys = ta.keygen(req.userId, new HashSet<>(req.attrs));

        Map<String, List<String>> ukJson = new HashMap<>();
        for (Map.Entry<String, Element[]> entry : keys.UK.entrySet()) {
            List<String> list = Arrays.stream(entry.getValue())
                    .map(e -> Base64.getEncoder().encodeToString(e.toBytes()))
                    .toList();
            ukJson.put(entry.getKey(), list);
        }

        Map<String, List<String>> tkJson = new HashMap<>();
        for (Map.Entry<String, Element[]> entry : keys.TK.entrySet()) {
            List<String> list = Arrays.stream(entry.getValue())
                    .map(e -> Base64.getEncoder().encodeToString(e.toBytes()))
                    .toList();
            tkJson.put(entry.getKey(), list);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("UK", ukJson);
        map.put("TK", tkJson);
        map.put("DK", Base64.getEncoder().encodeToString(keys.DK.toBytes()));
        map.put("EK", Base64.getEncoder().encodeToString(keys.EK.toBytes()));

        return map;
    }

@PostMapping("/client/encrypt")
public String clientEncrypt(@RequestBody Map<String, Object> req) {
    try {
        String message = (String) req.get("message");
        String policy = (String) req.get("policy");
        String ekBase64 = (String) req.get("ek");

        Element ek = pairing.getG1()
                .newElementFromBytes(Base64.getDecoder().decode(ekBase64))
                .getImmutable();

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, List<String>>>() {}.getType();
        Map<String, List<String>> ukRaw = gson.fromJson(gson.toJson(req.get("uk")), mapType);

        Map<String, Element[]> uk = JsonUtil.decodeElementArrayMap(ukRaw, pairing);

        ClientEncryptor encryptor = new ClientEncryptor(pairing);
        PreCiphertext pre = encryptor.preEncrypt(message, policy, ek, uk);
        return pre.toJson();
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur dans /client/encrypt: " + e.getMessage());
    }
}


    @PostMapping("/encrypt")
    public String encrypt(@RequestBody EncryptRequest req) {
        try {
            PreCiphertext pre = PreCiphertext.fromJson(req.getPreCt(), pairing);
            Map<String, Element[]> uk = JsonUtil.decodeElementArrayMap(req.getUkJson(), pairing);
            CipherText ct = servEncryptor.outEncrypt(pre, uk);
            return ct.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur dans /encrypt: " + e.getMessage());
        }
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody DecryptRequest req) {
        try {
            CipherText ct = CipherText.fromJson(req.getCipherTextJson(), pairing);
            Element[] tk = JsonUtil.decodeElementArray(req.getTkJson(), pairing);
            Set<String> attrs = new HashSet<>(req.getAttrs());
            TransformedCT trans = servDecryptor.outDecrypt(ct, tk, attrs);
            return trans.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur dans /decrypt: " + e.getMessage());
        }
    }

    @PostMapping("/client/decrypt")
public String clientDecrypt(@RequestBody Map<String, String> payload) {
    try {
        TransformedCT trans = TransformedCT.fromJson(payload.get("transformedCt"), pairing);
        Element dk = pairing.getZr().newElementFromBytes(
            Base64.getDecoder().decode(payload.get("dk"))
        ).getImmutable();

        ClientDecryptor decryptor = new ClientDecryptor();
        return decryptor.finalDecrypt(trans, dk);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur dans /client/decrypt: " + e.getMessage());
    }
}

}
