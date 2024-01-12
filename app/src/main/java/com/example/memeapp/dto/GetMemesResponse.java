package com.example.memeapp.dto;

import com.example.memeapp.model.meme.Meme;

import java.util.List;

public class GetMemesResponse {
    private List<Meme> memesWithTags;

    public List<Meme> getMemesWithTags() {
        return memesWithTags;
    }

    public void setMemesWithTags(List<Meme> memesWithTags) {
        this.memesWithTags = memesWithTags;
    }
}
