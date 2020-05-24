package com.example.homing.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homing.R;
import com.example.homing.models.helpers.LoadTableTask;
import com.example.homing.models.helpers.MyGestureListener;

public class MainActivity extends AppCompatActivity {
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadTableTask loadTableTask = new LoadTableTask(this);
        loadTableTask.execute("Users");
        LoadTableTask loadTableTask2 = new LoadTableTask(this);
        loadTableTask2.execute("ChatTexts");

        MyGestureListener gestureListener = new MyGestureListener();
        gestureListener.setListener(new MyGestureListener.Listener() {
            @Override
            public void onScroll(float differenceX, float differenceY) {
                if (differenceX == 0 && differenceY > 1) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });

        gestureDetector = new GestureDetector(this, gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
