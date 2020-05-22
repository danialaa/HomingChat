package com.example.homing.models.helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

public class AddItemTask extends AsyncTask<Document, Void, Void> {
    private Context context;

    public AddItemTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Document... documents) {
        DynamoHelper.getINSTANCE(this.context).loadTable("Users");

        DynamoHelper.getINSTANCE(this.context).insertInTable(documents[0], DynamoHelper.getINSTANCE(this.context).getTables().get(0));
        return null;
    }
}
