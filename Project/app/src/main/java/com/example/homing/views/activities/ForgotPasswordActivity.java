package com.example.homing.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.example.homing.R;
import com.example.homing.models.helpers.CognitoHelper;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText phoneEdit, codeEdit, passwordEdit, confirmEdit;
    ForgotPasswordHandler forgotPasswordHandler;
    ForgotPasswordContinuation resultContinuation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        phoneEdit = findViewById(R.id.phoneEdit);
        codeEdit = findViewById(R.id.codeEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmEdit = findViewById(R.id.passwordConfirmEdit);

        forgotPasswordHandler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                Log.d("AWS Cognito", "Password changed successfully");
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                CognitoUserCodeDeliveryDetails codeDeliveryDetails = continuation.getParameters();
                Log.d("AWS Cognito", "Password change code sent to " +
                        codeDeliveryDetails.getDestination());

                resultContinuation = continuation;
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("AWS Cognito", "Password change failure.. " +
                        exception.getLocalizedMessage());
            }
        };
    }

    public void verifyNumber(View view) {
        CognitoHelper cognitoHelper = CognitoHelper.getINSTANCE(this);
        CognitoUser user = cognitoHelper.getUserPool().getUser(phoneEdit.getText().toString());
        user.forgotPasswordInBackground(forgotPasswordHandler);
    }

    public void changePassword(View view) {
        if (!passwordEdit.getText().toString().equals(confirmEdit.getText().toString())) {
            //dialog for passwords
        } else if (!codeEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty()) {
            resultContinuation.setPassword(passwordEdit.getText().toString());
            resultContinuation.setVerificationCode(codeEdit.getText().toString());

            resultContinuation.continueTask();
        } else {
            //dialog for empty
        }
    }
}
