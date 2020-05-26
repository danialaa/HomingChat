package com.example.homing.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.homing.models.helpers.DynamoHelper;

public class UpdateItemTask extends AsyncTask<Document, Void, Boolean> {
    public Context context;
    public Table table;

    public UpdateItemTask(Context context, Table table) {
        this.context = context;
        this.table = table;
    }

    @Override
    protected Boolean doInBackground(Document... documents) {
        boolean isUpdated;

        isUpdated = DynamoHelper.getINSTANCE(context).updateInTable(documents[0], table);

        return isUpdated;
    }

    @Override
    protected void onPostExecute(Boolean isUpdated) {
        super.onPostExecute(isUpdated);

        if (isUpdated) {
            Log.d("Update Item Task", "Update successful");
        } else {
            Log.d("Update Item Task", "Update unsuccessful");
        }
    }
}
