package com.example.homing.models.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat extends Object implements Serializable {
    private String id;
    private List<Text> texts = new ArrayList<>();

    public Chat(String id, List<Text> texts) {
        this.id = id;
        this.texts = texts;
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Text> getTexts() {
        return texts;
    }

    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }
}