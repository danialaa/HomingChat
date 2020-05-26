package com.example.homing.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.homing.R;
import com.example.homing.models.classes.User;
import com.example.homing.views.fragments.ChatsListFragment;
import com.example.homing.views.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    public static User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        goToFragment(new ChatsListFragment());

        user = (User)getIntent().getSerializableExtra("User");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_chats:
                        goToFragment(new ChatsListFragment());
                        break;

                    case R.id.action_profile:
                        goToFragment(new ProfileFragment());
                        break;

                    case R.id.action_logout:
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                        alertDialog.setTitle(getString(R.string.logout_dialog_title));
                        alertDialog.setMessage(getString(R.string.logout_dialog_message));
                        alertDialog.setPositiveButton(getString(R.string.bye), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            }
                        });
                        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });

                        AlertDialog dialog = alertDialog.create();
                        dialog.show();

                        break;
                }

                return true;
            }
        });
    }

    private void goToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }
}
