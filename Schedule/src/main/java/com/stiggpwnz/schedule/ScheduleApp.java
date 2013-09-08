package com.stiggpwnz.schedule;

import android.app.Application;
import android.app.PendingIntent;

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
        objectGraph = ObjectGraph.create(new DependenciesModule(this));
        super.onCreate();
        if (PendingIntent.getService(this, 0, NotifierService.newInstance(this, 0), PendingIntent.FLAG_NO_CREATE) == null) {
            new BootCompletedReceiver().onReceive(this, null);
        }
    }
}
