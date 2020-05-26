package com.example.homing.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.homing.R;
import com.example.homing.models.adapters.ContactListAdapter;
import com.example.homing.models.classes.User;
import com.example.homing.models.helpers.DynamoHelper;
import com.example.homing.controllers.GetItemTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddChatActivity extends AppCompatActivity {
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        List<User> users = new ArrayList<>();
        users = fillUserList(users);

        recycler = findViewById(R.id.contactsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(new ContactListAdapter(this, users));
    }

    private List<User> fillUserList(List<User> users) {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (phoneCursor.moveToNext()) {
                        String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        users.add(new User(name, phone, "", "", "", null));
                        users.get(users.size() - 1).setPhone(users.get(users.size() - 1).getPhone().replaceAll("\\s+", ""));

                        Log.d("Add Chat Activity", "Contact name = " + name + ", number = " + users.get(users.size() - 1).getPhone());
                    }

                    phoneCursor.close();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        users = fixStatus(users);
        return users;
    }

    private List<User> fixStatus(List<User> users) {
        for (User user : users) {
            User userInDB = null;

            List<String> attributes = new ArrayList<>();
            attributes.add("User_ID");
            attributes.add("name");
            attributes.add("status");
            GetItemTask getItemTask = new GetItemTask(AddChatActivity.this, attributes,
                    user.getPhone());
            getItemTask.execute(DynamoHelper.getINSTANCE(AddChatActivity.this).getTables().get(0));

            try {
                userInDB = (User)getItemTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (userInDB == null) {
                user.setStatus("-1");
            } else {
                user.setName(userInDB.getName());
            }
        }

        return users;
    }
}
