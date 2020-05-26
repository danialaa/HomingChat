package com.example.homing.models.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homing.R;
import com.example.homing.models.classes.Text;
import com.example.homing.views.activities.ChatActivity;
import com.example.homing.views.activities.HomeActivity;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private String firstUser;
    private List<Text> texts;
    private boolean isMine = false;
    private final int RIGHT = 1, LEFT = 0;

    public ChatAdapter(Context context, String firstUser) {
        this.context = context;
        this.firstUser = firstUser;
        this.texts = HomeActivity.user.getChats().get(ChatActivity.chatNum).getTexts();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.self_text_row, parent, false);
            isMine = true;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.friend_text_row, parent, false);
            isMine = false;
        }

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.contentText.setText(texts.get(position).getContent());
        holder.dateText.setText(texts.get(position).getTime() + " ● " + texts.get(position).getDate());

        if (isMine) {
            if (texts.get(position).getSeen()) {
                holder.seenImage.setImageResource(R.drawable.seen);
            } else {
                holder.seenImage.setImageResource(R.drawable.unseen);
            }
        }
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (texts.get(position).getByFirst() && firstUser.equals(HomeActivity.user.getPhone())) {
            return RIGHT;
        } else if (!texts.get(position).getByFirst() && !firstUser.equals(HomeActivity.user.getPhone())) {
            return RIGHT;
        } else {
            return LEFT;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView contentText, dateText;
        public AppCompatImageView seenImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            contentText = itemView.findViewById(R.id.contentText);
            dateText = itemView.findViewById(R.id.chatDateText);

            try {
                seenImage = itemView.findViewById(R.id.seenImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
