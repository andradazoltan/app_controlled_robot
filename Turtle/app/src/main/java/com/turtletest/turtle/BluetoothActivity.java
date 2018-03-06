package com.turtletest.turtle;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * BluetoothActivity is a parent of all other children.
 * The user is able to pair to a bluetooth module in this activity.
 * References: http://mcuhq.com/27/simple-android-bluetooth-application-with-arduino-example
 *             https://www.codeproject.com/Articles/814814/Android-Connectivity
 */

public class BluetoothActivity extends FragmentActivity{

    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;

    //Bluetooth Selection Buttons
    private Button mStart;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;

    //Bluetooth connection variables
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    //Child fragment GUI Components
    private Button startstopBtn;
    private Button stopBtn;
    private CardView followBtn;
    private CardView obstacleBtn;

    //Lists of GUI Components arranged by activity and fragments
    private ArrayList<Button> btGUI = new ArrayList<Button>();
    private ArrayList<CardView> fragGUI = new ArrayList<CardView>();
    private ArrayList<CardView> actionGUI = new ArrayList<CardView>();

    //Associated fragments GUI components
    private CardView aboutUsButton;
    private CardView lightsButton;
    private CardView bluetoothButton;
    private CardView autoButton;
    private CardView manualButton;

    //Bluetooth variables
    private final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler; // Our main handler that will receive callback notifications
    public ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    /* Set up bluetooth connection, find GUI Components
     * @param Bundle savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoothactivity);

        //Find all GUI Components
        mBluetoothStatus = findViewById(R.id.bluetoothStatus);
        mReadBuffer = findViewById(R.id.readBuffer);
        mScanBtn = findViewById(R.id.scan);
        mOffBtn = findViewById(R.id.off);
        mDiscoverBtn = findViewById(R.id.discover);
        mListPairedDevicesBtn = findViewById(R.id.PairedBtn);
        mStart = findViewById(R.id.startbtn);
        startstopBtn = findViewById(R.id.startstopbtn);
        stopBtn = findViewById(R.id.stopBtn);

        //GUI Components for child fragments
        aboutUsButton = findViewById(R.id.aboutusBtn);
        lightsButton = findViewById(R.id.light);
        bluetoothButton = findViewById(R.id.bluetooth);
        autoButton = findViewById(R.id.automatic);
        manualButton = findViewById(R.id.manual);
        followBtn = findViewById(R.id.manualBTbtn);
        obstacleBtn = findViewById(R.id.obstacleBtn);

        //Bluetooth GUI components
        btGUI.add(mScanBtn);
        btGUI.add(mStart);
        btGUI.add(mOffBtn);
        btGUI.add(mDiscoverBtn);
        btGUI.add(mListPairedDevicesBtn);

        //Fragment GUI Components
        fragGUI.add(followBtn);
        fragGUI.add(obstacleBtn);

        //Action GUI Components
        actionGUI.add(aboutUsButton);
        actionGUI.add(lightsButton);
        actionGUI.add(bluetoothButton);
        actionGUI.add(manualButton);
        actionGUI.add(autoButton);

        //Change visibility of GUI from other fragments
        for (Button i:btGUI){
            changeButtonVisibility(true, i);
        }
        for (CardView i: fragGUI){
            changeCardVisibility(false, i);
        }
        changeButtonVisibility(false, startstopBtn);
        changeButtonVisibility(false, stopBtn);
        for (CardView i: actionGUI){
            changeCardVisibility(false, i);
        }

        //Fragment managers
        fragmentManager = getSupportFragmentManager();

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // Get a handle on the bluetooth radio

        mDevicesListView = findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // Assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        //Handler will receive callback notifications; allows Arduino to communicate with app
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        //Check if connection cannot be made
        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth - inform user
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {
            //Set onClick listener when the user presses start button
            mStart.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Fragment actionactivity = new ActionActivity();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //Save view to maintain order of views when user pushes back button
                    fragmentTransaction.replace(android.R.id.content, actionactivity).addToBackStack(null).commit();

                    //Disable visibility of child fragment GUI components
                    for (Button i: btGUI){
                        changeButtonVisibility(false, i);
                    }
                    changeButtonVisibility(false, startstopBtn);
                    changeButtonVisibility(false, stopBtn);
                    changeCardVisibility(false, followBtn);
                    changeCardVisibility(false, obstacleBtn);

                }
            });

            //Set onClick listeners of BT GUI Buttons
            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });
            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });
            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });
            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });
        }
    }

    /* Informs the user if bluetooth is turned on.
     * @param View view
     * Reference from: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //change GUI Components
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    /* Getter method for connected bluetooth thread
     * @return ConnectedThread thread
     */
    public ConnectedThread getThread(){
        return mConnectedThread;
    }

    /* Display bluetooth connection status after user selects
     * option to enable radio
     * @param int requestCode
     * @param int resultCode
     * @param Intent data
     * Reference from: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            }
            else
                mBluetoothStatus.setText("Disabled");
        }
    }
    /* Disables bluetooth connections.
     * @param View view
     * Reference from: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    /* Discovers new bluetooth connections.
     * @param View view
     * Reference from: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    /* Displays list of paired bluetooth connections
     * @param View view
     * Reference: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    /* Connects to bluetooth module from available devices.
     * Reference from: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.setHandler(mHandler);

                        mConnectedThread.start();
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    /* Creates Bluetooth Socket
     * @param BluetoothDevice device
     * @throws IOException
     * Reference from: https://www.codeproject.com/Articles/814814/Android-Connectivity
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    /* Redraw view when activity is returned to from back button.
     */
    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            //Restores visibility of bluetooth buttons on return
            for (Button i : btGUI){
                changeButtonVisibility(true, i);
            }
            getSupportFragmentManager().popBackStack();
        } else {
            //Restore parent view if stack is empty
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    /* Changes card visibility (lets user press the button )
     */
    public void changeCardVisibility(boolean visibility, CardView c){
        if (visibility){
            c.setVisibility(View.VISIBLE);
        } else {
            c.setVisibility(View.GONE);
        }
    }

    /* Changes button visibility (lets user press the button )
      */
    public void changeButtonVisibility(boolean visibility, Button b){
        if (visibility){
            b.setVisibility(View.VISIBLE);
        } else{
            b.setVisibility(View.GONE);
        }
    }
}
