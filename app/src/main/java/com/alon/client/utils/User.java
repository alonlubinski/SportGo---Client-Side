package com.alon.client.utils;

public class User {
    private static User instance;
    private String email, username, avatar;
    private String userRole;

    // Constructor.
    private User() {
    }

    // Getters and setters.
    public static synchronized User getInstance(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }


    public void login(String email, String username, String avatar, String userRole){
        instance.setEmail(email);
        instance.setUsername(username);
        instance.setAvatar(avatar);
        instance.setUserRole(userRole);
    }

    public static void logout(){
        instance.setEmail("");
        instance.setUsername("");
        instance.setAvatar("");
        instance.setUserRole("");
    }
}
