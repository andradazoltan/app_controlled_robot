package com.turtletest.turtle;

import android.content.Intent;
import android.widget.Button;

import org.apache.tools.ant.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class StartScreen {
    private MainActivity startScreen;
    private Button startBtn;

    @Before
    public void setUp() throws Exception{
        startScreen = Robolectric.buildActivity(MainActivity.class).create().get();
        startScreen.onCreate(null);
        startBtn = startScreen.findViewById(R.id.bluetoothbtn);
    }

    @Test
    public void clickingStartButton() throws
            Exception{
        startBtn.performClick();
        Intent expectedIntent = new Intent(startScreen, BluetoothActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }
}
