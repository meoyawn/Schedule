package com.stiggpwnz.schedule;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.stiggpwnz.schedule.activities.MainActivity;
import com.stiggpwnz.schedule.activities.PreferenceActivity;
import com.stiggpwnz.schedule.adapters.DaysPagerAdapter;
import com.stiggpwnz.schedule.fragments.DayFragment;
import com.stiggpwnz.schedule.fragments.FacultiesFragment;
import com.stiggpwnz.schedule.fragments.MainFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Created by stiggpwnz on 30.08.13
 */
@Module(injects = {MainActivity.class, MainFragment.class, DayFragment.class, FacultiesFragment.class, NotifierService.class, BootCompletedReceiver.class,
        PreferenceActivity.class, DaysPagerAdapter.class})
public class DependenciesModule {

    private final Context context;

    public DependenciesModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Persistence providePersistance() {
        return new Persistence(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Provides
    @Singleton
    public Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @Singleton
    public Timber provideTimber() {
        return BuildConfig.DEBUG ? Timber.DEBUG : Timber.PROD;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }
}
