package com.example.android.homeautomation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.android.homeautomation.Utils.address;
import static com.example.android.homeautomation.Utils.bulbStatusONE;
import static com.example.android.homeautomation.Utils.bulbStatusTWO;

/**
 * Created by HP on 17-01-2018.
 */

public class Control extends AppCompatActivity{

    Button btn1, btn2;
    ImageView imageButton1, imageButton2;
    TextView bulb1Status, bulb2Status;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice device = null;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        btn1 = (Button) findViewById(R.id.btn_1);
        btn2 = (Button) findViewById(R.id.btn_2);
        imageButton1 = (ImageView) findViewById(R.id.bulb_1);
        imageButton2 = (ImageView) findViewById(R.id.bulb_2);
        bulb1Status = (TextView) findViewById(R.id.bulb_1_txt);
        bulb2Status = (TextView) findViewById(R.id.bulb_2_txt);
        Intent intent = getIntent();

        if(mSharedPreferences != null){
            int one = mSharedPreferences.getInt("Status of 1",10);
            int two = mSharedPreferences.getInt("Status of 2",10);
            if(one != 10 && two != 10){

                Utils.bulbStatusONE = one;
                Utils.bulbStatusTWO = two;

                Log.i(" NotificationPP","One: "+one);
                Log.i(" NotificationPP","Two: "+two);
            }
        }

        address = intent.getStringExtra("Extra address");
        Log.i("Address: ", ""+address);

        if (btSocket == null && btAdapter == null && device == null) {
            //create device and set the MAC address
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //insert code to deal with this
                }
            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }

        if(intent.getExtras()!= null){
            int one = intent.getIntExtra(Utils.statusONE,10);
            int two = intent.getIntExtra(Utils.statusTWO,10);
            if(one!= 10 && two != 10)
            {
                Log.i(" Notification","One: "+one);
                Log.i(" Notification","Two: "+two);
                Utils.bulbStatusTWO = two;
                Utils.bulbStatusONE = one;
            }
        }
        boolean launched = intent.getBooleanExtra(Utils.launchedFromNotification,false);
        if(launched) {
            if (Utils.bulbStatusONE == 1) {
                DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(this, R.color.green));
                btn1.setText(R.string.turn_off);
                bulb1Status.setText(R.string.bulb_1_on_status);
            }
            if (Utils.bulbStatusONE == 0) {
                DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(this, R.color.red));
                btn1.setText(R.string.turn_on);
                bulb1Status.setText(R.string.bulb_1_off_status);
            }
            if (Utils.bulbStatusTWO == 1) {
                DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(this, R.color.green));
                btn2.setText(R.string.turn_off);
                bulb2Status.setText(R.string.bulb_2_on_status);
            }
            if (Utils.bulbStatusTWO == 0) {
                DrawableCompat.setTint(imageButton2.getDrawable(), ContextCompat.getColor(this, R.color.red));
                btn2.setText(R.string.turn_on);
                bulb2Status.setText(R.string.bulb_2_off_status);
            }
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bulbStatusONE == 0) {
                    DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green));
                    btn1.setText(R.string.turn_off);
                    bulb1Status.setText(R.string.bulb_1_on_status);
                    bulbStatusONE = 1;
                    //Send data to turn the bulb ON
                    mConnectedThread.write("1");
                }
                else if(bulbStatusONE == 1) {
                    DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.red));
                    btn1.setText(R.string.turn_on);
                    bulb1Status.setText(R.string.bulb_1_off_status);
                    bulbStatusONE = 0;
                    //Send data to turn bulb OFF
                    mConnectedThread.write("2");
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bulbStatusTWO == 0) {
                    DrawableCompat.setTint(imageButton2.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green));
                    btn2.setText(R.string.turn_off);
                    bulb2Status.setText(R.string.bulb_2_on_status);
                    bulbStatusTWO = 1;
                    //Send data to turn the bulb ON
                    mConnectedThread.write("4");
                }
                else if(bulbStatusTWO ==1) {
                    DrawableCompat.setTint(imageButton2.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.red));
                    btn2.setText(R.string.turn_on);
                    bulb2Status.setText(R.string.bulb_2_off_status);
                    bulbStatusTWO = 0;
                    //Send data to turn the bulb OFF
                    mConnectedThread.write("3");
                }
            }
        });
    }

    public void updateButtonOne(View view){

        if(bulbStatusONE == 0) {
            DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(this, R.color.green));
            btn1.setText(R.string.turn_off);
            bulb1Status.setText(R.string.bulb_1_on_status);
            bulbStatusONE = 1;
            //Send data to turn the bulb ON
            mConnectedThread.write("1");
        }
        else if(bulbStatusONE == 1) {
            DrawableCompat.setTint(imageButton1.getDrawable(), ContextCompat.getColor(this, R.color.red));
            btn1.setText(R.string.turn_on);
            bulb1Status.setText(R.string.bulb_1_off_status);
            bulbStatusONE = 0;
            //Send data to turn bulb OFF
            mConnectedThread.write("2");
        }
    }

    public void updateButtonTwo(View view){

        if(bulbStatusTWO == 0) {
            DrawableCompat.setTint(imageButton2.getDrawable(), ContextCompat.getColor(this, R.color.green));
            btn2.setText(R.string.turn_off);
            bulb2Status.setText(R.string.bulb_2_on_status);
            bulbStatusTWO = 1;
            //Send data to turn the bulb ON
            mConnectedThread.write("4");
        }
        else if(bulbStatusTWO ==1) {
            DrawableCompat.setTint(imageButton2.getDrawable(), ContextCompat.getColor(this, R.color.red));
            btn2.setText(R.string.turn_on);
            bulb2Status.setText(R.string.bulb_2_off_status);
            bulbStatusTWO = 0;
            //Send data to turn the bulb OFF
            mConnectedThread.write("3");
        }
    }

    //SENDING DATA TO ARDUINO VIA BLUETOOTH
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    //bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_time:
                Intent intent = new Intent(Control.this,EnterTimeActivity.class);
                startActivity(intent);
                return true;

            default:
                return false;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed =  mSharedPreferences.edit();
        ed.putInt("State of 1 PAUSE",Utils.bulbStatusONE);
        ed.putInt("State of 2 PAUSE", Utils.bulbStatusTWO);
        ed.apply();
    }

}
