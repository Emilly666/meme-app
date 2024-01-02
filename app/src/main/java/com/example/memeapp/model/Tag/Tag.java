package com.example.memeapp.model.Tag;

public class Tag {

    private long id;
    private String name;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString(){
        return "id: "+id+
                "name: "+ name;
    }
}
