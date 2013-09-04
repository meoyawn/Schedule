package com.stiggpwnz.schedule;

import java.io.Serializable;

/**
 * Created by stiggpwnz on 01.09.13.
 */
public class FileMetadata implements Serializable {

    public String name;
    public String path;
    public String group_regex;

    @Override
    public String toString() {
        return name;
    }
}
