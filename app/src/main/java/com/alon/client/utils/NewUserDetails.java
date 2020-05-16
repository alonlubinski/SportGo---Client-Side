package com.alon.client.utils;

public class NewUserDetails {
    private UserRole role;
    private String userName, email, avatar;

    // Constructors
    public NewUserDetails(){

    }

    public NewUserDetails(UserRole role, String userName, String email, String avatar) {
        this.role = role;
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
    }

    // Getters and setters
    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
