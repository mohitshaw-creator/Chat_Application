package com.example.chattingapplication;

public class Users {
    private String userID;
    private String userName;
    private String userPhone;
    private String imageProfile;
    private String email;
    private String status;

    public Users() {
    }

    public Users(String userID, String userName, String userPhone, String imageProfile, String email, String status) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imageProfile = imageProfile;
        this.email = email;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
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
