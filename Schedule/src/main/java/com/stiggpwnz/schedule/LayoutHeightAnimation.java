package com.stiggpwnz.schedule;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class LayoutHeightAnimation extends Animation {

    View view;
    int targetHeight;
    int initialHeight;
    boolean width;

    public LayoutHeightAnimation(View view) {
        this.view = view;
        setDuration(view.getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    public LayoutHeightAnimation from(int value) {
        initialHeight = value;
        return this;
    }

    public LayoutHeightAnimation to(int value) {
        targetHeight = value;
        return this;
    }

    public LayoutHeightAnimation setWidth(boolean width) {
        this.width = width;
        return this;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int value;
        if (targetHeight > initialHeight) {
            value = (int) ((targetHeight - initialHeight) * interpolatedTime + initialHeight);
        } else {
            value = (int) (initialHeight - (initialHeight - targetHeight) * interpolatedTime);
        }
        if (!width) {
            view.getLayoutParams().height = value;
        } else {
            view.getLayoutParams().width = value;
        }
        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}