package com.example.abe.backend;

public class ClientDecryptRequest {

    private String transformedCt; 
    private String dk;             

    public ClientDecryptRequest() {
    
    }

    public String getTransformedCt() {
        return transformedCt;
    }

    public void setTransformedCt(String transformedCt) {
        this.transformedCt = transformedCt;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }
}
