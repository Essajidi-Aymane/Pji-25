package com.example.abe.backend;


import java.util.Set;

public class KeygenRequest {

public String userId ; 
public Set<String> attrs ; 


public Set<String> getAttrs() {
    return attrs;
}
public String getUserId() {
    return userId;
}
public void setAttrs(Set<String> attrs) {
    this.attrs = attrs;
}
public void setUserId(String userId) {
    this.userId = userId;
}


}