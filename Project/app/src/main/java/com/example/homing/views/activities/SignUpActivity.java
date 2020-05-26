package com.example.homing.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.*;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.homing.R;
import com.example.homing.models.classes.User;
import com.example.homing.controllers.AddItemTask;
import com.example.homing.models.helpers.CognitoHelper;
import com.example.homing.models.helpers.DynamoHelper;
import com.example.homing.models.helpers.S3Helper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    EditText birthdayEdit, phoneEdit, nameEdit, statusEdit, passwordEdit, confirmPasswordEdit;
    CircleImageView userImage;
    boolean isImageAdded = false;
    Uri imageUri;
    CognitoHelper cognitoHelper;
    DatePickerDialog.OnDateSetListener dateSetListener;
    @SuppressLint("StaticFieldLeak")
    public static DynamoHelper dynamoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        cognitoHelper = CognitoHelper.getINSTANCE(this);

        AWSMobileClient.getInstance().initialize(this).execute();

        birthdayEdit = findViewById(R.id.birthdayEdit);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        statusEdit = findViewById(R.id.statusEdit);
        userImage = findViewById(R.id.userImage);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmPasswordEdit = findViewById(R.id.passwordConfirmEdit);

        dynamoHelper = DynamoHelper.getINSTANCE(this);

        userImage.setClickable(true);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String day2, month2;

                if (month < 10) {
                    month2 = "0" + month;
                } else {
                    month2 = String.valueOf(month);
                }

                if (dayOfMonth < 10) {
                    day2 = "0" + dayOfMonth;
                } else {
                    day2 = String.valueOf(dayOfMonth);
                }

                String date = day2 + "/" + month2 + "/" + year;
                birthdayEdit.setText(date);
            }
        };
    }

    public void goToLogin(View view) {
        finish();
    }

    public void signUp(View view) {
        if (passwordEdit.getText().toString().equals(confirmPasswordEdit.getText().toString())) {
            CognitoUserPool userPool = cognitoHelper.getUserPool();

            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            userAttributes.addAttribute("phone_number", phoneEdit.getText().toString());

            if (!isImageAdded) {
                userAttributes.addAttribute("picture", getString(R.string.no_image_link));
            } else {
                //S3Helper.uploadImage(imageUri, phoneEdit.getText().toString(), this);
                userAttributes.addAttribute("picture", phoneEdit.getText().toString());
            }

            userAttributes.addAttribute("name", nameEdit.getText().toString());
            userAttributes.addAttribute("birthdate", birthdayEdit.getText().toString());
            userAttributes.addAttribute("custom:status", statusEdit.getText().toString());

            addUserToDB();

            userPool.signUpInBackground(phoneEdit.getText().toString(),
                    passwordEdit.getText().toString(), userAttributes, null,
                    signupHandler(userAttributes, userPool));
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
            alertDialog.setTitle(getString(R.string.mismatch_dialog_title));
            alertDialog.setMessage(getString(R.string.mismatch_dialog_message));
            alertDialog.setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }

    private SignUpHandler signupHandler(final CognitoUserAttributes userAttributes,
                                        final CognitoUserPool userPool) {
        return new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean userConfirmed,
                                  CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                if (!userConfirmed) {
                    Log.d("AWS Cognito", "Signup not confirmed " +
                            cognitoUserCodeDeliveryDetails.getDestination());

                    Intent intent = new Intent(SignUpActivity.this,
                            VerificationActivity.class);
                    intent.putExtra("phone", phoneEdit.getText().toString());
                    startActivity(intent);
                } else {
                    Log.d("AWS Cognito", "Signup confirmed");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("AWS Cognito", "Signup failed  " + exception.getMessage());
            }
        };
    }

    private void addUserToDB() {
        String image = getString(R.string.no_image_link);

        if (isImageAdded) {
            image = phoneEdit.getText().toString();
        }

        User user = new User(nameEdit.getText().toString(), phoneEdit.getText().toString(),
                birthdayEdit.getText().toString(), statusEdit.getText().toString(),
                image, null);

        AddItemTask addItemTask = new AddItemTask(this);
        Document document = new Document();

        document.put("User_ID", user.getPhone());
        document.put("name", user.getName());
        document.put("birthdate", user.getBirthdate());
        document.put("status", user.getStatus());
        document.put("picture", user.getPicture());

        addItemTask.execute(document);
    }

    public void popDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);

        Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(SignUpActivity.this).load(imageUri).into(userImage);
            //isImageAdded = true;
        }
    }
}
