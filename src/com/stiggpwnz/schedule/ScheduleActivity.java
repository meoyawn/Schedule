package com.stiggpwnz.schedule;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScheduleActivity extends SherlockFragmentActivity implements TabListener, OnPageChangeListener, EditLessonFragment.Listener, DayFragment.Listener {

	private ScheduleApplication app;
	private ViewPager pager;
	private DaysAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (ScheduleApplication) getApplication();
		setContentView(R.layout.activity_main);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private void updateDay() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK) + 5;
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
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

		if (app.getDay() != day) {
			app.setDay(day);
			app.setWeek(week);
			adapter.setDay(day);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			setTitle(app.getGroupName());

			if (app.isRowUpdated())
				adapter.setLessons(app.getLessons());
			updateDay();
			adapter.updateLesson();

			getSupportActionBar().setSelectedNavigationItem(app.getDay());
			pager.setCurrentItem(app.getDay());
		} else if (app.getRow() != null) {
			setTitle(app.getGroupName());

			adapter = new DaysAdapter(this, getSupportFragmentManager(), app.getDay(), app.getLessons());
			updateDay();

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

			getSupportActionBar().setSelectedNavigationItem(app.getDay());
			pager.setCurrentItem(app.getDay());
		} else {
			startActivity(new Intent(this, PreferenceActivity.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, PreferenceActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onPageSelected(int position) {
		getSupportActionBar().setSelectedNavigationItem(position);
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

	@Override
	public void saveLesson(int day, int lesson, String output) {
		String parseOddEven = app.parseOddEven(output);
		app.saveValue(day, lesson, output);
		adapter.setLesson(day, lesson, parseOddEven);
	}

	@Override
	public String getActualStringData(int day, int lesson) {
		return app.getActualStringData(day, lesson);
	}
}
