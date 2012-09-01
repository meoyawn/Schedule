package com.stiggpwnz.schedule;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DaysAdapter extends FragmentPagerAdapter {

	public static final int DAYS_NUMBER = 6;

	private static final String ODD_WEEK = "í/í";
	private static final String EVEN_WEEK = "÷/í";

	private static final int ODD = 1;
	private static final int EVEN = 0;

	private Context context;
	private Row row;

	private int week;
	private int day;
	private int lesson;

	public DaysAdapter(Context context, FragmentManager fm, Row row, int week, int day, int lesson) {
		super(fm);
		this.context = context;
		this.row = row;
		this.week = week;
		this.day = day;
		this.lesson = lesson;
	}

	@Override
	public Fragment getItem(int day) {
		Bundle args = new Bundle();
		args.putStringArray(DayFragment.LESSONS, getLessions(day, week));
		if (this.day == day)
			args.putInt(DayFragment.LESSON, lesson);

		Fragment fragment = new DayFragment();
		fragment.setArguments(args);
		return fragment;
	}

	private String[] getLessions(int day, int week) {
		String[] lessons = new String[LessonsAdapter.LESSONS_NUMBER];
		for (int lesson = 0; lesson < LessonsAdapter.LESSONS_NUMBER; lesson++) {
			int cellNum = 3 + day * LessonsAdapter.LESSONS_NUMBER + lesson + day;
			Cell cell = row.getCell(cellNum);
			String cellValue = cell.getStringCellValue();
			String shownValue = cellValue;
			if (cellValue.startsWith(ODD_WEEK)) {
				if (cellValue.contains(EVEN_WEEK)) {
					if (week == ODD)
						shownValue = cellValue.substring(cellValue.indexOf(ODD_WEEK) + 3, cellValue.indexOf(EVEN_WEEK));
					else
						shownValue = cellValue.substring(cellValue.indexOf(EVEN_WEEK) + 3, cellValue.length());
				} else {
					if (week == ODD)
						shownValue = cellValue.substring(cellValue.indexOf(ODD_WEEK) + 3, cellValue.length());
					else
						shownValue = "";
				}
			} else if (cellValue.startsWith(EVEN_WEEK)) {
				if (cellValue.contains(ODD_WEEK)) {
					if (week == EVEN)
						shownValue = cellValue.substring(cellValue.indexOf(EVEN_WEEK) + 3, cellValue.indexOf(ODD_WEEK));
					else
						shownValue = cellValue.substring(cellValue.indexOf(ODD_WEEK) + 3, cellValue.length());
				} else {
					if (week == ODD)
						shownValue = "";
					else
						shownValue = cellValue.substring(cellValue.indexOf(EVEN_WEEK) + 3, cellValue.length());
				}
			}
			lessons[lesson] = shownValue;
		}
		return lessons;
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