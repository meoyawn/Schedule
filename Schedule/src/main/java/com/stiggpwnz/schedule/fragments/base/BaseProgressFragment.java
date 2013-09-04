package com.stiggpwnz.schedule.fragments.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.view.ViewGroup;

import com.devspark.progressfragment.SherlockProgressFragment;
import com.stiggpwnz.schedule.MultiThreadedBus;
import com.stiggpwnz.schedule.Persistence;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

import butterknife.Views;
import timber.log.Timber;

public abstract class BaseProgressFragment extends SherlockProgressFragment implements FragmentInterface {

    @Inject protected MultiThreadedBus bus;
    @Inject protected Timber timber;
    @Inject protected Persistence persistence;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApp.getObjectGraph().inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCreateView(savedInstanceState);
        Views.inject(this, getView());
        setContentShownNoAnimation(true);
        onViewCreated(savedInstanceState);
    }

    protected boolean isInPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    protected abstract void onCreateView(Bundle savedInstanceState);

    protected abstract void onViewCreated(Bundle savedInstanceState);

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
        Views.reset(this);
        super.onDestroyView();
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    @Override
    public void runOnBackgroundThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    @Override
    public ViewGroup getContentView() {
        return (ViewGroup) super.getContentView();
    }
}
