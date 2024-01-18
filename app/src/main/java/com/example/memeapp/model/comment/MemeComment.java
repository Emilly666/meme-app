package com.example.memeapp.model.comment;

import androidx.annotation.NonNull;

import java.sql.Timestamp;

public class MemeComment {
    private String authorNickname;
    private String comment;
    private Timestamp add_timestamp = new Timestamp(System.currentTimeMillis());


    public Timestamp getAdd_timestamp() {
        return add_timestamp;
    }

    public void setAdd_timestamp(Timestamp add_timestamp) {
        this.add_timestamp = add_timestamp;
    }



    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
