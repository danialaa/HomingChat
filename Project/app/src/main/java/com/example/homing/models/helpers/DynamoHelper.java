package com.example.homing.models.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.PutItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.homing.models.classes.Chat;
import com.example.homing.models.classes.Text;
import com.example.homing.models.classes.User;

import java.util.ArrayList;
import java.util.List;

public class DynamoHelper {
    private Context context;
    private final String poolID = "us-east-1:83d35d6a-5c8c-4e7f-ad01-46ccb6d3e9b4";
    private final Regions poolregion = Regions.US_EAST_1;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dynamoDBClient;
    @SuppressLint("StaticFieldLeak")
    private static volatile DynamoHelper INSTANCE = null;
    private ArrayList<Table> tables = new ArrayList<>();

    public static synchronized DynamoHelper getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DynamoHelper(context);
        }

        return INSTANCE;
    }

    private DynamoHelper(Context context) {
        this.context = context;

        credentialsProvider = new CognitoCachingCredentialsProvider(this.context, poolID, poolregion);
        dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void loadTable (String tableName) {
        Table table = Table.loadTable(dynamoDBClient, tableName);

        tables.add(table);
    }

    public Document insertInTable(Document item, Table table) {
        PutItemOperationConfig putItemOperationConfig = new PutItemOperationConfig();
        putItemOperationConfig.withReturnValues(ReturnValue.ALL_OLD);

        return table.putItem(item, putItemOperationConfig);
    }

    public List getAllFromTable(List<String> attributes, Table table) {
        ScanOperationConfig scanOperationConfig = new ScanOperationConfig();
        scanOperationConfig.withAttributesToGet(attributes);
        Search searchResult = table.scan(scanOperationConfig);

        List list = searchResult.getAllResults();

        Log.d("AWS Dynamo", "Get All from " + table.getTableName() + " size = " + list.size());

        switch (table.getTableName()) {
            case "Users":
                return User.returnUsers(list, this.context);

            case "ChatTexts":
                List<Chat> chats = Text.returnChats(list);
                return chats;
        }

        return null;
    }

    public Object getItem(List<String> attributes, Table table, String id) {
        ScanOperationConfig scanOperationConfig = new ScanOperationConfig();
        scanOperationConfig.withAttributesToGet(attributes);
        Search searchResult = table.scan(scanOperationConfig);

        Log.d("AWS Dynamo", "Get item from " + table.getTableName() + ", id = " + id);

        switch (table.getTableName()) {
            case "Users":
                Document document = table.getItem(new Primitive(id));
                return User.returnUser(document, this.context);

            //case "ChatTexts":
            //    return Text.returnChat(searchResult.getAllResults());
        }

        return null;
    }

//    public Document deleteFromTable(String id) {
//        return table.deleteItem(new Primitive(id), new DeleteItemOperationConfig().withReturnValues(ReturnValue.ALL_OLD));
//    }

    public boolean updateInTable(Document item, Table table) {
        String idName;
        Document retrievedItem;

        if (item.containsKey("User_ID")) {
            retrievedItem = table.getItem(new Primitive(item.get("User_ID").asString()));
            idName = "User_ID";
        } else {
            retrievedItem = table.getItem(new Primitive(item.get("Chat_ID").asString()),
                    new Primitive(item.get("Text_ID").asInt()));
            idName = "ChatID";
        }

        boolean isUpdated = false;
        Document updatedDocument;

        if (retrievedItem != null) {
            if (idName.equals("User_ID")) {
                retrievedItem = User.putAttributes(item, retrievedItem);
                updatedDocument = table.updateItem(retrievedItem, new Primitive(item.get("User_ID").asString()),
                        new UpdateItemOperationConfig().withReturnValues(ReturnValue.UPDATED_NEW));
            } else {
                retrievedItem = Text.putAttributes(item, retrievedItem);
                updatedDocument = table.updateItem(retrievedItem, new Primitive(item.get("Chat_ID").asString()),
                        new Primitive(item.get("Text_ID").asInt()),
                        new UpdateItemOperationConfig().withReturnValues(ReturnValue.UPDATED_NEW));
            }

            try {
                Log.d("AWS Dynamo", "Updating in table: " + Document.toJson(updatedDocument));

                isUpdated = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("AWS Dynamo", "Updating in table: json error = " + e.getLocalizedMessage());
            }
        } else {
            Log.d("AWS Dynamo", "retrieved not found");
        }

        return isUpdated;
    }

    public CognitoCachingCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public AmazonDynamoDBClient getDynamoDBClient() {
        return dynamoDBClient;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }
}