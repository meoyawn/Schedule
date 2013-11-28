package com.stiggpwnz.schedule.test;


import android.test.ActivityInstrumentationTestCase2;

import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.activities.MainActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * Created by adelnizamutdinov on 28/11/2013
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testLoadFaculty() {
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.left_drawer)).check(matches(isDisplayed()));
    }
}