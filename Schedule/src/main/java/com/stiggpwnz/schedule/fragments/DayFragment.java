package com.stiggpwnz.schedule.fragments;

import android.os.Bundle;
import android.view.View;

import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.adapters.LessonsAdapter;
import com.stiggpwnz.schedule.fragments.base.BaseListFragment;

import org.joda.time.DateTime;

import static com.stiggpwnz.schedule.fragments.MainFragment.getCurrentLesson;

/**
 * Created by stiggpwnz on 31.08.13
 */
public class DayFragment extends BaseListFragment {

    public static DayFragment newInstance(String[] lessons, int highlightedColor) {
        DayFragment fragment = new DayFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("lessons", lessons);
        bundle.putInt("color", highlightedColor);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean today = getArguments().getBoolean("today", false);
        setListAdapter(new LessonsAdapter(getActivity(),
                getArguments().getStringArray("lessons"),
                getResources().getStringArray(R.array.times),
                getLesson(today),
                getArguments().getInt("color")));
    }

    public void setLessons(String[] lessons) {
        getArguments().putStringArray("lessons", lessons);
        getListAdapter().setLessons(lessons);
    }

    @Override
    public LessonsAdapter getListAdapter() {
        return (LessonsAdapter) super.getListAdapter();
    }

    public void setToday(boolean today) {
        getArguments().putBoolean("today", today);
        getListAdapter().setLesson(getLesson(today));
    }

    public void setHighLightedColor(int color) {
        getListAdapter().setHighlightedColor(color);
        getArguments().putInt("color", color);
    }

    private int getLesson(boolean today) {
        if (today) {
            return getCurrentLesson(getResources().getStringArray(R.array.times), getString(R.string.time_separator), DateTime.now());
        } else {
            return -1;
        }
    }
}
