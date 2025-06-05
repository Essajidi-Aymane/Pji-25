package com.example.abe.backend;

import com.example.abe.model.CipherText;
import com.example.abe.model.PreCiphertext;
import com.example.abe.model.TransformedCT;
import com.example.abe.server.ServerDecryptor;
import com.example.abe.server.ServerEncryptor;
import com.example.abe.util.JsonUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AbeController {
    private final Pairing pairing ;
    private final ServerEncryptor servEncryptor ;
    private final ServerDecryptor servDecryptor ;
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
}
