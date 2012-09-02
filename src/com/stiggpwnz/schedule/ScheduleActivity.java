package com.stiggpwnz.schedule;

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

public class ScheduleActivity extends SherlockFragmentActivity implements TabListener, OnPageChangeListener {

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

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			getSupportActionBar().setTitle(app.getGroupName());
			
			adapter.updateTime();
			if (app.isRowUpdated())
				adapter.setRow(app.getRow());
			adapter.notifyDataSetChanged();
			
			getSupportActionBar().setSelectedNavigationItem(adapter.getDay());
			pager.setCurrentItem(adapter.getDay());
		} else if (app.getRow() != null) {
			getSupportActionBar().setTitle(app.getGroupName());

			adapter = new DaysAdapter(this, getSupportFragmentManager(), app.getRow());
			adapter.updateTime();

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

}
