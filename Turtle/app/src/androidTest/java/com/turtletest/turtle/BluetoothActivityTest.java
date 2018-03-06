package com.turtletest.turtle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;

import static android.support.test.espresso.matcher.RootMatchers.withDecorView;import android.support.test.espresso.Espresso;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;import android.support.test.espresso.action.ViewActions;
import static android.support.test.espresso.matcher.ViewMatchers.withText;import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.LargeTest;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BluetoothActivityTest {
    private FragmentManager fragmentManager;

    @Rule
    public ActivityTestRule<BluetoothActivity> btActivity = new ActivityTestRule<>(
            BluetoothActivity.class);

    @Before
    public void init() {
        fragmentManager = btActivity.getActivity().getSupportFragmentManager();
    }

    @Test
    public void checkButtons() {
        //Check displays
        Espresso.onView(withId(R.id.bluetoothbtn)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.PairedBtn)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.discover)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.devicesListView)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.off)).check(matches(isDisplayed()));
        //Check clickable
        Espresso.onView(withId(R.id.bluetoothbtn)).check(matches(isClickable()));
        Espresso.onView(withId(R.id.PairedBtn)).check(matches(isClickable()));
        Espresso.onView(withId(R.id.discover)).check(matches(isClickable()));
        Espresso.onView(withId(R.id.devicesListView)).check(matches(isClickable()));
        Espresso.onView(withId(R.id.off)).check(matches(isClickable()));
    }

    @Test
    public void checkLayout() {
        Espresso.onView(withId(R.id.bluetoothbtn)).check(matches(withParent(withId(R.id.btll))));
    }

    @Test
    public void actionFrag() {
        Espresso.onView(withId(R.id.startbtn)).perform(click());
        fragmentManager.beginTransaction().add(R.id.action_fragment, new ActionActivity()).commit();
        onView(withId(R.id.bluetooth)).check(matches(isDisplayed()));
    }

}