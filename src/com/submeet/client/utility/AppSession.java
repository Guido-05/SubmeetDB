package com.submeet.client.utility;


public class AppSession {

    // Singleton instance
    private static AppSession instance;

    // User ID of the logged-in user
    private int userId  = -1;

    // Username of the logged-in user
    private String username;

    // Mail of the logged-in user
    private String mail;

    // Private constructor to prevent instantiation
    private AppSession() {}

    // Static method to get the singleton instance
    public static AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }

    // Getter for userId
    public int getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter for userId
    public String getUsername() {
        return username;
    }

    // Setter for userId
    public void setUsername(String userId) {
        this.username = username;
    }

    // Getter for userId
    public String getMail() {
        return mail;
    }

    // Setter for userId
    public void setMail(String mail) {
        this.mail = mail;
    }

    // Method to clear the session (logout)
    public void clear() {
        this.userId = 0;
    }
}
