package com.example.abe.backend;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DecryptRequest {
    private String cipherTextJson;
    private Map<String, List<String>> tkJson;
    private Set<String> attrs; // attributs
    
    
    @JsonProperty("D")
    private String D; 

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
       return D;
   }
   public void setD(String D) {
       this.D = D;
   }
}
