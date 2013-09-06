package com.stiggpwnz.schedule.fragments.base;

import android.os.Bundle;

public abstract class RetainedProgressFragment extends BaseProgressFragment {

    private boolean created;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            created = savedInstanceState.getBoolean("created", false);
        }
        if (!created) {
            onFirstCreated();
            created = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("created", created);
    }

    public abstract void onFirstCreated();

}
