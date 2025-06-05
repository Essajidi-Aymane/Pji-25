package com.example.abe.parser;

public class AccessPolicyParser {

    public static AccessTreeNode parse(String expr) {
        expr = expr.trim();
        if (expr.startsWith("(") && expr.endsWith(")")) {
            expr = expr.substring(1, expr.length() - 1).trim();
        }
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            if (c == ')') depth--;
            if (depth == 0) {
                if (expr.substring(i).startsWith(" AND ")) {
                    return createOperatorNode("AND", expr.substring(0, i), expr.substring(i + 5));
                } else if (expr.substring(i).startsWith(" OR ")) {
                    return createOperatorNode("OR", expr.substring(0, i), expr.substring(i + 4));
                }
            }
        }

        AccessTreeNode leaf = new AccessTreeNode();
        leaf.attr = expr.trim();
        return leaf;
    }

    private static AccessTreeNode createOperatorNode(String op, String leftExpr, String rightExpr) {
        AccessTreeNode node = new AccessTreeNode();
        node.operator = op;
        node.left = parse(leftExpr);
        node.right = parse(rightExpr);
        return node;
    }
}
