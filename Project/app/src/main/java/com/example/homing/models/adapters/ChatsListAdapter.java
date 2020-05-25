package com.example.homing.models.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homing.R;
import com.example.homing.models.classes.Chat;
import com.example.homing.models.classes.Text;
import com.example.homing.models.classes.User;
import com.example.homing.models.helpers.DynamoHelper;
import com.example.homing.models.helpers.GetItemTask;
import com.example.homing.views.activities.ChatActivity;
import com.example.homing.views.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatsListViewHolder> {
    private Context context;
    private List<Chat> chats;

    public ChatsListAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_row, parent, false);

        return new ChatsListViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatsListViewHolder holder, final int position) {
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Chat", position);
                context.startActivity(intent);
            }
        });

        String userID = chats.get(position).getId().substring(0, 13);
        boolean isFriendFirst = true;
        Text lastText = chats.get(position).getTexts().get(chats.get(position).getTexts().size() - 1);

        if (userID.equals(HomeActivity.user.getPhone())) {
            userID = chats.get(position).getId().substring(13);
            isFriendFirst = false;
        }

        User friend = getFriend(userID);
        holder.friendName.setText(friend.getName());
        holder.textDateTime.setText(lastText.getTime() + " ● " + lastText.getDate());

        String lastTextContent = "";

        switch (lastText.getType()) {
            case VOICE_NOTE:
                lastTextContent = "Voice Record";
                break;

            case IMAGE:
                lastTextContent = "Media File";
                break;

            default:
                lastTextContent = lastText.getContent();

                if (lastTextContent.length() > 20) {
                    lastTextContent = lastTextContent.substring(0, 20) + "...";
                }
                break;
        }

        if ((lastText.getByFirst() && isFriendFirst) || (!isFriendFirst && !lastText.getByFirst())) {
            if (!lastText.getSeen()) {
                holder.seenStatusImage.setVisibility(View.VISIBLE);
                holder.seenStatusImage.setImageResource(R.drawable.notification);
            } else {
                holder.seenStatusImage.setVisibility(View.INVISIBLE);
                holder.seenStatusImage.getLayoutParams().width = 0;
            }
        } else {
            holder.seenStatusImage.setVisibility(View.VISIBLE);

            if (!lastText.getSeen()) {
                holder.seenStatusImage.setImageResource(R.drawable.unseen);
            } else {
                holder.seenStatusImage.setImageResource(R.drawable.seen);
            }
        }

        holder.textPreview.setText(lastTextContent);

        //if (!userList.get(position).getUserimage().equals("default")){
        //    Picasso.get().load(userList.get(position).getUserimage())
        //            .into(holder.friendImage);
        //}
    }

    private User getFriend(String id) {
        List<String> attributes = new ArrayList<>();
        attributes.add("User_ID");
        attributes.add("name");
        attributes.add("birthdate");
        attributes.add("status");
        attributes.add("picture");

        GetItemTask getItemTask = new GetItemTask(context, attributes, id);
        getItemTask.execute(DynamoHelper.getINSTANCE(context).getTables().get(0));

        try {
            return (User)getItemTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatsListViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView friendImage;
        private LinearLayout card;
        private TextView friendName, textPreview, textDateTime;
        private AppCompatImageView seenStatusImage;

        ChatsListViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.cardLayout);
            friendImage = itemView.findViewById(R.id.contactImage);
            friendName = itemView.findViewById(R.id.contactNameText);
            textPreview = itemView.findViewById(R.id.chatPreviewText);
            seenStatusImage = itemView.findViewById(R.id.seenStatusImage);
            textDateTime = itemView.findViewById(R.id.chatDateText);
        }
    }
}
