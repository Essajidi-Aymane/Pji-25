package com.example.abe.server;

import com.example.abe.model.CipherText;
import com.example.abe.parser.AccessPolicyParser;
import com.example.abe.parser.AccessTreeNode;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import com.example.abe.model.TransformedCT;

import java.util.Set;

public class ServerDecryptor {

    private final Pairing pairing ;

    public ServerDecryptor(Pairing p) {
        this.pairing = p ;

    }

    public TransformedCT outDecrypt(CipherText ct, Element[] TK, Set<String> userAttrs) {
        AccessTreeNode policyTree = AccessPolicyParser.parse(ct.policy);


        if (!isSatifiedField(policyTree, userAttrs)) {
            System.out.println("Accès refusé : attributs insuffisants");
            return null;
        }
        Element g1_TK0 = ct.C0.getField().newElement().set(TK[0]).getImmutable();
        Element transformed = pairing.pairing(ct.C0, g1_TK0).getImmutable();
        System.out.println("C0 field: " + ct.C0.getField().getClass());
        System.out.println("TK[0] field: " + TK[0].getField().getClass());
        return new TransformedCT(transformed, ct.encMsg);
    }
    public static boolean isSatifiedField(AccessTreeNode node , Set<String> attrs){
        if(node.isLeaf()) return attrs.contains(node.attr) ;
        boolean left = isSatifiedField(node.left, attrs);
        boolean right = isSatifiedField(node.right,attrs);
        return  node.operator.equals(("AND"))?(left && right): (left||right);
    }

}
