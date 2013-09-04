package com.stiggpwnz.schedule.fragments.base;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.stiggpwnz.schedule.MultiThreadedBus;

import javax.inject.Inject;

import butterknife.Views;

public class BaseFragment extends SherlockFragment implements FragmentInterface {

    @Inject
    protected MultiThreadedBus bus;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Views.inject(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Views.reset(this);
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
    }

    @Override
    public void runOnBackgroundThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }
}
