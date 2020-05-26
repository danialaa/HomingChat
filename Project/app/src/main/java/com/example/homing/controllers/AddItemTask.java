package com.example.homing.controllers;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.homing.models.helpers.DynamoHelper;

public class AddItemTask extends AsyncTask<Document, Void, Void> {
    private Context context;

    public AddItemTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Document... documents) {
        if (documents[0].containsKey("User_ID")) {
            DynamoHelper.getINSTANCE(this.context).insertInTable(documents[0],
                    DynamoHelper.getINSTANCE(this.context).getTables().get(0));
        } else {
            DynamoHelper.getINSTANCE(this.context).insertInTable(documents[0],
                    DynamoHelper.getINSTANCE(this.context).getTables().get(1));
        }

        return null;
    }
}
