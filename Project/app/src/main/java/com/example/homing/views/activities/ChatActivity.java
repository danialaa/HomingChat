package com.example.homing.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
                if (count > 0) {
                    sendButton.setClickable(true);
                } else {
                    sendButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        friendName.setText(getFriendName());

        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setHasFixedSize(true);
        refreshAdapter();
    }

    private void refreshAdapter() {
        chatRecycler.setAdapter(new ChatAdapter(this, HomeActivity.user.getChats().get(chatNum).getFirstUser()));
    }

    private String getFriendName() {
        User friend = new User();
        String friendID = "";

        List<String> attributes = new ArrayList<>();
        attributes.add("User_ID");
        attributes.add("name");
        attributes.add("birthdate");
        attributes.add("status");
        attributes.add("picture");

        if (HomeActivity.user.getChats().get(chatNum).getFirstUser() == HomeActivity.user.getPhone()) {
            friendID = HomeActivity.user.getChats().get(chatNum).getSecondUser();
        } else {
            friendID = HomeActivity.user.getChats().get(chatNum).getFirstUser();
        }

        GetItemTask getItemTask = new GetItemTask(ChatActivity.this, attributes, friendID);
        getItemTask.execute(DynamoHelper.getINSTANCE(ChatActivity.this).getTables().get(0));

        try {
            friend = (User)getItemTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return friend.getName();
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
        todayTime.replace(" ", "");

        document.put("Chat_ID", chat.getId());
        document.put("Text_ID", chat.getTexts().size());
        document.put("date", todayDate);
        document.put("time", todayTime);
        document.put("content", text.getText().toString());
        document.put("seen", false);

        boolean isByFirst = false;

        if (HomeActivity.user.getChats().get(chatNum).getFirstUser() == HomeActivity.user.getPhone()) {
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