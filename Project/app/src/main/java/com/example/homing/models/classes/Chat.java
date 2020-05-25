package com.example.homing.models.classes;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat extends Object implements Serializable {
    private String id;
    private List<Text> texts = new ArrayList<>();
    private String firstUser, secondUser;

    public Chat(String id, List<Text> texts) {
        this.id = id;
        this.texts = texts;
        setUsers();
    }

    public Chat() {
    }

    public void setUsers() {
        this.firstUser = id.substring(0, 13);
        this.secondUser = id.substring(13);
    }

    public String getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(String firstUser) {
        this.firstUser = firstUser;
    }

    public String getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(String secondUser) {
        this.secondUser = secondUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setUsers();
    }

    public List<Text> getTexts() {
        return texts;
    }

    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }
}