package com.example.homing.models.classes;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.homing.models.enums.TextType;
import com.example.homing.models.helpers.DynamoHelper;

import java.io.Serializable;
import java.lang.reflect.Array;
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

    public static Chat returnChat(String firstUser, String secondUser, Context context) {
        DynamoHelper dynamoHelper = DynamoHelper.getINSTANCE(context);

        List<String> attributes = new ArrayList<>();
        attributes.add("Chat_ID");
        attributes.add("Text_ID");
        attributes.add("date");
        attributes.add("time");
        attributes.add("content");
        attributes.add("seen");
        attributes.add("byfirst");
        attributes.add("type");

        List<Chat> chats = dynamoHelper.getAllFromTable(attributes, dynamoHelper.getTables().get(1));
        Chat matchingChat = new Chat();

        for (int i = 0; i < chats.size(); i++) {
            String firstRetrievedUser = chats.get(i).getId().substring(0, 13);
            String secondRetrievedUser = chats.get(i).getId().substring(13);

            if ((firstRetrievedUser.equals(firstUser) || firstRetrievedUser.equals(secondUser)) &&
                    (secondRetrievedUser.equals(firstUser) || secondRetrievedUser.equals(secondUser))) {
                matchingChat = chats.get(i);
                break;
            }
        }

        return matchingChat;
    }

//    public static Chat returnChat(Document document, Context context) {
//        Chat chat = new Chat();
//        chat.setId(document.get("Chat_ID").asString());
//
//        for (int u = i + 1; u < documents.size(); u++) {
//            if (documents.get(u).get("Chat_ID").asString().equals(chat.getId())) {
//                Text text = new Text();
//                text.setId(documents.get(u).get("Text_ID").asInt());
//                text.setDate(documents.get(u).get("date").asString());
//                text.setTime(documents.get(u).get("time").asString());
//                text.setContent(documents.get(u).get("content").asString());
//                text.setSeen(documents.get(u).get("seen").asBoolean());
//                text.setByFirst(documents.get(u).get("byfirst").asBoolean());
//
//                switch (documents.get(u).get("type").asString()) {
//                    case "text":
//                        text.setType(TextType.TEXT);
//
//                        break;
//
//                    case "image":
//                        text.setType(TextType.IMAGE);
//
//                        break;
//
//                    case "voicenote":
//                        text.setType(TextType.VOICE_NOTE);
//
//                        break;
//                }
//
//                chat.getTexts().add(text);
//            }
//        }
//
//        chatIds.add(chat.getId());
//        chats.add(chat);
//
//    }

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
        isByFirst = isByFirst;
    }

    public TextType getType() {
        return type;
    }

    public void setType(TextType type) {
        this.type = type;
    }
}