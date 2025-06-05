package com.example.abe.backend;

public class EncryptRequest {
    private String preCt;
    private String ukJson ;

    public String getPreCt() {
        return  preCt;
    }
    public String getUkJson(){
        return ukJson;
    }

    public void setPreCt(String preCt) {
        this.preCt = preCt;
    }

    public void setUkJson(String ukJson) {
        this.ukJson = ukJson;
    }
}
