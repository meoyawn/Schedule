package com.stiggpwnz.schedule.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.stiggpwnz.schedule.Lesson;
import com.stiggpwnz.schedule.ScheduleApp;
import com.stiggpwnz.schedule.fragments.DayFragment;

import javax.inject.Inject;

/**
 * Created by stiggpwnz on 30.08.13
 */
public class DaysPagerAdapter extends FragmentPagerAdapter {

    private Lesson[][] lessons;
    private String[] titles;
    private DayFragment[] fragments;
    private int day;
    private int highlightedColor;
    private boolean evenWeek;

    @Inject Gson gson;

    public DaysPagerAdapter(FragmentManager fm, Lesson[][] lessons, String[] titles, int day, int highlightedColor, boolean evenWeek) {
        super(fm);
        this.lessons = lessons;
        this.titles = titles;
        this.fragments = new DayFragment[lessons.length];
        this.day = day;
        this.highlightedColor = highlightedColor;
        this.evenWeek = evenWeek;
        ScheduleApp.getObjectGraph().inject(this);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int i) {
        DayFragment dayFragment = DayFragment.newInstance(gson.toJson(lessons[i]), highlightedColor, evenWeek);
        if (i == day) {
            dayFragment.getArguments().putBoolean("today", true);
        }
        return dayFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        fragments[position] = (DayFragment) item;
        return item;
    }

    @Override
    public int getCount() {
        return lessons.length;
    }

    public void setDay(int day, boolean evenWeek) {
        this.day = day;
        for (int i = 0; i < getCount(); i++) {
            DayFragment fragment = fragments[i];
            if (fragment != null) {
                fragment.setToday(i == day);
                fragment.setEvenWeek(evenWeek);
            }
        }
    }

    public void setHighlightedColor(int highlightedColor) {
        this.highlightedColor = highlightedColor;
        for (DayFragment fragment : fragments) {
            if (fragment != null) {
                fragment.setHighLightedColor(highlightedColor);
            }
        }
    }

    public void setLessons(Lesson[][] lessons) {
        this.lessons = lessons;
        for (int i = 0; i < getCount(); i++) {
            if (fragments[i] != null) {
                fragments[i].setLessons(lessons[i]);
            }
        }
    }
}
