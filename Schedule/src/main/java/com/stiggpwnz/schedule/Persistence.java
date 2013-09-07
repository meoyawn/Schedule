package com.stiggpwnz.schedule;

import android.content.SharedPreferences;

import javax.inject.Singleton;

@Singleton
public class Persistence {

    private final SharedPreferences prefs;

    public Persistence(SharedPreferences prefs) {
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
}
