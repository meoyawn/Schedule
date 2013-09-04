package com.stiggpwnz.schedule.fragments.base;

import android.os.Bundle;

public abstract class RetainedProgressFragment extends BaseProgressFragment implements RetainedInterface {

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
            onFirstCreated(getView());
            created = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("created", created);
    }
}
