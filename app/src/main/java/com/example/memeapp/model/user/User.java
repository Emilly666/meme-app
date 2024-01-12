package com.example.memeapp.model.user;

import com.example.memeapp.dto.AuthenticationResponse;

public class User {

    private Integer id = 0;
    private String nickname;
    private String email;
    private String password;
    private String pictureURL;

    public User(AuthenticationResponse response){
        id = response.getUser_id();
        nickname = response.getNickname();
        email = response.getEmail();
        password = response.getPassword();
        pictureURL = response.getPictureURL();
    }
    public User(){

    }
    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

