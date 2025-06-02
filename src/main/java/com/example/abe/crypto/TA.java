package com.example.abe.crypto;

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TA {

        private Element g , alpha ;
        private Element e_gg_alpha;
        private Pairing pairing ;
        private Element beta ;

        public void setup() {

            PairingFactory.getInstance().setUsePBCWhenPossible(true);
            PairingFactory.getInstance().setReuseInstance(false);

            pairing = PairingFactory.getPairing("params/a.properties") ;

            g = pairing.getG1().newRandomElement().getImmutable() ;

            alpha = pairing.getZr().newRandomElement().getImmutable() ;
            e_gg_alpha= pairing.pairing(g,g).powZn(alpha).getImmutable();
            beta = pairing.getZr().newRandomElement().getImmutable();

            System.out.println("TA setup terminé");
        }

    public UserKeys keygen(String userId, Set<String> attributes) {
        Field Zp = pairing.getZr();
        Field G1 = pairing.getG1();

        Element s_i = Zp.newRandomElement().getImmutable();
        Element z_i = Zp.newRandomElement().getImmutable();

        Map<String, Element[]> tkMap = new HashMap<>();
        Map<String, Element[]> ukMap = new HashMap<>();

        for (String attr : attributes) {

            Element h2_u = H2(attr, Zp);
            Element h2_us = H2(attr + s_i.toString(), Zp);
            Element h1_u = H1(attr, G1);

            Element inv_h2_us = h2_us.invert();

            Element tk1 = g.powZn(h2_u.mul(inv_h2_us)).getImmutable();
            Element tk2 = H1(attr, G1).powZn(inv_h2_us).getImmutable();


            Element up1 = g.powZn(inv_h2_us).getImmutable();
            Element up2 = h1_u.powZn(inv_h2_us).getImmutable();

            tkMap.put(attr, new Element[]{tk1, tk2});
            ukMap.put(attr, new Element[]{up1, up2});
        }

        return new UserKeys(s_i, z_i, tkMap, ukMap);
    }


    private Element H2(String input , Field zp) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes());
                return zp.newElement().setFromHash(hash,0,hash.length).getImmutable() ;
            }catch (Exception e) {throw new RuntimeException("Erreur dans H2 : " + e.getMessage() );
            }
    }
    private Element H1(String input ,  Field g1) {
     return g1.newElement().setFromHash(input.getBytes(), 0 , input.getBytes().length).getImmutable() ;
        }


    public Pairing getPairing() {return this.pairing;}
    public Element getG() {return this.g;}
    public  Element getAlpha() {return this.alpha;}
    public Element getE_gg_alpha(){ return  this.e_gg_alpha;}



}
