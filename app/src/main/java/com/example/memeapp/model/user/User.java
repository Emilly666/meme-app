package com.example.memeapp.model.user;

public class User {


    private long id;
    private String nickname;
    private String password;
    private String pictureURL;
    private String email;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPictureURL() { return pictureURL; }
    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

