package com.example.abe.backend;

import java.util.List;
import java.util.Map;

public class ClientEncryptRequest {
    private String message;
    private String policy;
    private String ek; 
    private Map<String, List<String>> uk; 

    public String getMessage() { return message; }
    public String getPolicy() { return policy; }
    public String getEk() { return ek; }
    public Map<String, List<String>> getUk() { return uk; }

    public void setMessage(String message) { this.message = message; }
    public void setPolicy(String policy) { this.policy = policy; }
    public void setEk(String ek) { this.ek = ek; }
    public void setUk(Map<String, List<String>> uk) { this.uk = uk; }
}
