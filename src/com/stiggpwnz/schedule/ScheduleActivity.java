package com.stiggpwnz.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class ScheduleActivity extends SherlockFragmentActivity implements TabListener, OnPageChangeListener {

	private DaysAdapter adapter;
	private ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Calendar calendar = Calendar.getInstance();
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		int day = calendar.get(Calendar.DAY_OF_WEEK) + 5;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int lesson = lesson(day, hour, minute);

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

		try {
			Sheet sheet = open("sched12131.xls");
			adapter = new DaysAdapter(this, getSupportFragmentManager(), sheet.getRow(2), week, day, lesson);
			pager = (ViewPager) findViewById(R.id.pager);
			pager.setAdapter(adapter);
			pager.setOnPageChangeListener(this);

			for (int i = 0; i < adapter.getCount(); i++) {
				CharSequence pageTitle = adapter.getPageTitle(i);
				Tab tab = getSupportActionBar().newTab();
				tab.setText(pageTitle);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		getSupportActionBar().setSelectedNavigationItem(day);
		pager.setCurrentItem(day);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onPageSelected(int position) {
		getSupportActionBar().setSelectedNavigationItem(position);
	}

	private Sheet open(String filename) throws IOException {
		InputStream is = getResources().getAssets().open(filename);
		HSSFWorkbook wb = new HSSFWorkbook(is);
		is.close();
		return wb.getSheetAt(0);
	}

	private int lesson(int day, int hour, int minute) {
		int lesson = -1;
		if (day == DaysAdapter.DAYS_NUMBER)
			return lesson;

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

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

}
