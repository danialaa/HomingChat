package com.example.homing.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.homing.models.classes.Chat;
import com.example.homing.models.classes.Text;
import com.example.homing.models.classes.User;
import com.example.homing.models.helpers.CognitoHelper;
import com.example.homing.models.helpers.DynamoHelper;
import com.example.homing.models.helpers.GetAllItemsTask;
import com.example.homing.models.helpers.GetItemTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
                Log.d("AWS Cognito", "Login Success");

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("User", getUserData());

                startActivity(intent);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation,
                                                 String userId) {
                Log.d("AWS Cognito", "Getting login details");

                AuthenticationDetails details = new AuthenticationDetails(userId,
                        passwordEdit.getText().toString(), null);
                authenticationContinuation.setAuthenticationDetails(details);
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.d("AWS Cognito", "Getting MFA");
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.d("AWS Cognito", "Having challenge in authentication login");
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("AWS Cognito", "Login Failed.. " + exception.getLocalizedMessage());
            }
        };
    }

    private User getUserData() {
        User user = new User();

        List<String> attributes = new ArrayList<>();
        attributes.add("User_ID");
        attributes.add("name");
        attributes.add("birthdate");
        attributes.add("status");
        attributes.add("picture");
        GetItemTask getItemTask = new GetItemTask(LoginActivity.this, attributes,
                phoneEdit.getText().toString());
        getItemTask.execute(DynamoHelper.getINSTANCE(LoginActivity.this).getTables().get(0));

        try {
            user = (User)getItemTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        user.setChats(getChatList());

        return user;
    }

    private List<Chat> getChatList() {
        List<String> attributes = new ArrayList<>();

        attributes.clear();
        attributes.add("Chat_ID");
        attributes.add("Text_ID");
        attributes.add("date");
        attributes.add("time");
        attributes.add("content");
        attributes.add("seen");
        attributes.add("byfirst");
        attributes.add("type");

        GetAllItemsTask getAllItemsTask = new GetAllItemsTask(LoginActivity.this, attributes);
        getAllItemsTask.execute(DynamoHelper.getINSTANCE(LoginActivity.this).getTables().get(1));
        List<Object> allChats = new ArrayList<>();

        try {
            allChats = getAllItemsTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Chat> myChats = objectToChat(allChats);
        myChats = Text.returnUserChats(phoneEdit.getText().toString(), myChats);

        Log.d("Login Activity", "Getting user chats.. original size = " +
                allChats.size() + ", now = " + myChats.size());

        return myChats;
    }

    private List<Chat> objectToChat(List<Object> list)  {
        List<Chat> chats = new ArrayList<>();

        for (Object object : list) {
            chats.add((Chat)object);
        }

        return chats;
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
