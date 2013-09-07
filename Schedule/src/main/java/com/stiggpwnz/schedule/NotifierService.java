package com.stiggpwnz.schedule;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.stiggpwnz.schedule.activities.MainActivity;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.stiggpwnz.schedule.fragments.MainFragment.getCurrentLesson;
import static com.stiggpwnz.schedule.fragments.MainFragment.getGroups;
import static com.stiggpwnz.schedule.fragments.MainFragment.parse;

/**
 * Created by Adel Nizamutdinov on 06.09.13
 */
public class NotifierService extends IntentService {

    @Inject Persistence persistence;
    @Inject Timber timber;
    @Inject DatabaseHelper databaseHelper;

    public NotifierService() {
        super(NotifierService.class.getSimpleName());
        ScheduleApp.getObjectGraph().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        FileMetadata metadata = persistence.getLastFileMetadata();
        // TODO schedule inapp
        if (metadata != null) {

            int lastSelectedGroup = persistence.getLastSelectedGroup(metadata.path);
            if (lastSelectedGroup != -1) {

                String[] times = getResources().getStringArray(R.array.times);

                DateTime now = DateTime.now();
                int day = now.getDayOfWeek() - 1;
                int lesson = getCurrentLesson(times, getString(R.string.time_separator), now);
                if (day != 6 && lesson != -1) {

                    try {
                        List<Group> groups = getGroups(this, databaseHelper, metadata);
                        Group group = groups.get(lastSelectedGroup);
                        boolean evenWeek = now.getWeekOfWeekyear() % 2 == 1;
                        String[][] lessons = parse(this, metadata, group.column, evenWeek);
                        String current = lessons[day][lesson];

                        if (!TextUtils.isEmpty(current)) {
                            notificationManager.notify(NotifierService.class.hashCode(),
                                    new NotificationCompat.Builder(this)
                                            .setAutoCancel(true)
                                            .setTicker(group.name + ' ' + times[lesson] + ' ' + current)
                                            .setSmallIcon(R.drawable.ic_launcher)
                                            .setContentTitle(group.name)
                                            .setContentInfo(times[lesson])
                                            .setContentText(current)
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(current).setSummaryText(""))
                                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                                            .setWhen(0)
                                            .build());
                            return;
                        }
                    } catch (Exception e) {
                        // fuck it
                    }
                }
                timber.d("hiding notification");
                notificationManager.cancel(NotifierService.class.hashCode());
            }
        }

        timber.d("showing no settings notification");
        // TODO show
    }
}
