package com.example.homing.models.helpers;

import android.content.Context;
import android.os.AsyncTask;

public class LoadTableTask extends AsyncTask<String, Void, Void> {
    private Context context;

    public LoadTableTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        DynamoHelper.getINSTANCE(this.context).loadTable(strings[0]);

        return null;
    }
}
