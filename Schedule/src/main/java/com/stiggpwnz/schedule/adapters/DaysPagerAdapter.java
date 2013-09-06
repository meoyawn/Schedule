package com.stiggpwnz.schedule.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.stiggpwnz.schedule.fragments.DayFragment;

/**
 * Created by stiggpwnz on 30.08.13
 */
public class DaysPagerAdapter extends FragmentPagerAdapter {

    private String[][] lessons;
    private String[] titles;
    private DayFragment[] fragments;

    public DaysPagerAdapter(FragmentManager fm, String[][] lessons, String[] titles) {
        super(fm);
        this.lessons = lessons;
        this.titles = titles;
        this.fragments = new DayFragment[lessons.length];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int i) {
        return DayFragment.newInstance(lessons[i]);
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

    public void setLessons(String[][] lessons) {
        this.lessons = lessons;
        for (int i = 0; i < getCount(); i++) {
            if (fragments[i] != null) {
                fragments[i].setLessons(lessons[i]);
            }
        }
    }
}
