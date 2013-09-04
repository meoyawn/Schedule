package com.stiggpwnz.schedule.activities;

import android.os.Bundle;
import android.os.Looper;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.frankiesardo.icepick.bundle.Bundles;
import com.stiggpwnz.schedule.MultiThreadedBus;
import com.stiggpwnz.schedule.Persistence;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

import butterknife.Views;
import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by stiggpwnz on 30.08.13.
 */
public abstract class BaseActivity extends SherlockFragmentActivity {

    @Inject protected MultiThreadedBus bus;
    @Inject protected Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApp.getObjectGraph().inject(this);
        Bundles.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        Views.inject(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundles.saveInstanceState(this, outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    protected void runOnBackgroundThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}
