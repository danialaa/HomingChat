package com.example.homing.models.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.DeleteItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.PutItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.util.ArrayList;
import java.util.List;

public class DynamoHelper {
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dynamoDBClient;
    @SuppressLint("StaticFieldLeak")
    private static volatile DynamoHelper INSTANCE = null;
    ArrayList<Table> tables = new ArrayList<>();

    public static synchronized DynamoHelper getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DynamoHelper(context);
        }

        return INSTANCE;
    }

    private DynamoHelper(Context context) {
        this.context = context;

        credentialsProvider = new CognitoCachingCredentialsProvider(this.context, "us-east-1:83d35d6a-5c8c-4e7f-ad01-46ccb6d3e9b4",
                Regions.US_EAST_1);
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

//    public Document deleteFromTable(String id) {
//        return table.deleteItem(new Primitive(id), new DeleteItemOperationConfig().withReturnValues(ReturnValue.ALL_OLD));
//    }

//    public List<Document> getAllFromTable(List<String> attributes) {
//        ScanOperationConfig scanOperationConfig = new ScanOperationConfig();
//        scanOperationConfig.withAttributesToGet(attributes);
//        Search searchResult = table.scan(scanOperationConfig);
//        return searchResult.getAllResults();
//    }

//    public abstract boolean updateInTable(Document item);

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