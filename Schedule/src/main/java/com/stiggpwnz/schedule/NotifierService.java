package com.stiggpwnz.schedule;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.stiggpwnz.schedule.activities.MainActivity;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.stiggpwnz.schedule.fragments.MainFragment.getGroups;
import static com.stiggpwnz.schedule.fragments.MainFragment.parse;

/**
 * Created by Adel Nizamutdinov on 06.09.13
 */
public class NotifierService extends IntentService {

    public static final int ID = 789456;

    public static Intent newInstance(Context context, int i) {
        return new Intent(context, NotifierService.class).putExtra("lesson", i);
    }

    @Inject Persistence    persistence;
    @Inject Timber         timber;
    @Inject DatabaseHelper databaseHelper;

    public NotifierService() {
        super("notifier");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScheduleApp.getObjectGraph().inject(this);
    }

    public static boolean evenWeek(@NonNull DateTime dateTime) {
        return dateTime.getWeekOfWeekyear() % 2 == 1;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        timber.d("receiving intent");

        FileMetadata metadata = persistence.getLastFileMetadata();
        if (!ScheduleApp.inApp && metadata != null) {
            DateTime now = DateTime.now();
            int day = now.getDayOfWeek() - 1;
            int lesson = intent.getIntExtra("lesson", -1);
            if (day != 6 && lesson != -1) {
                // TODO check favourites
                int lastSelectedGroup = persistence.getLastSelectedGroup(metadata.path);
                if (lastSelectedGroup != -1) {
                    try {
                        timber.d("triggered: getting groups");
                        List<Group> groups = getGroups(this, databaseHelper, metadata);
                        Group group = groups.get(lastSelectedGroup);
                        boolean evenWeek = evenWeek(now);
                        Lesson[][] lessons = parse(this, databaseHelper, metadata, group.column);
                        String current = lessons[day][lesson].get(evenWeek);
                        timber.d("got lesson: %s", current);

                        if (!TextUtils.isEmpty(current.trim())) {
                            String[] times = getResources().getStringArray(R.array.times);

                            PendingIntent activity = PendingIntent.getActivity(
                                    this,
                                    0,
                                    new Intent(this, MainActivity.class),
                                    0);
                            notificationManager.notify(ID, new NotificationCompat.Builder(this)
                                    .setAutoCancel(true)
                                    .setTicker(group.name + ' ' + times[lesson] + ' ' + current)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(group.name)
                                    .setContentInfo(times[lesson])
                                    .setContentText(current)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(current)
                                                      .setSummaryText(""))
                                    .setContentIntent(activity)
                                    .setWhen(0)
                                    .build());
                            return;
                        }
                    } catch (Exception e) {
                        // fuck it
                    }
                }
                timber.d("hiding notification");
                notificationManager.cancel(ID);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
