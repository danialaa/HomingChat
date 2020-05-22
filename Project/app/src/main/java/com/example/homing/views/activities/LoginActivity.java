package com.example.homing.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.example.homing.R;
import com.example.homing.models.helpers.CognitoHelper;

public class LoginActivity extends AppCompatActivity {
    EditText phoneEdit, passwordEdit;
    AuthenticationHandler authHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneEdit = findViewById(R.id.phoneEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        authHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.d("AWS cognito:", "Login Success");

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.d("AWS cognito:", "Getting login details");

                AuthenticationDetails details = new AuthenticationDetails(userId, passwordEdit.getText().toString(), null);
                authenticationContinuation.setAuthenticationDetails(details);
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.d("AWS cognito:", "Getting MFA");
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.d("AWS cognito:", "Having challenge in authentication login");
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("AWS cognito:", "Login Failed.. " + exception.getLocalizedMessage());
            }
        };
    }

    public void goToHome(View view) {
        CognitoHelper cognitoHelper = CognitoHelper.getINSTANCE(this);
        CognitoUser user = cognitoHelper.getUserPool().getUser(phoneEdit.getText().toString());
        user.getSessionInBackground(authHandler);
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}
