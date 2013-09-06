package com.stiggpwnz.schedule.fragments.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devspark.progressfragment.SherlockProgressFragment;
import com.stiggpwnz.schedule.MultiThreadedBus;
import com.stiggpwnz.schedule.Persistence;
import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;
import timber.log.Timber;

public abstract class BaseProgressFragment extends SherlockProgressFragment {

    @Inject protected MultiThreadedBus bus;
    @Inject protected Timber timber;
    @Inject protected Persistence persistence;

    @InjectView(R.id.textErrorMessage) TextView errorMessage;
    @InjectView(R.id.buttonRetry) protected Button retryButton;

    @OnClick(R.id.buttonRetry)
    void retry() {
        onRetryClick();
    }

    protected abstract void onRetryClick();

    @Override
    public void setEmptyText(int resId) {
        errorMessage.setText(resId);
    }

    @Override
    public void setEmptyText(CharSequence text) {
        errorMessage.setText(text);
    }

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
    public ViewGroup getContentView() {
        return (ViewGroup) super.getContentView();
    }
}
