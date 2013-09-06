package com.stiggpwnz.schedule.fragments;

import android.os.Bundle;
import android.view.View;

import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.adapters.LessonsAdapter;
import com.stiggpwnz.schedule.fragments.base.BaseListFragment;

/**
 * Created by stiggpwnz on 31.08.13
 */
public class DayFragment extends BaseListFragment {

    public static DayFragment newInstance(String[] lessons) {
        DayFragment fragment = new DayFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("lessons", lessons);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new LessonsAdapter(getActivity(), getArguments().getStringArray("lessons"), getResources().getStringArray(R.array.times)));
    }

    public void setLessons(String[] lessons) {
        getArguments().putStringArray("lessons", lessons);
        LessonsAdapter adapter = (LessonsAdapter) getListAdapter();
        adapter.setLessons(lessons);
    }
}
