package com.example.homing.models.helpers;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private Listener listener;

    @Override
    public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (listener == null) {
            return true;
        }

        listener.onScroll(distanceX, distanceY);

        return true;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onScroll(float differenceX, float differenceY);
    }
}
