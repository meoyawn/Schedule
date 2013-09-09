package com.stiggpwnz.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Adel Nizamutdinov on 07.09.13
 */
@Singleton
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final Class[] CLASSES = new Class[]{Group.class, Lesson.class};

    public static final int DATABASE_VERSION = 2;

    @Inject
    public DatabaseHelper(Context context) {
        super(context, "schedule.db", null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            for (Class clazz : CLASSES) {
                TableUtils.createTable(connectionSource, clazz);
            }
        } catch (SQLException ignored) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            for (Class clazz : CLASSES) {
                TableUtils.dropTable(connectionSource, clazz, true);
            }
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException ignored) {

        }
    }
}