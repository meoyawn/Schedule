package com.stiggpwnz.schedule.fragments.base;

import android.os.Bundle;
import android.view.View;

public abstract class RetainedFragment extends BaseFragment implements RetainedInterface {

    public boolean isCreated() {
        return getArguments().getBoolean("created", false);
    }

    public void setCreated() {
        getArguments().putBoolean("created", true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isCreated()) {
            return;
        }
        onFirstCreated(view);
        setCreated();
    }
}
