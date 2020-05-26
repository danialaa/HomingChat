package com.example.homing.controllers;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.example.homing.models.helpers.DynamoHelper;

import java.util.List;

public class GetItemTask extends AsyncTask<Table, Void, Object> {
    private Context context;
    private List<String> attributes;
    private String id;

    public GetItemTask(Context context, List<String> attributes, String id) {
        this.context = context;
        this.attributes = attributes;
        this.id = id;
    }

    @Override
    protected Object doInBackground(Table... tables) {
        Object object = DynamoHelper.getINSTANCE(this.context).getItem(attributes, tables[0], id);

        return object;
    }
}
