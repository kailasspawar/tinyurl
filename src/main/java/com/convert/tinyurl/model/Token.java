package com.convert.tinyurl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String tokenString;

    
    public void setId(Integer id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }
    public Integer getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getToken() {
        return tokenString;
    }
    // Constructors, getters, and setters
    
}

