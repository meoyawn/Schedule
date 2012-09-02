package com.stiggpwnz.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class DaysAdapter extends FragmentPagerAdapter {

	public static final int DAYS_NUMBER = 6;

	private Context context;
	private String[][] lessons;
	private DayFragment[] fragments;

	private int day;

	public DaysAdapter(Context context, FragmentManager fm, int day, String[][] lessons) {
		super(fm);
		this.context = context;
		this.day = day;
		this.lessons = lessons;
		this.fragments = new DayFragment[DAYS_NUMBER];
	}

	public void updateLesson() {
		if (fragments[day] != null)
			fragments[day].updateLesson();
	}

	public void setDay(int day) {
		if (this.day != day) {
			this.day = day;
			for (int i = 0; i < DAYS_NUMBER; i++)
				if (fragments[i] != null)
					fragments[i].setToday(i == day ? true : false);
		}
	}

	@Override
	public Fragment getItem(int day) {
		Log.d("tag", "getting item: " + day);

		Bundle args = new Bundle();
		args.putStringArray(DayFragment.LESSONS, lessons[day]);
		if (this.day == day)
			args.putBoolean(DayFragment.TODAY, true);

		DayFragment fragment = new DayFragment();
		fragment.setArguments(args);
		fragments[day] = fragment;
		return fragment;
	}

	public void setLessons(String[][] lessons) {
		this.lessons = lessons;
		for (int i = 0; i < DAYS_NUMBER; i++)
			if (fragments[i] != null)
				fragments[i].setLessons(lessons[i]);
	}

	@Override
	public int getCount() {
		return DAYS_NUMBER;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return context.getResources().getStringArray(R.array.days)[position];
	}

}