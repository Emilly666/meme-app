package com.example.memeapp.model.meme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meme {
    private Integer id;
    private String file_path;
    private String title;
    private long user_id;
    private Timestamp add_timestamp = new Timestamp(System.currentTimeMillis());
    private int total_likes = 0;
    private int total_dislikes = 0;
}
