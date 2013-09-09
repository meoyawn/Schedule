package com.stiggpwnz.schedule;

import android.app.Application;

import com.crittercism.app.Crittercism;

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
        Crittercism.initialize(this, "522e37d3a7928a7c05000003");
        new BootCompletedReceiver().onReceive(this, null);
    }
}
