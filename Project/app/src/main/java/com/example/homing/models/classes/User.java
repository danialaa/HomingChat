package com.example.homing.models.classes;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBEntry;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    private String name, phone, birthdate, status, picture;
    private List<Chat> chats = new ArrayList<>();

    public User(String name, String phone, String birthdate, String status, String picture, List<Chat> chats) {
        this.name = name;
        this.phone = phone;
        this.birthdate = birthdate;
        this.status = status;
        this.picture = picture;
        this.chats = chats;
    }

    public User() {
    }

//    @Override
//    public boolean updateInTable(Document item) {
//        Document retrievedItem = table.getItem(new Primitive(item.get("id").asString()));
//        boolean isUpdated = false;
//
//        if (retrievedItem != null) {
//            retrievedItem.put("id", item.get("id").asString());
//            retrievedItem.put("name", item.get("name").asString());
//            retrievedItem.put("birthdate", item.get("birthdate").asString());
//            retrievedItem.put("status", item.get("status").asString());
//
//            Document updateDocument = table.updateItem(retrievedItem, new Primitive(item.get("id").asString()),
//                    new UpdateItemOperationConfig().withReturnValues(ReturnValue.UPDATED_NEW));
//
//            try {
//                Log.d("aws", "Updating in table: " + Document.toJson(updateDocument));
//
//                isUpdated = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d("aws", "Updating in table: json error = " + e.getLocalizedMessage());
//            }
//        }
//
//        return isUpdated;
//    }

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