package com.stiggpwnz.schedule;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.stiggpwnz.schedule.fragments.MainFragment;

import java.io.File;
import java.io.Serializable;

/**
 * Created by stiggpwnz on 01.09.13
 */
@DatabaseTable
public class FileMetadata implements Serializable {

    @DatabaseField(id = true) public String path;
    @DatabaseField public String name;
    @DatabaseField public String group_regex;

    @Override
    public String toString() {
        return name;
    }

    public File getFile(Context context) {
        return new File(Utils.getFilesDir(context), getFileName());
    }

    public String getUrl() {
        return MainFragment.DROPBOX + path;
    }

    public String getFileName() {
        return path.substring(1);
    }
}
