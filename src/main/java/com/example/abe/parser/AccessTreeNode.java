package com.example.abe.parser;

import it.unisa.dia.gas.jpbc.Element;

public class AccessTreeNode {
    public String attr;
    public String operator; 
    public AccessTreeNode left;
    public AccessTreeNode right;
    public transient Element lambda; 
    public transient Element preC;      
    public transient Element C; 
    public transient Element C_prime; 
    public transient int xIndex;

    public boolean isLeaf() {
        return operator == null;
    }
}

