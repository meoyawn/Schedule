package com.stiggpwnz.schedule.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;
import com.squareup.otto.Subscribe;
import com.stiggpwnz.schedule.FileMetadata;
import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.fragments.FacultiesFragment;
import com.stiggpwnz.schedule.fragments.MainFragment;

import butterknife.InjectView;
import de.cketti.library.changelog.ChangeLog;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;

    SherlockActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.main_root);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerToggle = new SherlockActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer_dark, R.string.opened, R.string.closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.faculties);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportFragmentManager().findFragmentById(R.id.left_drawer) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_drawer, new FacultiesFragment())
                    .commit();
        }

        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) == null) {
            FileMetadata metadata = persistence.getLastFileMetadata();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, MainFragment.newInstance(metadata))
                    .commit();

            if (metadata.path == null) {
                drawerLayout.openDrawer(Gravity.LEFT);
                drawerToggle.onDrawerOpened(null);
            }
        }

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onFacultySelected(FileMetadata metadata) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, MainFragment.newInstance(metadata))
                .commit();
        drawerLayout.closeDrawers();
        persistence.setLastSelectedMetadata(metadata);
    }
}
