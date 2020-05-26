package com.example.homing.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.homing.R;
import com.example.homing.models.adapters.ChatAdapter;
import com.example.homing.models.classes.Chat;
import com.example.homing.models.classes.Text;
import com.example.homing.models.classes.User;
import com.example.homing.models.enums.TextType;
import com.example.homing.models.helpers.AddItemTask;
import com.example.homing.models.helpers.DynamoHelper;
import com.example.homing.models.helpers.GetItemTask;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private AppCompatImageButton backButton, sendButton;
    private CircleImageView friendImage;
    private TextView friendName;
    private RecyclerView chatRecycler;
    private EditText text;
    public static int chatNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatNum = getIntent().getIntExtra("Chat", 0);

        backButton = findViewById(R.id.backButton);
        sendButton = findViewById(R.id.sendButton);
        friendImage = findViewById(R.id.friendImage);
        friendName = findViewById(R.id.friendNameText);
        chatRecycler = findViewById(R.id.chatRecycler);
        text = findViewById(R.id.textEdit);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sendButton.setClickable(true);
                    sendButton.getBackground().setTint(ContextCompat.getColor(
                            ChatActivity.this, R.color.colorPrimaryDark));
                } else {
                    sendButton.setClickable(false);
                    sendButton.getBackground().setTint(ContextCompat.getColor(
                            ChatActivity.this, R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        friendName.setText(getIntent().getStringExtra("Friend"));

        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setHasFixedSize(true);
        refreshAdapter();
    }

    private void refreshAdapter() {
        chatRecycler.setAdapter(new ChatAdapter(this, HomeActivity.user.getChats().get(chatNum).getFirstUser()));
    }

    public void backToHome(View view) {
        finish();
    }

    public void sendText(View view) {
        Chat chat = HomeActivity.user.getChats().get(chatNum);

        AddItemTask addItemTask = new AddItemTask(this);
        Document document = new Document();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String todayDate = dateFormat.format(date);
        String todayTime = timeFormat.format(date);
        todayTime.replaceAll("\\s+", "");

        document.put("Chat_ID", chat.getId());
        document.put("Text_ID", chat.getTexts().size());
        document.put("date", todayDate);
        document.put("time", todayTime);
        document.put("content", text.getText().toString());
        document.put("seen", false);

        boolean isByFirst = false;

        if (HomeActivity.user.getChats().get(chatNum).getFirstUser() == HomeActivity.user.getPhone()) {
            Log.d("Chat Activity", "text is by first who is " + HomeActivity.user.getChats().get(chatNum).getFirstUser());
            isByFirst = true;
        }

        document.put("byfirst", isByFirst);
        document.put("type", "text");

        addItemTask.execute(document);
        HomeActivity.user.getChats().get(chatNum).getTexts().add(new Text(chat.getTexts().size(),
                todayDate, todayTime, text.getText().toString(), false, isByFirst, TextType.TEXT));

        text.setText("");
        refreshAdapter();
    }
}