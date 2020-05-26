package com.example.homing.views.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.s3.transferutility.*;
import com.example.homing.R;
import com.example.homing.views.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */

public class ProfileFragment extends Fragment {
    private CircleImageView userImage;
    private AppCompatButton editButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final List<EditText> editTexts = new ArrayList<>();

        userImage = view.findViewById(R.id.userImage);
        EditText name = view.findViewById(R.id.nameEdit);
        editTexts.add(name);
        EditText phone = view.findViewById(R.id.phoneEdit);
        editTexts.add(phone);
        EditText birthdate = view.findViewById(R.id.birthdayEdit);
        editTexts.add(birthdate);
        EditText status = view.findViewById(R.id.statusEdit);
        editTexts.add(status);
        editButton = view.findViewById(R.id.editButton);

        name.setText(HomeActivity.user.getName());
        phone.setText(HomeActivity.user.getPhone());
        birthdate.setText(HomeActivity.user.getBirthdate());
        status.setText(HomeActivity.user.getStatus());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText editText : editTexts) {
                    if (editButton.getText().toString().equals(getString(R.string.edit))) {
                        editText.setFocusable(true);
                        editText.setBackgroundResource(android.R.drawable.editbox_background);
                        editButton.setText(getString(R.string.submit));
                    } else {
                        editText.setFocusable(false);
                        editText.setBackgroundResource(R.color.transparent);
                        editButton.setText(getString(R.string.edit));
                    }
                }
            }
        });

        return view;
    }
}