package com.example.homing.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homing.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatsListViewHolder> {
    private Context context;
    //list

    public ChatsListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ChatsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_row, parent, false);

        return new ChatsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsListViewHolder holder, int position) {
        //if (!userList.get(position).getUserimage().equals("default")){
        //    Picasso.get().load(userList.get(position).getUserimage())
        //            .into(holder.friendImage);
        //}
    }

    @Override
    public int getItemCount() {
        return 0; //list count
    }

    class ChatsListViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView friendImage;
        private TextView friendName, textPreview;
        private AppCompatImageView notificationImage;

        ChatsListViewHolder(@NonNull View itemView) {
            super(itemView);

            friendImage = itemView.findViewById(R.id.contactImage);
            friendName = itemView.findViewById(R.id.contactNameText);
            textPreview = itemView.findViewById(R.id.chatPreviewText);
            notificationImage = itemView.findViewById(R.id.chatNotificationImage);
        }
    }
}
