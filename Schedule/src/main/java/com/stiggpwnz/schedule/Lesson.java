package com.stiggpwnz.schedule;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adel Nizamutdinov on 08.09.13
 */
@DatabaseTable
public class Lesson {

    private static final String ODD_PREFIX = "н/н";
    private static final String EVEN_PREFIX = "ч/н";

    @DatabaseField(id = true) public String id;
    @DatabaseField public String odd;
    @DatabaseField public String even;

    public Lesson() {

    }

    public Lesson(String id) {
        this.id = id;
    }

    public void set(String value) {
        if (value.contains(ODD_PREFIX)) {
            odd = value.replace(ODD_PREFIX, "");
        } else if (value.contains(EVEN_PREFIX)) {
            even = value.replace(EVEN_PREFIX, "");
        } else {
            odd = value;
            even = value;
        }
    }

    public String get(boolean evenWeek) {
        return evenWeek ? even : odd;
    }

    public boolean isFromDb() {
        return odd != null && even != null;
    }

    public boolean isTheSame() {
        return odd != null && odd.equals(even) || odd == null && even == null;
    }
}
