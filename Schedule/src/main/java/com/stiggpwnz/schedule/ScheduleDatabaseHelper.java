package com.stiggpwnz.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adel Nizamutdinov on 07.09.13
 */
public class ScheduleDatabaseHelper extends SQLiteOpenHelper {

    public ScheduleDatabaseHelper(Context context) {
        super(context, "schedule.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
