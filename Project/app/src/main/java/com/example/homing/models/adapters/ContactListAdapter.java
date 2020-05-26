package com.example.homing.models.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homing.R;
import com.example.homing.models.classes.Chat;
import com.example.homing.models.classes.User;
import com.example.homing.views.activities.ChatActivity;
import com.example.homing.views.activities.HomeActivity;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {
    private Context context;
    private List<User> users;
    private final String invitationMessage = "Hello!\n" + HomeActivity.user.getName() +
            " is inviting you to chat on Homing!\n Download the app from Google Play";

    public ContactListAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_row, parent, false);

        return new ContactListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactListViewHolder holder, final int position) {
        holder.name.setText(users.get(position).getName());

        if (users.get(position).getStatus().equals("-1")) {
            holder.button.setText(context.getString(R.string.invite));
        } else {
            holder.button.setText(context.getString(R.string.chat));
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.button.getText().toString().equals(context.getString(R.string.invite))) {
                    inviteContact(users.get(position).getPhone());
                } else {
                    addContact(users.get(position));
                }
            }
        });
    }

    private void inviteContact(String phone) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phone));
        intent.putExtra("sms_body", invitationMessage);
        context.startActivity(intent);
    }

    private void addContact(User user) {
        boolean isExisting = false;
        int position = -1;

        for (Chat chat : HomeActivity.user.getChats()) {
            if (chat.getFirstUser().equals(user.getPhone()) || chat.getSecondUser().equals(user.getPhone())) {
                isExisting = true;
                position = HomeActivity.user.getChats().indexOf(chat);
                break;
            }
        }

        if (isExisting) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("Chat", position);
            intent.putExtra("Friend", user.getName());

            context.startActivity(intent);
        } else {
            Chat chat = new Chat(HomeActivity.user.getPhone() + user.getPhone(), null);
            HomeActivity.user.getChats().add(chat);

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("Chat", HomeActivity.user.getChats().size() - 1);
            intent.putExtra("Friend", user.getName());

            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ContactListViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public AppCompatButton button;

        public ContactListViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contactNameText);
            button = itemView.findViewById(R.id.interactionButton);
        }
    }
}
