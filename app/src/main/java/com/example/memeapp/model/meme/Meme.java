package com.example.memeapp.model.meme;

import java.sql.Timestamp;

public class Meme {
    private Integer id;
    private String file_path;
    private String title;
    private long user_id;
    private Timestamp add_timestamp = new Timestamp(System.currentTimeMillis());
    private int total_likes = 0;
    private Integer author_id;
    private String author_nickname;
    private int reactionType;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public Timestamp getAdd_timestamp() {
        return add_timestamp;
    }

    public void setAdd_timestamp(Timestamp add_timestamp) {
        this.add_timestamp = add_timestamp;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public int getReactionType() {
        return reactionType;
    }

    public void setReactionType(int reactionType) {
        this.reactionType = reactionType;
    }
}
