package com.example.homing.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.example.homing.R;
import com.example.homing.models.adapters.ChatsListAdapter;
import com.example.homing.models.helpers.CognitoHelper;
import com.example.homing.views.activities.HomeActivity;

public class ChatsListFragment extends Fragment {
    private TextView nameText;
    private RecyclerView recyclerView;

    public ChatsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_list, container, false);

        nameText = view.findViewById(R.id.nameText);
        recyclerView = view.findViewById(R.id.chatListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        refreshAdapter();

        nameText.setText(HomeActivity.user.getName());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    private void refreshAdapter() {
        recyclerView.setAdapter(new ChatsListAdapter(getActivity(), HomeActivity.user.getChats()));
    }
}
