package com.example.abe.backend;

import java.util.List;

public class DecryptRequest {
    private String cipherTextJson;
    public  List<String> tkJson;
    private List<String> attrs; // attributs

    public List<String> getAttrs() {
        return attrs;
    }

    public String getCipherTextJson() {
        return cipherTextJson;
    }

    public List<String> getTkJson() {
        return tkJson;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }

    public void setCipherTextJson(String cipherTextJson) {
        this.cipherTextJson = cipherTextJson;
    }

    public void setTkJson(List<String> tkJson) {
        this.tkJson = tkJson;
    }
}
