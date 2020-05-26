package com.example.homing.models.classes;

import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends Object implements Serializable {
    private String name, phone, birthdate, status, picture;
    private List<Chat> chats = new ArrayList<>();

    public User(String name, String phone, String birthdate, String status, String picture,
                List<Chat> chats) {
        this.name = name;
        this.phone = phone;
        this.birthdate = birthdate;
        this.status = status;
        this.picture = picture;
        this.chats = chats;
    }

    public User() {
    }

    public static List<User> returnUsers(List<Document> documents, Context context) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            users.add(returnUser(documents.get(i), context));
        }

        return users;
    }

    public static User returnUser(Document document, Context context) {
        User user = new User();
        user.setPhone(document.get("User_ID").asString());
        user.setName(document.get("name").asString());
        user.setBirthdate(document.get("birthdate").asString());
        user.setStatus(document.get("status").asString());
        user.setPicture(document.get("picture").asString());

        return user;
    }

    public static Document putAttributes(Document original, Document copy) {
        copy.put("User_ID", original.get("User_ID").asString());
        copy.put("name", original.get("name").asString());
        copy.put("birthdate", original.get("birthdate").asString());
        copy.put("status", original.get("status").asString());
        copy.put("picture", original.get("picture").asString());

        return copy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }
}