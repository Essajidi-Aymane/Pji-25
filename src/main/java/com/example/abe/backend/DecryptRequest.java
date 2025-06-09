package com.example.abe.backend;

import java.util.List;
import java.util.Map;
import java.util.Set;



public class DecryptRequest {
    private String cipherTextJson;
    private Map<String, List<String>> tkJson;
    private Set<String> attrs; // attributs
    private String d; 

    public Set<String> getAttrs() {
        return attrs;
    }

    public String getCipherTextJson() {
        return cipherTextJson;
    }

   public Map<String, List<String>> getTkJson() {
       return tkJson;
   }

    public void setAttrs(Set<String> attrs) {
        this.attrs = attrs;
    }

    public void setCipherTextJson(String cipherTextJson) {
        this.cipherTextJson = cipherTextJson;
    }

    public void setTkJson(Map<String, List<String>> tkJson) {
        this.tkJson = tkJson;
    }

   public String getD() {
       return d;
   }
}
