package com.example.homing.models.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

public class UpdateItemTask extends AsyncTask<Document, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Document... documents) {
        boolean isUpdated = false;

        try {
            //isUpdated = dynamoTable.updateInTable(documents[0]);
        } catch (Exception e) {
            Log.d("aws", "Update failed: " + e.getMessage());
        }

        return isUpdated;
    }

    @Override
    protected void onPostExecute(Boolean isUpdated) {
        super.onPostExecute(isUpdated);

        if (isUpdated) {
            Log.d("aws", "Update successful");
        } else {
            Log.d("aws", "Update unsuccessful");
        }
    }
}
