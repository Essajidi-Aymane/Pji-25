package com.example.abe.server;

import com.example.abe.crypto.TK;
import com.example.abe.model.CipherText;
import com.example.abe.model.TransformedCT;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Map;
import java.util.Set;

public class ServerDecryptor {

    private final Pairing pairing;

    public ServerDecryptor(Pairing p) {
        this.pairing = p;
    }

   
    public TransformedCT outDecrypt(CipherText ct, TK tk, Set<String> attrs) {

        if (!isSatisfied(ct.policy, attrs)) {
            System.out.println(" Accès refusé : attributs insuffisants.");
            return null;
        }
        Element A = recurse(ct.policy, tk.getTkMap(), attrs);
System.out.println("c0 : " + ct.C0.getField());
System.out.println("tk : " + tk.getD().getField());

        Element top = pairing.pairing(ct.C0, tk.getD()).getImmutable();
        Element transCT = top.div(A).getImmutable();

        return new TransformedCT(transCT, ct.encMsg);
    }

    private Element recurse(AccessTreeNode node,
                            Map<String, Element[]> tkMap,
                            Set<String> attrs) {

        // Feuille
        if (node.isLeaf()) {
            if (!attrs.contains(node.attr)) return null;   

            Element[] dk = tkMap.get(node.attr);          
            if (dk == null) throw new IllegalStateException("DK manquant pour " + node.attr);

               
            Element e1 = pairing.pairing(dk[0], node.C).getImmutable();
            Element e2 = pairing.pairing(dk[1], node.C_prime).getImmutable();
            Element ratio = e1.div(e2).getImmutable();


            return ratio;
        }

       
        Element left  = recurse(node.left , tkMap, attrs);
        Element right = recurse(node.right, tkMap, attrs);

        if ("AND".equalsIgnoreCase(node.operator)) {
            if (left == null || right == null) return null; 
            
            Element leftPart = left.powZn(weightForX(node.left.xIndex)).getImmutable();
            Element rightPart = right.powZn(weightForX(node.right.xIndex)).getImmutable();
            return leftPart.mul(rightPart).getImmutable();          
        } else { 
            return (left != null) ? left : right;             
        }
    }

    private Element weightForX(int x) {
        switch (x) {
            case 1: return pairing.getZr().newElement(2).getImmutable();          
            case 2: return pairing.getZr().newElement(1).negate().getImmutable(); 
            default: throw new IllegalStateException("xIndex doit valoir 1 ou 2");
        }
    }

    private boolean isSatisfied(AccessTreeNode n, Set<String> attrs) {
        if (n.isLeaf()) return attrs.contains(n.attr);
        boolean l = isSatisfied(n.left , attrs);
        boolean r = isSatisfied(n.right, attrs);
        return "AND".equalsIgnoreCase(n.operator) ? (l && r) : (l || r);
    }
}
