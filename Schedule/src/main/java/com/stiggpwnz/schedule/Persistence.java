package com.stiggpwnz.schedule;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

@Singleton
public class Persistence {

    private final Context context;
    private final SharedPreferences prefs;

    public Persistence(Context context, SharedPreferences prefs) {
        this.context = context;
        this.prefs = prefs;
    }

    public FileMetadata getLastFileMetadata() {
        FileMetadata metadata = new FileMetadata();
        metadata.path = getString("path");
        metadata.name = getString("name");
        metadata.group_regex = getString("regex");
        return metadata;
    }

    public void setLastSelectedMetadata(final FileMetadata metadata) {
        new Thread() {

            @Override
            public void run() {
                prefs.edit().putString("path", metadata.path)
                        .putString("name", metadata.name)
                        .putString("regex", metadata.group_regex)
                        .commit();
            }
        }.start();
    }

    private String getString(String key) {
        return prefs.getString(key, null);
    }

    public void setLastSelectedGroup(final String faculty, final int group) {
        new Thread() {

            @Override
            public void run() {
                prefs.edit().putInt(faculty, group).commit();
            }
        }.start();
    }

    public int getLastSelectedGroup(String faculty) {
        return prefs.getInt(faculty, -1);
    }

    public int getMainColor() {
        return prefs.getInt("main color", context.getResources().getColor(R.color.green_light_icon));
    }

    public void setMainColor(final int color) {
        new Thread() {

            @Override
            public void run() {
                prefs.edit().putInt("main color", color).commit();
            }
        }.start();
    }

    public int getSecondaryColor() {
        return prefs.getInt("secondary color", context.getResources().getColor(R.color.yellow_light_icon));
    }

    public void setSecondaryColor(final int color) {
        new Thread() {

            @Override
            public void run() {
                prefs.edit().putInt("secondary color", color).commit();
            }
        }.start();
    }

    public boolean shouldNotify() {
        return prefs.getBoolean(context.getString(R.string.should_notify), true);
    }
}
