package com.stiggpwnz.schedule;

/**
 * Created by Adel Nizamutdinov on 01.09.13
 */
public class Group {

    public int column;
    public String name;

    public Group(int column, String name) {
        this.column = column;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
