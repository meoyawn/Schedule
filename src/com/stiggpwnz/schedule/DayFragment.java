package com.stiggpwnz.schedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class DayFragment extends SherlockListFragment {

	public static interface Listener {
		public String getActualStringData(int day, int lesson);
	}

	public static final String TODAY = "today";
	public static final String LESSONS = "lessons";
	public static final String DAY = "day";

	private LessonsAdapter adapter;
	private Listener listener;
	private int day;

	public DayFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (Listener) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		String[] lessons = getArguments().getStringArray(LESSONS);
		boolean today = getArguments().getBoolean(TODAY, false);
		day = getArguments().getInt(DAY);

		adapter = new LessonsAdapter(getSherlockActivity(), lessons, today);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String actualStringData = listener.getActualStringData(day, position).trim();
		EditLessonFragment fragment = EditLessonFragment.newInstance(day, position, actualStringData);
		fragment.show(getSherlockActivity().getSupportFragmentManager(), "edit");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
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

	public void setLesson(int lesson, String input) {
		adapter.setLesson(lesson, input);
	}

}