package com.stiggpwnz.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class ScheduleApplication extends Application {

	private static final String GROUP_NAME = "group name";

	private static final String ODD_WEEK = "í/í";
	private static final String EVEN_WEEK = "÷/í";

	private static final int ODD = 1;
	private static final int EVEN = 0;

	private static final int FIRST_LESSON_CELL = 2;

	private SharedPreferences prefs;
	private SharedPreferences saved;
	private Sheet sheet;
	private Row row;

	private String faculty;
	private String groupName;
	private int group;
	private String[][] lessons;

	private int day;
	private int week;

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Calendar calendar = Calendar.getInstance();
		day = calendar.get(Calendar.DAY_OF_WEEK) + 5;
		week = calendar.get(Calendar.WEEK_OF_YEAR);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
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

	public String[][] getLessons() {
		if (lessons == null) {
			lessons = new String[DaysAdapter.DAYS_NUMBER][];
			for (int i = 0; i < DaysAdapter.DAYS_NUMBER; i++)
				lessons[i] = loadLessons(i);
		}
		return lessons;
	}

	private String[] loadLessons(int day) {
		String[] lessons = new String[LessonsAdapter.LESSONS_NUMBER];
		for (int lesson = 0; lesson < LessonsAdapter.LESSONS_NUMBER; lesson++) {
			String value = getSavedValue(day, lesson);
			if (value == null) {
				int cellNum = FIRST_LESSON_CELL + day * LessonsAdapter.LESSONS_NUMBER + lesson + day;
				value = getRow().getCell(cellNum).getStringCellValue();
			}
			lessons[lesson] = parseOddEven(value);
		}
		return lessons;
	}

	public String getActualStringData(int day, int lesson) {
		String value = getSavedValue(day, lesson);
		if (value == null) {
			int cellNum = FIRST_LESSON_CELL + day * LessonsAdapter.LESSONS_NUMBER + lesson + day;
			value = getRow().getCell(cellNum).getStringCellValue();
		}
		return value;
	}

	public String parseOddEven(String value) {
		String shownValue = value.trim();
		if (value.startsWith(ODD_WEEK)) {
			if (value.contains(EVEN_WEEK)) {
				if (week == ODD)
					shownValue = value.substring(value.indexOf(ODD_WEEK) + 3, value.indexOf(EVEN_WEEK));
				else
					shownValue = value.substring(value.indexOf(EVEN_WEEK) + 3, value.length());
			} else {
				if (week == ODD)
					shownValue = value.substring(value.indexOf(ODD_WEEK) + 3, value.length());
				else
					shownValue = "";
			}
		} else if (value.startsWith(EVEN_WEEK)) {
			if (value.contains(ODD_WEEK)) {
				if (week == EVEN)
					shownValue = value.substring(value.indexOf(EVEN_WEEK) + 3, value.indexOf(ODD_WEEK));
				else
					shownValue = value.substring(value.indexOf(ODD_WEEK) + 3, value.length());
			} else {
				if (week == ODD)
					shownValue = "";
				else
					shownValue = value.substring(value.indexOf(EVEN_WEEK) + 3, value.length());
			}
		}
		return shownValue;
	}

	private String getSavedValue(int day, int lesson) {
		String current = String.format("%d_%d", day, lesson);
		return saved.getString(current, null);
	}

	public void saveValue(int day, int lesson, String input) {
		String current = String.format("%d_%d", day, lesson);
		Editor editor = saved.edit();
		editor.putString(current, input);
		editor.commit();
		lessons[day][lesson] = input;
	}

	private Sheet openExcelFile() {
		if (getFaculty() != null) {
			try {
				InputStream is = getResources().getAssets().open(getFaculty());
				HSSFWorkbook wb = new HSSFWorkbook(is);
				is.close();
				return wb.getSheetAt(0);
			} catch (IOException e) {

			}
		}
		return null;
	}

	public boolean isRowUpdated() {
		return row == null;
	}

	public Row getRow() {
		if (row == null && getFaculty() != null && getGroup() != 0)
			row = getSheet().getRow(getGroup());
		return row;
	}

	public String getFaculty() {
		if (faculty == null) {
			faculty = prefs.getString(PreferenceActivity.FACULTY, null);
		}
		return faculty;
	}

	private int getGroup() {
		if (group == 0) {
			String number = prefs.getString(PreferenceActivity.GROUP, null);
			if (number != null) {
				group = Integer.valueOf(number);
				String faculty_group = String.format("%s_%d", getFaculty(), group);
				saved = getSharedPreferences(faculty_group, MODE_PRIVATE);
			}
		}
		return group;
	}

	public Sheet getSheet() {
		if (sheet == null)
			sheet = openExcelFile();
		return sheet;
	}

	public void resetFaculty() {
		resetGroup();
		sheet = null;
		faculty = null;
	}

	public void resetGroup() {
		row = null;
		lessons = null;
		group = 0;
	}

	public String getGroupName() {
		if (groupName == null)
			groupName = prefs.getString(GROUP_NAME, null);
		return groupName;
	}

	public void setGroupName(String groupName) {
		if (groupName != null && !groupName.equals(this.groupName)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(GROUP_NAME, groupName);
			editor.commit();
			this.groupName = groupName;
		}
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

}
