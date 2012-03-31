package com.android.stiggpwnz.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class ScheduleActivity extends Activity {

	protected static final int DIALOG_ABOUT = 0;
	protected static final int DIALOG_CHOOSE = 1;

	protected static final int GROUPS_NUMBER = 102;
	protected static final String GROUP = "group";

	protected static final int ODD = 0;
	protected static final int EVEN = 1;
	protected static final String ODD_STRING = "í/í";
	protected static final String EVEN_STRING = "÷/í";
	protected static final String TAG = "motherfucker";

	protected String[] titles = new String[6];
	protected String[] groups = new String[GROUPS_NUMBER];
	protected int[] groupsRows = new int[GROUPS_NUMBER];
	protected Sheet sheet;
	protected Row row;
	protected SharedPreferences prefs;
	protected int rownum;
	protected int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pages);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		titles[0] = getString(R.string.monday);
		titles[1] = getString(R.string.tuesday);
		titles[2] = getString(R.string.wednesday);
		titles[3] = getString(R.string.thursday);
		titles[4] = getString(R.string.friday);
		titles[5] = getString(R.string.saturday);

		sheet = openExcel();
		rownum = prefs.getInt(GROUP, 0);

		if (rownum < 2)
			showDialog(DIALOG_CHOOSE);
		else
			initUi();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (rownum < 2)
			showDialog(DIALOG_CHOOSE);
		else
			initUi();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuAbout:
			showDialog(DIALOG_ABOUT);
			return true;
		case R.id.menuDialog:
			showDialog(DIALOG_CHOOSE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog = new Dialog(this);
		dialog.setCanceledOnTouchOutside(true);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		switch (id) {
		case DIALOG_ABOUT:
			dialog.setContentView(R.layout.about);
			break;
		case DIALOG_CHOOSE:
			dialog.setContentView(R.layout.options);

			int rownum = sheet.getLastRowNum();
			int j = 0;
			for (int i = 2; i < rownum; i++) {
				Row row = sheet.getRow(i);
				Cell cell = row.getCell(1);
				String string = cell.getStringCellValue();
				if (j >= GROUPS_NUMBER)
					break;
				else if (string.charAt(0) == '9') {
					groups[j] = string;
					groupsRows[j] = i;
					j++;
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			Spinner spinner = (Spinner) dialog.findViewById(R.id.pickerGroup);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					ScheduleActivity.this.position = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			Button button = (Button) dialog.findViewById(R.id.btnOK);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences.Editor editor = ScheduleActivity.this.prefs.edit();
					editor.putInt(GROUP, groupsRows[ScheduleActivity.this.position]);
					editor.commit();
					ScheduleActivity.this.rownum = groupsRows[ScheduleActivity.this.position];
					if (ScheduleActivity.this.rownum >= 2) {
						initUi();
						dialog.dismiss();
					}
				}
			});

			break;
		default:
			return null;
		}
		return dialog;
	}

	protected void initUi() {
		row = sheet.getRow(rownum);
		ArrayList<View> mPages = new ArrayList<View>();
		ViewPager mPager;
		TitlePageIndicator mTitleIndicator;
		LayoutInflater inflater = LayoutInflater.from(this);

		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int weekNum = calendar.get(Calendar.WEEK_OF_YEAR);

		dayOfWeek += 5;
		int dayOfWeekReal = dayOfWeek;

		if (dayOfWeek == 5 && hours >= 19) {
			dayOfWeek += 2;
			weekNum++;
		} else if (dayOfWeek == 6) {
			dayOfWeek++;
		} else if (hours >= 19)
			dayOfWeek++;

		weekNum = weekNum % 2;
		dayOfWeek = dayOfWeek % 7;
		dayOfWeekReal = dayOfWeekReal % 7;

		for (int j = 0; j < 6; j++) {

			View day = inflater.inflate(R.layout.schedule, null);

			TextView[] text = new TextView[7];
			text[0] = (TextView) day.findViewById(R.id.text1);
			text[1] = (TextView) day.findViewById(R.id.text2);
			text[2] = (TextView) day.findViewById(R.id.text3);
			text[3] = (TextView) day.findViewById(R.id.text4);
			text[4] = (TextView) day.findViewById(R.id.text5);
			text[5] = (TextView) day.findViewById(R.id.text6);
			text[6] = (TextView) day.findViewById(R.id.text7);

			TableRow[] rows = new TableRow[7];
			rows[0] = (TableRow) day.findViewById(R.id.row1);
			rows[1] = (TableRow) day.findViewById(R.id.row2);
			rows[2] = (TableRow) day.findViewById(R.id.row3);
			rows[3] = (TableRow) day.findViewById(R.id.row4);
			rows[4] = (TableRow) day.findViewById(R.id.row5);
			rows[5] = (TableRow) day.findViewById(R.id.row6);
			rows[6] = (TableRow) day.findViewById(R.id.row7);

			for (int i = 0; i < 7; i++) {
				int cellnum = 2 + j * 7 + i + j;
				Cell cell = row.getCell(cellnum);
				String string = cell.getStringCellValue();
				String newString = string;

				if (string.startsWith(ODD_STRING)) {
					if (string.contains(EVEN_STRING)) {
						if (weekNum == ODD) {
							newString = string.substring(string.indexOf(ODD_STRING) + 3, string.indexOf(EVEN_STRING));
						} else {
							newString = string.substring(string.indexOf(EVEN_STRING) + 3, string.length());
						}
					} else {
						if (weekNum == ODD) {
							newString = string.substring(string.indexOf(ODD_STRING) + 3, string.length());
						} else {
							newString = "";
						}
					}
				} else if (string.startsWith(EVEN_STRING)) {
					if (string.contains(ODD_STRING)) {
						if (weekNum == EVEN) {
							newString = string.substring(string.indexOf(EVEN_STRING) + 3, string.indexOf(ODD_STRING));
						} else {
							newString = string.substring(string.indexOf(ODD_STRING) + 3, string.length());
						}
					} else {
						if (weekNum == ODD) {
							newString = "";
						} else {
							newString = string.substring(string.indexOf(EVEN_STRING) + 3, string.length());
						}
					}
				}

				text[i].setText(newString);
			}

			if (j == dayOfWeekReal) {
				int lesson = lesson(hours, minutes);
				if (lesson != -1)
					rows[lesson].setBackgroundColor(Color.parseColor("#DD34B4E3"));
			}

			day.setTag(titles[j]);
			mPages.add(day);

		}

		MainPageAdapter adapter = new MainPageAdapter(mPages);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(dayOfWeek);

		mTitleIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mTitleIndicator.setViewPager(mPager);
		mTitleIndicator.setCurrentItem(dayOfWeek);
		final float density = getResources().getDisplayMetrics().density;
		mTitleIndicator.setTextSize(15 * density); // 15dp
		mTitleIndicator.setFooterLineHeight(1 * density); // 1dp
		mTitleIndicator.setFooterIndicatorHeight(3 * density); // 3dp
		mTitleIndicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
		mTitleIndicator.setFooterColor(0x34B4E3);
		mTitleIndicator.setTextColor(0xAA000000);
		mTitleIndicator.setSelectedColor(0xFF000000);
		mTitleIndicator.setSelectedBold(true);
	}

	protected Sheet openExcel() {
		InputStream in = null;

		try {
			in = getResources().getAssets().open("schedule.xls");
		} catch (IOException e) {
			e.printStackTrace();
		}

		HSSFWorkbook wb = null;

		try {
			wb = new HSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wb.getSheetAt(0);
	}

	protected int lesson(int hours, int minutes) {
		int lesson = -1;
		switch (hours) {
		case 8:
			if (minutes >= 30)
				lesson = 0;
			break;
		case 9:
			if (minutes < 50)
				lesson = 0;
			else
				lesson = 1;
			break;
		case 10:
			lesson = 1;
			break;
		case 11:
			if (minutes < 20)
				lesson = 1;
			else
				lesson = 2;
			break;
		case 12:
			if (minutes < 50)
				lesson = 2;
			else
				lesson = 3;
			break;
		case 13:
			lesson = 3;
			break;
		case 14:
			if (minutes < 25)
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
			if (minutes < 30)
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