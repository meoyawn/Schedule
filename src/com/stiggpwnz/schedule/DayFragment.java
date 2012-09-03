package com.stiggpwnz.schedule;

import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;

public class DayFragment extends SherlockListFragment {

	public static final String TODAY = "today";
	public static final String LESSONS = "lessons";

	private LessonsAdapter adapter;

	public DayFragment() {

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String[] lessons = getArguments().getStringArray(LESSONS);
		boolean today = getArguments().getBoolean(TODAY, false);
		adapter = new LessonsAdapter(getSherlockActivity(), lessons, today);
		setListAdapter(adapter);
	}

	public void setLessons(String[] lessons) {
		adapter.setLessons(lessons);
		getArguments().putStringArray(LESSONS, lessons);
	}

	public void updateLesson() {
		adapter.updateLesson();
	}

	public void setToday(boolean today) {
		adapter.setToday(today);
		getArguments().putBoolean(TODAY, today);
	}

}