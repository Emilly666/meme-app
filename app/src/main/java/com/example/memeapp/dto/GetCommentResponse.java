package com.example.memeapp.dto;

import com.example.memeapp.model.comment.MemeComment;

import java.util.List;

public class GetCommentResponse {
    private List<MemeComment> commentsList;

    public List<MemeComment> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<MemeComment> commentsList) {
        this.commentsList = commentsList;
    }
}