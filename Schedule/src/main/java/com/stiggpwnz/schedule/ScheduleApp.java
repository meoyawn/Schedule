package com.stiggpwnz.schedule;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created by stiggpwnz on 30.08.13.
 */
public class ScheduleApp extends Application {

    private static ObjectGraph objectGraph;

    public static ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override
    public void onCreate() {
        objectGraph = ObjectGraph.create(new DependenciesModule(this));
        super.onCreate();
    }
}
