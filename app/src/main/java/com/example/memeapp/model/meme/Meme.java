package com.example.memeapp.model.meme;

import com.example.memeapp.model.tag.Tag;

import java.sql.Timestamp;
import java.util.List;

public class Meme {
    private Integer id;
    private String file_path;
    private String content_type;
    private String title;
    private Timestamp add_timestamp = new Timestamp(System.currentTimeMillis());
    private int total_likes = 0;
    private Integer author_id;
    private String author_nickname;
    private int reactionValue;
    private List<Tag> tags;

    public Meme(Integer id, String file_path, String content_type, String title, Timestamp add_timestamp, int total_likes, Integer author_id, String author_nickname, int reactionValue, List<Tag> tags) {
        this.id = id;
        this.file_path = file_path;
        this.content_type = content_type;
        this.title = title;
        this.add_timestamp = add_timestamp;
        this.total_likes = total_likes;
        this.author_id = author_id;
        this.author_nickname = author_nickname;
        this.reactionValue = reactionValue;
        this.tags = tags;
    }
    public Meme(){};

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

    public void setAuthor_nickname(String author_nickname) { this.author_nickname = author_nickname; }

    public int getReactionValue() {
        return reactionValue;
    }

    public void setReactionValue(int reactionValue) {
        this.reactionValue = reactionValue;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getContent_type() { return this.content_type; }
    public void setContent_type(String content_type) { this.content_type = content_type; }
}
