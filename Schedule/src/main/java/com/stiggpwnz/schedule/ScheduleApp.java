package com.stiggpwnz.schedule;

import android.app.Application;

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
        super.onCreate();
        objectGraph = ObjectGraph.create(new DependenciesModule(this));
        new BootCompletedReceiver().onReceive(this, null);
    }
}
