package com.example.abe.backend;

import java.util.List;

public class KeygenRequest {

public String userId ; 
public List<String> attrs ; 


public List<String> getAttrs() {
    return attrs;
}
public String getUserId() {
    return userId;
}
public void setAttrs(List<String> attrs) {
    this.attrs = attrs;
}
public void setUserId(String userId) {
    this.userId = userId;
}


}