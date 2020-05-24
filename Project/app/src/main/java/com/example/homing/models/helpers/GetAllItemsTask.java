package com.example.homing.models.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.example.homing.models.classes.Chat;
import com.example.homing.models.classes.User;

import java.util.List;

public class GetAllItemsTask extends AsyncTask<Table, Void, List<Object>> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private List<String> attributes;

    public GetAllItemsTask(Context context, List<String> attributes) {
        this.context = context;
        this.attributes = attributes;
    }

    @Override
    protected List<Object> doInBackground(Table... tables) {
        List list = DynamoHelper.getINSTANCE(this.context).getAllFromTable(attributes, tables[0]);
        return list;
    }
}
