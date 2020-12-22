package com.example.chattingapplication.Model;

public class User {

    private String id;
    private String username;
    //imageProfile
    private String imageURL;
    private String userPhone;
    private String email;
    private String status;
   // private String status;

    public User(String userID, String userName, String userPhone,String status,String email,String imageProfile) {
        this.id = userID;
        this.username = userName;
        this.imageURL = imageProfile;

        this.userPhone = userPhone;
        this.status = status;
        this.email = email;
       // this.status = status;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
