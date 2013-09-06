package com.stiggpwnz.schedule.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.koushikdutta.ion.Ion;
import com.stiggpwnz.schedule.FileMetadata;
import com.stiggpwnz.schedule.Group;
import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.Utils;
import com.stiggpwnz.schedule.adapters.DaysPagerAdapter;
import com.stiggpwnz.schedule.fragments.base.RetainedProgressFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static MainFragment newInstance(FileMetadata metadata) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(METADATA, metadata);
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager) ViewPager pager;

    Subscription groupListSubscription;
    List<Group> groups;

    private final Observer<List<Group>> groupsListObserver = new Observer<List<Group>>() {

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
    protected void onRetryClick() {
        onFirstCreated();
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.days_pager);
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

    private File getFile() {
        return new File(Utils.getFilesDir(getActivity()), getMetadata().path.substring(1));
    }

    @Override
    public void onFirstCreated() {
        FileMetadata metadata = getMetadata();
        if (metadata.path != null) {
            Observable<List<Group>> groupsListObservable;
            File file = getFile();
            if (file.exists() && file.length() > 0) {
                groupsListObservable = groupList(file);
            } else {
                setContentShown(false);
                groupsListObservable = Observable.from(Ion.with(getActivity(), DROPBOX + metadata.path)
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
        ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(getSupportActionBar().getThemedContext(), R.layout.sherlock_spinner_item, groups);
        adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(adapter, this);

        FileMetadata metadata = getMetadata();
        int lastSelected = persistence.getLastSelectedGroup(metadata.path);
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
        return Observable.create(new Func1<Observer<String[]>, Subscription>() {

            @Override
            public Subscription call(Observer<String[]> observer) {
                try {
                    CSVReader reader = getCsvReader(file);
                    String[] nextLine = null;
                    for (int i = 0; i < 2; i++) {
                        nextLine = reader.readNext();
                    }
                    reader.close();
                    observer.onNext(nextLine);
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        }).map(new Func1<String[], List<Group>>() {

            @Override
            public List<Group> call(String[] strings) {
                Pattern pattern = Pattern.compile(getMetadata().group_regex);
                List<Group> groups = new ArrayList<Group>();
                for (int i = 0; i < strings.length; i++) {
                    String string = strings[i];
                    if (pattern.matcher(string).matches()) {
                        groups.add(new Group(i, string));
                    }
                }
                return groups;
            }
        });
    }

    private CSVReader getCsvReader(File file) throws FileNotFoundException {
        return new CSVReader(new FileReader(file), ';');
    }

    Observable<String[][]> parseAsync(final int column, final boolean evenWeek) {
        return Observable.create(new Func1<Observer<String[][]>, Subscription>() {

            @Override
            public Subscription call(Observer<String[][]> observer) {
                try {
                    CSVReader reader = getCsvReader(getFile());
                    String[] nextLine;
                    for (int i = 0; i < 2; i++) {
                        reader.readNext();
                    }

                    int day = 0;
                    int lesson = 0;
                    String[][] result = new String[6][7];
                    boolean firstLine = true;

                    String oddWeekPrefix = getString(R.string.odd_week);
                    String evenWeekPrefix = getString(R.string.even_week);

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
                    observer.onNext(result);
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        Calendar calendar = Calendar.getInstance();
        boolean evenWeek = calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1;
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (day == 6) {
            day = 0;
            evenWeek = !evenWeek;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) > 19) {
            day++;
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
                    public void onNext(String[][] strings) {
                        if (pager.getAdapter() == null) {
                            pager.setAdapter(new DaysPagerAdapter(getChildFragmentManager(), strings, getResources().getStringArray(R.array.days)));
                            tabs.setViewPager(pager);
                            pager.setCurrentItem(finalDay, false);
                        } else {
                            DaysPagerAdapter adapter = (DaysPagerAdapter) pager.getAdapter();
                            adapter.setLessons(strings);
                        }
                        setContentEmpty(false);
                        setContentShown(true);
                    }
                });

        persistence.setLastSelectedGroup(getMetadata().path, i);

        return true;
    }
}