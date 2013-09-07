package com.stiggpwnz.schedule;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adel Nizamutdinov on 01.09.13
 */
@DatabaseTable
public class Group implements Comparable<Group> {

    @DatabaseField(id = true) public String name;
    @DatabaseField public int column;
    @DatabaseField public boolean isFavourite;

    public Group() {

    }

    public Group(int column, String name) {
        this.column = column;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Group another) {
        if (isFavourite && !another.isFavourite) {
            return -1;
        }
        if (!isFavourite && another.isFavourite) {
            return 1;
        }
        return 0;
    }
}
