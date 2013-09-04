package com.stiggpwnz.schedule;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by stiggpwnz on 30.08.13.
 */
@Singleton
public class MultiThreadedBus extends Bus {

    final Handler handler;

    @Inject
    public MultiThreadedBus(Handler handler) {
        super(ThreadEnforcer.ANY);
        this.handler = handler;
    }

    public void postOnUiThread(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            post(event);
        } else {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

    public void postOnBackgroundThread(final Object event) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            post(event);
        } else {
            new Thread() {

                @Override
                public void run() {
                    post(event);
                }
            }.start();
        }
    }
}
