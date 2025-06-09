package com.example.abe.crypto;

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrElement;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TA {
    private Element g, alpha, e_gg_alpha, beta;
    private Pairing pairing;
    private Element h ; 

    public static void main(String[] args) {
       TA ta = new TA(); 
       ta.setup();
      
    }
    public void setup() {
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        PairingFactory.getInstance().setReuseInstance(false);
        pairing = PairingFactory.getPairing("params/a.properties");

        g = pairing.getG1().newRandomElement().getImmutable();
        alpha = pairing.getZr().newRandomElement().getImmutable();
        e_gg_alpha = pairing.pairing(g, g).powZn(alpha).getImmutable();
        beta = pairing.getZr().newRandomElement().getImmutable();
        h= g.powZn(beta);
        PublicKey pb = new PublicKey(h, e_gg_alpha) ; 
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("pb.key"))) {
            out.writeObject(pb);
        System.out.println("Clé publique stockée dans pb.key");
        } catch (Exception e) {
            e.printStackTrace();
        }
         System.out.println(h);

        System.out.println("TA setup terminé");
    }

    public UserKeys keygen(String userId, Set<String> attributes,  Set<String> attrsUniverse) {
        Field Zp = pairing.getZr();
        Field G1 = pairing.getG1();
         Element s_i ; 
        do {
            s_i = Zp.newRandomElement().getImmutable();
        } while (s_i.isZero());
       
            Element z_i;
        do {
            z_i = Zp.newRandomElement().getImmutable();
        } while (z_i.isZero());

       
        Map<String,Element[]> ukMap = this.keygenUp(attrsUniverse,s_i)   ;   
        TK tk = this.keygenTk(attributes, z_i);
         return new UserKeys(s_i, z_i, tk, ukMap);
    }
    public TK keygenTk(Set<String> attributes,Element z_i) {
        Field Zp = pairing.getZr();
        Field G1 = pairing.getG1();
        Element r_i = Zp.newRandomElement().getImmutable();
        Element betaz_i= beta.mul(z_i).getImmutable();
        Element inv_betaz_i= betaz_i.invert().getImmutable();

        Element alphar_i= alpha.add(r_i).getImmutable(); 
        Element D_exp= alphar_i.mul(inv_betaz_i).getImmutable();

        Element D = g.powZn(D_exp).getImmutable();
         Map<String,Element[]> tkMap = new HashMap<>();
         for (String attr : attributes) {
            Element r_j= Zp.newRandomElement().getImmutable();
            Element inv_z_i = z_i.invert().getImmutable();
            Element h1_exp= r_j.mul(inv_z_i).getImmutable();
            Element h1_u = H1(attr, G1).getImmutable();
            Element Dj_part2= h1_u.powZn(h1_exp).getImmutable();
            Element ri_invzi= r_i.mul(inv_z_i).getImmutable(); 
            Element DJ_part1 = g.powZn(ri_invzi).getImmutable();
            Element Dj = DJ_part1.mul(Dj_part2).getImmutable(); 
            Element Dj_prime = g.powZn(h1_exp).getImmutable(); 
            
           tkMap.put(attr, new Element[]{Dj, Dj_prime});

         
        }
        return new TK(D, tkMap); 

    }

    

    public Map<String, Element[]> keygenUp(Set<String> attrsUnivers, Element s_i) {
         Field Zp = pairing.getZr();
        Field G1 = pairing.getG1();

         

         Map<String, Element[]> ukMap = new HashMap<>();
            for (String attr : attrsUnivers) {
            Element h2_us = H2(attr + s_i.toString(), Zp);
            Element h1_u = H1(attr, G1);
            Element inv_h2_us = h2_us.invert();

            

            Element up1 = g.powZn(inv_h2_us).getImmutable();
            Element up2 = h1_u.powZn(inv_h2_us).getImmutable();

            ukMap.put(attr, new Element[]{up1, up2});
        }
        return ukMap;


    }
    private Element H2(String input, Field zp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return zp.newElement().setFromHash(hash, 0, hash.length).getImmutable();
        } catch (Exception e) {
            throw new RuntimeException("Erreur dans H2 : " + e.getMessage());
        }
    }
//a faire
    private Element H1(String input, Field g1) {
        return g1.newElement().setFromHash(input.getBytes(), 0, input.length()).getImmutable();
    }

    public Pairing getPairing() { return this.pairing; }
    public Element getG() { return this.g; }
    public Element getAlpha() { return this.alpha; }
    public Element getE_gg_alpha() { return this.e_gg_alpha; }
}
