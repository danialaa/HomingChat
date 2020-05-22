package com.example.homing.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.example.homing.R;
import com.example.homing.models.helpers.CognitoHelper;

public class VerificationActivity extends AppCompatActivity {
    EditText codeEdit;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        codeEdit = findViewById(R.id.codeEdit);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
    }

    public void verifySignup(View view) {
        new VerificationProcess().execute(codeEdit.getText().toString(), phoneNumber);
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void resendCode(View view) {
        CognitoHelper cognitoHelper = CognitoHelper.getINSTANCE(this);
        CognitoUser user = cognitoHelper.getUserPool().getUser(phoneNumber);

        new ResendProcess().execute(user);

        TextView resend = findViewById(R.id.newCodeText);
        resend.setClickable(false);
    }

    @SuppressLint("StaticFieldLeak")
    private class ResendProcess extends AsyncTask<CognitoUser, Void, String> {
        @Override
        public String doInBackground(CognitoUser... cognitoUsers) {
            final String[] result = new String[1];

            VerificationHandler resendHandler = new VerificationHandler() {
                @Override
                public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                    result[0] = "Code resent successfully to " + verificationCodeDeliveryMedium.getDestination();
                }

                @Override
                public void onFailure(Exception exception) {
                    result[0] = "Failure.. " + exception.getLocalizedMessage();
                }
            };

            cognitoUsers[0].resendConfirmationCodeInBackground(resendHandler);

            return result[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("AWS cognito:", "Resend result = " + s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class VerificationProcess extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... strings) {
            final String[] result = new String[1];

            final GenericHandler verificationHandler = new GenericHandler() {
                @Override
                public void onSuccess() {
                    result[0] = "Success";
                }

                @Override
                public void onFailure(Exception exception) {
                    result[0] = "Failure.. " + exception.getMessage();
                }
            };

            CognitoHelper cognitoHelper = CognitoHelper.getINSTANCE(VerificationActivity.this);
            CognitoUser user = cognitoHelper.getUserPool().getUser(strings[1]);
            user.confirmSignUp(strings[0], false, verificationHandler);

            return result[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("AWS cognito:", "Verification result = " + s);
        }
    }
}
