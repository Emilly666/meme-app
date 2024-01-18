package com.example.memeapp.dto;


public class UserProfileResponse {
    private String nickname;
    private int totalMemesUploaded;
    private int totalLikesGiven;
    private int totalDislikesGiven;
    private int totalLikesReceived;
    private int totalDislikesReceived;
    private int totalComments;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTotalLikesGiven() {
        return totalLikesGiven;
    }

    public void setTotalLikesGiven(int totalLikesGiven) {
        this.totalLikesGiven = totalLikesGiven;
    }

    public int getTotalDislikesGiven() {
        return totalDislikesGiven;
    }

    public void setTotalDislikesGiven(int totalDislikesGiven) {
        this.totalDislikesGiven = totalDislikesGiven;
    }

    public int getTotalLikesReceived() {
        return totalLikesReceived;
    }

    public void setTotalLikesReceived(int totalLikesReceived) {
        this.totalLikesReceived = totalLikesReceived;
    }

    public int getTotalDislikesReceived() {
        return totalDislikesReceived;
    }

    public void setTotalDislikesReceived(int totalDislikesReceived) {
        this.totalDislikesReceived = totalDislikesReceived;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getTotalMemesUploaded() {
        return totalMemesUploaded;
    }

    public void setTotalMemesUploaded(int totalMemesUploaded) {
        this.totalMemesUploaded = totalMemesUploaded;
    }
}
