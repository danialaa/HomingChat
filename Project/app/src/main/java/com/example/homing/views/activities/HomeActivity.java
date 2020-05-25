package com.example.homing.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
                        //logout dialog

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
