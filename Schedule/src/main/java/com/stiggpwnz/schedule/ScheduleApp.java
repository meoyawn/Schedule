package com.stiggpwnz.schedule;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;

import dagger.ObjectGraph;

/**
 * Created by stiggpwnz on 30.08.13
 */
public class ScheduleApp extends Application {

    private static ObjectGraph objectGraph;

    public static boolean inApp;

    public static ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= 9) {
            enableStrictMode();
        }
        objectGraph = ObjectGraph.create(new DependenciesModule(this));
        super.onCreate();
        startService(new Intent(this, NotifierService.class));
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build());
        }
    }
}
