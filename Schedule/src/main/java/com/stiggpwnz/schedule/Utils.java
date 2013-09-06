package com.stiggpwnz.schedule;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import javax.inject.Singleton;

import butterknife.Views;

/**
 * Created by stiggpwnz on 31.08.13
 */
@Singleton
public class Utils {

    public static File getFilesDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return context.getExternalFilesDir(null);
        }
        return context.getFilesDir();
    }

    public static void replaceView(View container, int id, View view) {
        View old = Views.findById(container, id);
        ViewGroup parent = (ViewGroup) old.getParent();
        int index = parent.indexOfChild(old);
        parent.removeView(old);
        view.setId(id);
        parent.addView(view, index);
    }

}
