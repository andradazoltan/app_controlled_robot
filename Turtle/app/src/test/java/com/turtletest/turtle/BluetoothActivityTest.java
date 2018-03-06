package com.turtletest.turtle;

import android.bluetooth.BluetoothAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowBluetoothAdapter;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;
@RunWith(RobolectricTestRunner.class)
public class BluetoothActivityTest {
    
    private BluetoothAdapter bluetoothAdapter;
    private ShadowBluetoothAdapter shadowBluetoothAdapter;
    private BluetoothActivity btActivity;

    @Before
    public void setUp() throws Exception{
        bluetoothAdapter = Shadow.newInstanceOf(BluetoothAdapter.class);
        shadowBluetoothAdapter = shadowOf(bluetoothAdapter);
    }

    //Test child fragment and parent launches
    @Test
    public void testLaunch() throws Exception {
        assertNotNull(shadowOf(RuntimeEnvironment.application));
        assertTrue(Robolectric.setupActivity(BluetoothActivity.class) != null);
    }

    @Test
    public void lightLaunch() throws Exception{
        LightsActivity lightFrag = new LightsActivity();
        SupportFragmentTestUtil.startFragment(lightFrag, BluetoothActivity.class);
        assertNotNull(lightFrag);
    }

    @Test
    public void GyroLaunch() throws Exception{
        assertNotNull(shadowOf(RuntimeEnvironment.application));
        Gryo gyroFragment = new Gryo();
        SupportFragmentTestUtil.startFragment(gyroFragment, BluetoothActivity.class);
        assertNotNull(gyroFragment);
    }

    @Test
    public void ActionLaunch() throws Exception{
        assertNotNull(shadowOf(RuntimeEnvironment.application));
        ActionActivity actionFragment = new ActionActivity();
        SupportFragmentTestUtil.startFragment(actionFragment, BluetoothActivity.class);
        assertNotNull(actionFragment);
    }

    @Test
    public void ManualLaunch() throws Exception{
        assertNotNull(shadowOf(RuntimeEnvironment.application));
        ManualActivity manFragment = new ManualActivity();
        SupportFragmentTestUtil.startFragment(manFragment, BluetoothActivity.class);
        assertNotNull(manFragment);
    }

    @Test
    public void InfoLaunch() throws Exception{
        assertNotNull(shadowOf(RuntimeEnvironment.application));
        AboutUs infoFragment = new AboutUs();
        SupportFragmentTestUtil.startFragment(infoFragment, BluetoothActivity.class);
        assertNotNull(infoFragment);
    }

    //Test Bluetooth Components
    @Test
    public void testInitialAdapter(){
        assertTrue(!bluetoothAdapter.isEnabled());
    }

    @Test
    public void testAdapterEnable(){
        shadowBluetoothAdapter.setEnabled(true);
        assertTrue(bluetoothAdapter.isEnabled());
    }

    @Test
    public void testSetAddress() throws Exception{
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        shadowOf(adapter).setAddress("expected");
        assertTrue(adapter.getAddress().equals("expected"));
    }

    //Test data being sent using Toast
    @Test
    public void testAutoText() throws Exception{
        BluetoothActivity btActivity = Robolectric.setupActivity(BluetoothActivity.class);
        btActivity.findViewById(R.id.stopBtn).performClick();
        assertTrue(ShadowToast.getTextOfLatestToast().toString().equals("SS"));

        btActivity.findViewById(R.id.obstacleBtn).performClick();
        assertTrue(ShadowToast.getTextOfLatestToast().toString().equals("OO"));

        btActivity.findViewById(R.id.manualBTbtn).performClick();
        assertTrue(ShadowToast.getTextOfLatestToast().toString().equals("FF"));
    }

}