package com.example.abe.parser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class AccessPolicyParser {



    public static AccessTreeNode parse(String expr, Element s, Element ek, Field Zp) {
        expr = expr.trim();
        if (expr.startsWith("(") && expr.endsWith(")") && isMatchingParentheses(expr)) {
            expr = expr.substring(1, expr.length() - 1).trim();
        }

        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            if (c == ')') depth--;
            if (depth == 0) {
                if (expr.substring(i).startsWith(" AND ")) {
                    return createOperatorNode("AND", expr.substring(0, i), expr.substring(i + 5), s, ek, Zp);
                } else if (expr.substring(i).startsWith(" OR ")) {
                    return createOperatorNode("OR", expr.substring(0, i), expr.substring(i + 4), s, ek, Zp);
                }
            }
        }

        // Cas feuille
        AccessTreeNode leaf = new AccessTreeNode();
        leaf.attr = expr.trim();
        leaf.lambda = s.getImmutable();
        leaf.preC = H2(leaf.attr + ek.toString(), Zp).mul(leaf.lambda);

        return leaf;
    }
   public String toPolicyString(AccessTreeNode node) {
    if (node.isLeaf()) return node.attr;
    return "(" + toPolicyString(node.left) + " " + node.operator + " " + toPolicyString(node.right) + ")";
}
    private static AccessTreeNode createOperatorNode(String op, String leftExpr, String rightExpr, Element s, Element ek, Field Zp) {
        AccessTreeNode node = new AccessTreeNode();
        node.operator = op;

        if ("OR".equalsIgnoreCase(op)) {
            node.left = parse(leftExpr, s, ek, Zp);
            node.right = parse(rightExpr, s, ek, Zp);
            node.left .xIndex = 1;   
            node.right.xIndex = 2;
        } else if ("AND".equalsIgnoreCase(op)) {
            // f(x) = s + a·x (seuil 2 de Shamir)
            Element a = Zp.newRandomElement().getImmutable();
            Element x1 = Zp.newElement(1);
            Element x2 = Zp.newElement(2);
            Element s1 = a.duplicate().mul(x1).add(s).getImmutable(); // f(1)
            Element s2 = a.duplicate().mul(x2).add(s).getImmutable(); // f(2)

            node.left = parse(leftExpr, s1, ek, Zp);
            node.right = parse(rightExpr, s2, ek, Zp);
             node.left .xIndex = 1;   
             node.right.xIndex = 2;
        }

        return node;
    }

    private static boolean isMatchingParentheses(String expr) {
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            if (depth == 0 && i != expr.length() - 1) return false;
        }
        return depth == 0;
    }

    private static Element H2(String input, Field Zp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Zp.newElement().setFromHash(hash, 0, hash.length).getImmutable();
        } catch (Exception e) {
            throw new RuntimeException("Erreur dans H2 : " + e.getMessage());
        }
    }

    public static List<String> extractAttributes(AccessTreeNode node) {
        List<String> attributes = new ArrayList<>();
        collectAttributes(node, attributes);
        return attributes;
    }

    private static void collectAttributes(AccessTreeNode node, List<String> attributes) {
        if (node == null) return;
        if (node.isLeaf()) {
            attributes.add(node.attr);
        } else {
            collectAttributes(node.left, attributes);
            collectAttributes(node.right, attributes);
        }
    }
}
