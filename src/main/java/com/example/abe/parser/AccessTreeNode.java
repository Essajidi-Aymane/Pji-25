package com.example.abe.parser;

import it.unisa.dia.gas.jpbc.Element;

public class AccessTreeNode {
    public String attr;
    public String operator; 
    public AccessTreeNode left;
    public AccessTreeNode right;

    public Element lambda; 
    public Element preC;      
    public Element C; 
    public Element C_prime; 
    public int     xIndex;  

    public boolean isLeaf() {
        return operator == null;
    }
}

