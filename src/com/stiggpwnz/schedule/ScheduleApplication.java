package com.stiggpwnz.schedule;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ScheduleApplication extends Application {

	private static final String GROUP_NAME = "group name";

	private SharedPreferences prefs;
	private Sheet sheet;
	private Row row;

	private String faculty;
	private String groupName;
	private int group;

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
		if (faculty == null)
			faculty = prefs.getString(PreferenceActivity.FACULTY, null);
		return faculty;
	}

	private int getGroup() {
		if (group == 0) {
			String number = prefs.getString(PreferenceActivity.GROUP, null);
			if (number != null)
				group = Integer.valueOf(number);
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

}
