package com.example.abe.backend;

import java.util.List;
import java.util.Map;

public class EncryptRequest {
    private String preCt;

    public Map<String, List<String>> ukJson;

    public String getPreCt() {
        return  preCt;
    }
    public Map<String, List<String>> getUkJson() {
    return ukJson;
}

    public void setPreCt(String preCt) {
        this.preCt = preCt;
    }

  public void setUkJson(Map<String, List<String>> ukJson) {
      this.ukJson = ukJson;
  }
}
