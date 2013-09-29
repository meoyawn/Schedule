package com.stiggpwnz.schedule.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dmitriy.tarasov.android.intents.IntentUtils;
import com.stiggpwnz.schedule.BootCompletedReceiver;
import com.stiggpwnz.schedule.NotifierService;
import com.stiggpwnz.schedule.Persistence;
import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

import de.cketti.library.changelog.ChangeLog;

/**
 * Created by Adel Nizamutdinov on 09.09.13
 */
public class PreferenceActivity extends SherlockPreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Inject Persistence persistence;

    CheckBoxPreference notifyPreference;

    Preference changelog;
    Preference contact;
    Preference source;
    Preference rate;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApp.getObjectGraph().inject(this);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(persistence.getMainColor()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.prefs);

        notifyPreference = (CheckBoxPreference) findPreference(getString(R.string.should_notify));
        if (notifyPreference != null) {
            notifyPreference.setOnPreferenceChangeListener(this);
        }
        changelog = findPreference(getString(R.string.changelog_key));
        if (changelog != null) {
            changelog.setOnPreferenceClickListener(this);
        }
        contact = findPreference(getString(R.string.contact_key));
        if (contact != null) {
            contact.setOnPreferenceClickListener(this);
        }
        source = findPreference(getString(R.string.source_key));
        if (source != null) {
            source.setOnPreferenceClickListener(this);
        }
        rate = findPreference(getString(R.string.rate_key));
        if (rate != null) {
            rate.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == notifyPreference) {
            Boolean value = (Boolean) newValue;
            if (value) {
                new BootCompletedReceiver().onReceive(this, new Intent().putExtra(getString(R.string.should_notify), true));
            } else {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                String[] times = getResources().getStringArray(R.array.times);
                for (int i = 0; i < times.length; i++) {
                    alarmManager.cancel(PendingIntent.getService(this, i, NotifierService.newInstance(this, i), 0));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == changelog) {
            AlertDialog logDialog = new ChangeLog(this).getLogDialog();
            logDialog.setCanceledOnTouchOutside(true);
            logDialog.show();
            return true;
        } else if (preference == contact) {
            startActivity(IntentUtils.sendEmail("stiggpwnz@gmail.com", getString(R.string.support_email_subject), ""));
            return true;
        } else if (preference == source) {
            startActivity(IntentUtils.openLink("https://github.com/adelnizamutdinov/Schedule"));
            return true;
        } else if (preference == rate) {
            startActivity(IntentUtils.openPlayStore(this));
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        ScheduleApp.inApp = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScheduleApp.inApp = false;
    }

}
