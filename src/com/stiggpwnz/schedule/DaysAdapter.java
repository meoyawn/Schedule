package com.stiggpwnz.schedule;

import java.util.Calendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DaysAdapter extends FragmentStatePagerAdapter {

	public static final int DAYS_NUMBER = 6;
	private static final String ODD_WEEK = "í/í";
	private static final String EVEN_WEEK = "÷/í";

	private static final int ODD = 1;
	private static final int EVEN = 0;

	private static final int FIRST_LESSON_CELL = 2;

	private Context context;
	private Row row;

	private int week;
	private int day;
	private int hour;
	private int minute;

	public DaysAdapter(Context context, FragmentManager fm, Row row) {
		super(fm);
		this.context = context;
		this.row = row;
	}

	public void updateTime() {
		Calendar calendar = Calendar.getInstance();
		day = calendar.get(Calendar.DAY_OF_WEEK) + 5;
		week = calendar.get(Calendar.WEEK_OF_YEAR);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		if (day == 5 && hour >= 19) {
			day += 2;
			week++;
		} else if (day == 6) {
			day++;
			week++;
		} else if (hour >= 19)
			day++;
		week = week % 2;
		day = day % 7;
	}

	public int getDay() {
		return day;
	}

	public void setRow(Row row) {
		this.row = row;
	}

	@Override
	public Fragment getItem(int day) {
		Bundle args = new Bundle();
		args.putStringArray(DayFragment.LESSONS, getLessions(day));
		if (this.day == day)
			args.putInt(DayFragment.LESSON, calculateCurrentLesson(hour, minute));

		Fragment fragment = new DayFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	private String[] getLessions(int day) {
		String[] lessons = new String[LessonsAdapter.LESSONS_NUMBER];
		for (int lesson = 0; lesson < LessonsAdapter.LESSONS_NUMBER; lesson++) {
			int cellNum = FIRST_LESSON_CELL + day * LessonsAdapter.LESSONS_NUMBER + lesson + day;
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

	private int calculateCurrentLesson(int hour, int minute) {
		int lesson = -1;
		if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5 == DaysAdapter.DAYS_NUMBER)
			return lesson;

		// 8.30–9.50
		// 10.00–11.20
		// 11.30–12.50
		// 13.05–14.25
		// 14.40–16.00
		// 16.10–17.30
		// 17.40–19.00
		switch (hour) {
		case 8:
			if (minute >= 30)
				lesson = 0;
			break;

		case 9:
			if (minute < 50)
				lesson = 0;
			else
				lesson = 1;
			break;

		case 10:
			lesson = 1;
			break;

		case 11:
			if (minute < 20)
				lesson = 1;
			else
				lesson = 2;
			break;

		case 12:
			if (minute < 50)
				lesson = 2;
			else
				lesson = 3;
			break;

		case 13:
			lesson = 3;
			break;

		case 14:
			if (minute < 25)
				lesson = 3;
			else
				lesson = 4;
			break;

		case 15:
			lesson = 4;
			break;

		case 16:
			lesson = 5;
			break;

		case 17:
			if (minute < 30)
				lesson = 5;
			else
				lesson = 6;
			break;

		case 18:
			lesson = 6;
			break;
		}
		return lesson;
	}
}