package com.example.abe.parser;

public class AccessTreeNode {
    public String attr ;
    public String operator ;
    public AccessTreeNode left ;
    public AccessTreeNode right ;
    public boolean isLeaf() {
        return operator== null ;
    }
}
