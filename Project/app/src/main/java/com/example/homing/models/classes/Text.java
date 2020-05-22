package com.example.homing.models.classes;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.homing.models.enums.TextType;

public class Text {
    private int id;
    private String date, time, content;
    private boolean isSeen, isByMe;
    private TextType type;

    public Text(int id, String date, String time, String content, boolean isSeen, boolean isByMe, TextType type) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.content = content;
        this.isSeen = isSeen;
        this.isByMe = isByMe;
        this.type = type;
    }

    public Text() {

    }

//    @Override
//    public boolean updateInTable(Document item) {
//        Document retrievedItem = table.getItem(new Primitive(item.get("id").asString()));
//        boolean isUpdated = false;
//
//        if (retrievedItem != null) {
//            retrievedItem.put("id", item.get("id").asString());
//            retrievedItem.put("date", item.get("date").asString());
//            retrievedItem.put("time", item.get("time").asString());
//            retrievedItem.put("isSeen", item.get("isSeen").asBoolean());
//            retrievedItem.put("content", item.get("content").asString());
//
//            switch (item.get("type").asString()) {
//                case "TextType.IMAGE":
//                    retrievedItem.put("type", "image");
//
//                    break;
//
//                case "TextType.VOICE_NOTE":
//                    retrievedItem.put("type", "voicenote");
//
//                    break;
//
//                default:
//                    retrievedItem.put("type", "text");
//
//                    break;
//            }
//            switch (item.get("isByMe").asString()) {
//                case "false":
//                    retrievedItem.put("firstSender", false);
//
//                    break;
//
//                default:
//                    retrievedItem.put("firstSender", true);
//
//                    break;
//            }
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isByMe() {
        return isByMe;
    }

    public void setByMe(boolean byMe) {
        isByMe = byMe;
    }

    public TextType getType() {
        return type;
    }

    public void setType(TextType type) {
        this.type = type;
    }
}