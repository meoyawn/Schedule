package com.stiggpwnz.schedule;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Adel Nizamutdinov on 07.09.13
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    public static void main(String[] args) throws IOException, SQLException {
        File configFile = new File(System.getProperty("user.dir"), "Schedule/src/main/res/raw/ormlite_config.txt");
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        writeConfigFile(configFile, DatabaseHelper.CLASSES);
    }
}
