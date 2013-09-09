package com.stiggpwnz.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import javax.inject.Inject;

import timber.log.Timber;

import static com.stiggpwnz.schedule.fragments.MainFragment.TIME_FORMATTER;

/**
 * Created by Adel Nizamutdinov on 08.09.13
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Inject Persistence persistence;
    @Inject Timber timber;

    static boolean shouldForceNotify(Context context, Intent intent) {
        return intent != null && intent.getBooleanExtra(context.getString(R.string.should_notify), false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleApp.getObjectGraph().inject(this);

        timber.d("starting receiver");
        if (!persistence.shouldNotify() && !shouldForceNotify(context, intent)) {
            return;
        }

        String[] times = context.getResources().getStringArray(R.array.times);
        String separator = context.getString(R.string.time_separator);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime now = DateTime.now();

        LocalTime previous = null;
        for (int i = 0; i < times.length; i++) {
            String[] timeSplit = times[i].split(separator);
            DateTime start = (previous != null ? previous : TIME_FORMATTER.parseLocalTime(timeSplit[0]).minusMinutes(30)).toDateTimeToday();
            previous = TIME_FORMATTER.parseLocalTime(timeSplit[1]);
            long triggerAtMillis = start.isBefore(now) ? start.plusDays(1).getMillis() : start.getMillis();
            timber.d("will trigger %s", new DateTime(triggerAtMillis));
            alarmManager.setRepeating(AlarmManager.RTC,
                    triggerAtMillis,
                    AlarmManager.INTERVAL_DAY,
                    PendingIntent.getService(context, i, NotifierService.newInstance(context, i), 0));
        }
    }

}
