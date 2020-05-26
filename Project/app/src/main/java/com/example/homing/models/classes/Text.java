package com.example.homing.models.classes;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.homing.models.enums.TextType;
import com.example.homing.models.helpers.DynamoHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Text implements Serializable {
    private int id;
    private String date, time, content;
    private boolean isSeen, isByFirst;
    private TextType type;

    public Text(int id, String date, String time, String content, boolean isSeen, boolean isByFirst,
                TextType type) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.content = content;
        this.isSeen = isSeen;
        this.isByFirst = isByFirst;
        this.type = type;
    }

    public Text() {

    }

    public static List<Chat> returnChats(List<Document> documents) {
        List<Chat> chats = new ArrayList<>();
        List<String> chatIds = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            Chat chat = new Chat();
            chat.setId(documents.get(i).get("Chat_ID").asString());

            if (!chatIds.contains(chat.getId())) {
                for (int u = i; u < documents.size(); u++) {
                    if (documents.get(u).get("Chat_ID").asString().equals(chat.getId())) {
                        Text text = new Text();
                        text.setId(documents.get(u).get("Text_ID").asInt());
                        text.setDate(documents.get(u).get("date").asString());
                        text.setTime(documents.get(u).get("time").asString());
                        text.setContent(documents.get(u).get("content").asString());
                        text.setSeen(documents.get(u).get("seen").asBoolean());
                        text.setByFirst(documents.get(u).get("byfirst").asBoolean());

                        switch (documents.get(u).get("type").asString()) {
                            case "text":
                                text.setType(TextType.TEXT);
                                break;

                            case "image":
                                text.setType(TextType.IMAGE);
                                break;

                            case "voicenote":
                                text.setType(TextType.VOICE_NOTE);
                                break;
                        }

                        chat.getTexts().add(text);
                    }
                }

                chatIds.add(chat.getId());
                chats.add(chat);
            }
        }

        Log.d("Text Class", "Returning chats.. original size = " + documents.size() + ", now = " + chats.size());

        return chats;
    }

    public static List<Chat> returnUserChats(String id, List<Chat> chats) {
        List<Chat> matchingChats = new ArrayList<>();

        for (int i = 0; i < chats.size(); i++) {
            String firstUser = chats.get(i).getId().substring(0, 13);
            String secondUser = chats.get(i).getId().substring(13);
            Log.d("Text Class", "first user = " + firstUser + ", second user = " + secondUser);

            if (id.equals(firstUser) || id.equals(secondUser)) {
                matchingChats.add(chats.get(i));
            }
        }

        return matchingChats;
    }

    public Document objectToDocument(Chat chat, int textNum) {
        Document document = new Document();

        document.put("Chat_ID", chat.getId());
        document.put("Text_ID", chat.getTexts().get(textNum).getId());
        document.put("date", chat.getTexts().get(textNum).getDate());
        document.put("time", chat.getTexts().get(textNum).getTime());
        document.put("content", chat.getTexts().get(textNum).getContent());
        document.put("seen", chat.getTexts().get(textNum).getSeen());
        document.put("byfirst", chat.getTexts().get(textNum).getByFirst());
        document.put("type", "text");

        return document;
    }

    public static Document putAttributes(Document original, Document copy) {
        copy.put("Chat_ID", original.get("Chat_ID").asString());
        copy.put("Text_ID", original.get("Text_ID").asInt());
        copy.put("date", original.get("date").asString());
        copy.put("time", original.get("time").asString());
        copy.put("content", original.get("content").asString());
        copy.put("seen", original.get("seen").asBoolean());
        copy.put("byfirst", original.get("byfirst").asBoolean());
        copy.put("type", original.get("type").asString());

        return copy;
    }

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

    public boolean getSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean getByFirst() {
        return isByFirst;
    }

    public void setByFirst(boolean isByFirst) {
        this.isByFirst = isByFirst;
    }

    public TextType getType() {
        return type;
    }

    public void setType(TextType type) {
        this.type = type;
    }
}