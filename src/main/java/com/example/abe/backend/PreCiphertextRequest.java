package com.example.abe.backend;

import java.util.List;

public class PreCiphertextRequest {
    public String message;
    public String policy;
    public List<String> userAttrs;



    @Override
public String toString() {
    return "PreCiphertextRequest{" +
            "message='" + message + '\'' +
            ", policy='" + policy + '\'' +
            ", userAttrs=" + userAttrs +
            '}';
}


    public String getMessage() {
        return message;
    }
    public String getPolicy() {
        return policy;
    }
    public List<String> getUserAttrs() {
        return userAttrs;
    }
    public void setMessage(String message) {
        this.message = message;
    }public void setPolicy(String policy) {
        this.policy = policy;
    }public void setUserAttrs(List<String> userAttrs) {
        this.userAttrs = userAttrs;
    }
    
    
}
