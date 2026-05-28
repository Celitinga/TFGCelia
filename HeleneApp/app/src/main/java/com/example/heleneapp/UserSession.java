package com.example.heleneapp;

import java.util.ArrayList;
import java.util.List;

public class UserSession {

    private static UserSession instance;

    private Long userId;
    private String username;
    private String token;
    private List<String> roles = new ArrayList<>();

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(Long userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public void clear() {
        userId = null;
        username = null;
        token = null;
        roles.clear();
    }
}