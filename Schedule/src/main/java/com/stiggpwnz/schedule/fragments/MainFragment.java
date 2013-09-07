package com.stiggpwnz.schedule.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.koushikdutta.ion.Ion;
import com.stiggpwnz.schedule.DatabaseHelper;
import com.stiggpwnz.schedule.FileMetadata;
import com.stiggpwnz.schedule.Group;
import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.adapters.DaysPagerAdapter;
import com.stiggpwnz.schedule.adapters.GroupsAdapter;
import com.stiggpwnz.schedule.fragments.base.RetainedProgressFragment;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Func1;

/**
 * Created by Adel Nizamutdinov on 31.08.13
 */
public class MainFragment extends RetainedProgressFragment implements ActionBar.OnNavigationListener {

    public static final String DROPBOX = "https://dl.dropboxusercontent.com/u/32772116/schedule";
    public static final String METADATA = "metadata";

    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("H.m");

    public static MainFragment newInstance(FileMetadata metadata) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(METADATA, metadata);
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public static int getCurrentLesson(String[] times, String separator, DateTime now) {
        LocalTime previous = null;
        for (int i = 0; i < times.length; i++) {
            String[] timeSplit = times[i].split(separator);
            LocalTime start = previous != null ? previous : TIME_FORMATTER.parseLocalTime(timeSplit[0]).minusMinutes(30);
            LocalTime end = TIME_FORMATTER.parseLocalTime(timeSplit[1]);
            if (new Interval(start.toDateTimeToday(), end.toDateTimeToday()).contains(now)) {
                return i;
            }
            previous = end;
        }
        return -1;
    }

    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager) ViewPager pager;

    Subscription groupListSubscription;
    List<Group> groups;

    MenuItem favouriteMenuItem;

    final Observer<Group> favouriteRefreshedObserver = new Observer<Group>() {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable throwable) {
            if (favouriteMenuItem != null) {
                favouriteMenuItem.setIcon(R.drawable.ic_action_favourite);
            }
        }

        @Override
        public void onNext(Group group) {
            if (favouriteMenuItem != null) {
                favouriteMenuItem.setIcon(group.isFavourite ?
                        R.drawable.ic_action_unfavourite :
                        R.drawable.ic_action_favourite);
            }
        }
    };

    final Observer<List<Group>> groupsListObserver = new Observer<List<Group>>() {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable throwable) {
            setEmptyText(R.string.error_downloading_faculty_file);
            setContentEmpty(true);
            setContentShown(true);
        }

        @Override
        public void onNext(List<Group> strings) {
            MainFragment.this.groups = strings;
            setUpNavigation();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favourite:
                final int index = getSupportActionBar().getSelectedNavigationIndex();
                toggleFavourite(index).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favouriteRefreshedObserver);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Observable<Group> refreshFavourite(final int index) {
        return Observable.create(new Func1<Observer<Group>, Subscription>() {

            @Override
            public Subscription call(Observer<Group> observer) {
                try {
                    final Group group = groups.get(index);
                    databaseHelper.getDao(Group.class).refresh(group);
                    observer.onNext(group);
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    private Observable<Group> toggleFavourite(final int index) {
        return Observable.create(new Func1<Observer<Group>, Subscription>() {

            @Override
            public Subscription call(Observer<Group> observer) {
                try {
                    final Group group = groups.get(index);
                    group.isFavourite = !group.isFavourite;
                    databaseHelper.getDao(Group.class).createOrUpdate(group);
                    observer.onNext(group);
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    @Override
    protected void onRetryClick() {
        onFirstCreated();
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.days_pager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        favouriteMenuItem = menu.findItem(R.id.action_favourite);
        favouriteMenuItem.setEnabled(false);
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        if (groups != null) {
            setUpNavigation();
        }
    }

    private ActionBar getSupportActionBar() {
        return getSherlockActivity().getSupportActionBar();
    }

    @Override
    public void onFirstCreated() {
        FileMetadata metadata = getMetadata();
        if (metadata.path != null) {
            Observable<List<Group>> groupsListObservable;
            File file = metadata.getFile(getActivity());
            if (file.exists() && file.length() > 0) {
                groupsListObservable = groupList(file);
            } else {
                setContentShown(false);
                groupsListObservable = Observable.from(Ion.with(getActivity(), metadata.getUrl())
                        .write(file))
                        .flatMap(new Func1<File, Observable<List<Group>>>() {

                            @Override
                            public Observable<List<Group>> call(File file) {
                                return groupList(file);
                            }
                        });
            }
            groupListSubscription = groupsListObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(groupsListObserver);
        } else {
            setEmptyText(R.string.choose_your_faculty);
            retryButton.setVisibility(View.GONE);
            setContentEmpty(true);
        }
    }

    private void setUpNavigation() {
        SpinnerAdapter adapter = new GroupsAdapter(getSupportActionBar().getThemedContext(), groups);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(adapter, this);

        int lastSelected = persistence.getLastSelectedGroup(getMetadata().path);
        if (lastSelected != -1) {
            getSupportActionBar().setSelectedNavigationItem(lastSelected);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private FileMetadata getMetadata() {
        return (FileMetadata) getArguments().getSerializable(METADATA);
    }

    @Override
    public void onDestroy() {
        if (groupListSubscription != null) {
            groupListSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    Observable<List<Group>> groupList(final File file) {
        return Observable.create(new Func1<Observer<List<Group>>, Subscription>() {

            @Override
            public Subscription call(Observer<List<Group>> observer) {
                try {
                    List<Group> groupList = getGroups(getActivity(), databaseHelper, getMetadata());
                    observer.onNext(groupList);
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    public static List<Group> getGroups(Context context, DatabaseHelper databaseHelper, FileMetadata metadata) throws SQLException, IOException {
        CSVReader reader = getCsvReader(metadata.getFile(context));
        reader.readNext();
        String[] nextLine = reader.readNext();
        reader.close();

        Pattern pattern = Pattern.compile(metadata.group_regex);
        List<Group> groups = new ArrayList<Group>();
        for (int i = 0; i < nextLine.length; i++) {
            String string = nextLine[i];
            if (pattern.matcher(string).matches()) {
                Group group = new Group(i, string);
                databaseHelper.getDao(Group.class).refresh(group);
                groups.add(group);
            }
        }
        Collections.sort(groups);
        return groups;
    }

    private static CSVReader getCsvReader(File file) throws FileNotFoundException {
        return new CSVReader(new FileReader(file), ';');
    }

    Observable<String[][]> parseAsync(final int column, final boolean evenWeek) {
        return Observable.create(new Func1<Observer<String[][]>, Subscription>() {

            @Override
            public Subscription call(Observer<String[][]> observer) {
                try {
                    String[][] lessons = parse(getActivity(), getMetadata(), column, evenWeek);
                    observer.onNext(lessons);
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    public static String[][] parse(Context context, FileMetadata metadata, int column, boolean evenWeek) throws IOException {
        CSVReader reader = getCsvReader(metadata.getFile(context));
        String[] nextLine;
        for (int i = 0; i < 2; i++) {
            reader.readNext();
        }

        int day = 0;
        int lesson = 0;
        String[][] result = new String[6][7];
        boolean firstLine = true;

        String oddWeekPrefix = context.getString(R.string.odd_week);
        String evenWeekPrefix = context.getString(R.string.even_week);

        while ((nextLine = reader.readNext()) != null) {
            if (!TextUtils.isEmpty(nextLine[0])) {
                String value = nextLine[column];
                if (evenWeek) {
                    if (value.contains(evenWeekPrefix)) {
                        result[day][lesson] = value.replace(evenWeekPrefix, "");
                    } else if (!value.contains(oddWeekPrefix)) {
                        result[day][lesson] = value;
                    }
                } else {
                    if (value.contains(oddWeekPrefix)) {
                        result[day][lesson] = value.replace(oddWeekPrefix, "");
                    } else if (!value.contains(evenWeekPrefix)) {
                        result[day][lesson] = value;
                    }
                }
                if (!firstLine) {
                    lesson++;
                }
                firstLine = !firstLine;
            } else {
                day++;
                lesson = 0;
                firstLine = true;
            }
        }

        reader.close();
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        DaysPagerAdapter adapter = (DaysPagerAdapter) pager.getAdapter();
        if (adapter != null) {
            DateTime dateTime = DateTime.now();
            int day = dateTime.getDayOfWeek() - 1;
            int hour = dateTime.getHourOfDay();
            if (day == 6) {
                day = 0;
            } else if (hour > 19) {
                if (day == 5) {
                    day = 0;
                } else {
                    day++;
                }
            }
            adapter.setDay(day);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        refreshFavourite(i)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favouriteRefreshedObserver);

        DateTime dateTime = DateTime.now();
        boolean evenWeek = dateTime.getWeekOfWeekyear() % 2 == 1;
        int day = dateTime.getDayOfWeek() - 1;
        int hour = dateTime.getHourOfDay();
        if (day == 6) {
            day = 0;
            evenWeek = !evenWeek;
        } else if (hour > 19) {
            if (day == 5) {
                day = 0;
            } else {
                day++;
            }
        }

        setContentShown(false);
        final int finalDay = day;

        parseAsync(groups.get(i).column, evenWeek)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[][]>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        setEmptyText(R.string.unable_to_parse_lessons);
                        setContentEmpty(true);
                        setContentShown(true);
                    }

                    @Override
                    public void onNext(String[][] lessons) {
                        if (pager.getAdapter() == null) {
                            pager.setAdapter(new DaysPagerAdapter(getChildFragmentManager(),
                                    lessons,
                                    getResources().getStringArray(R.array.days),
                                    finalDay));
                            tabs.setViewPager(pager);
                            tabs.setIndicatorColorResource(R.color.green);
                            pager.setCurrentItem(finalDay, false);
                        } else {
                            getPagerAdapter().setLessons(lessons);
                        }
                        setContentEmpty(false);
                        setContentShown(true);
                        favouriteMenuItem.setEnabled(true);
                    }
                });

        persistence.setLastSelectedGroup(getMetadata().path, i);

        return true;
    }

    DaysPagerAdapter getPagerAdapter() {
        if (pager != null) {
            return (DaysPagerAdapter) pager.getAdapter();
        }
        return null;
    }
}