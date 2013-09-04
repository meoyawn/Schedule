package com.stiggpwnz.schedule.fragments.base;

public interface FragmentInterface {

    public void runOnUiThread(Runnable runnable);

    public void runOnBackgroundThread(Runnable runnable);
}
