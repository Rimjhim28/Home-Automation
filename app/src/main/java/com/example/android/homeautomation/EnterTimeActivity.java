package com.example.android.homeautomation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by HP on 21-01-2018.
 */

public class EnterTimeActivity extends AppCompatActivity {
    Button btnDone;
    EditText txtAM, txtHour, txtMinute;
    Integer hour, minute;
    String time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.i(" Notification","One: "+Utils.bulbStatusONE);
        Log.i(" Notification","Two: "+Utils.bulbStatusTWO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_time);

        btnDone = (Button) findViewById(R.id.btn_done);
        txtAM = (EditText) findViewById(R.id.txt_am);
        txtHour = (EditText) findViewById(R.id.txt_hour);
        txtMinute = (EditText) findViewById(R.id.txt_minute);

        if ((txtAM.toString() == "AM" || txtAM.toString() == "PM") && (hour >= 1 && hour <= 12) && (minute >= 0 && minute <= 60))
            time = txtHour + " : " + txtMinute + " " + txtAM;
        Utils.time.add(time);


        btnDone.setOnClickListener(new View.OnClickListener() {
            public PendingIntent pendingIntent;

            @Override
            public void onClick(View view) {


                time = txtHour + " : " + txtMinute + " " + txtAM;
                Utils.time.add(time);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent myIntent = new Intent(EnterTimeActivity.this, NotificationIntentService.class);
                pendingIntent = PendingIntent.getService(EnterTimeActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 20 * 1000,
                        pendingIntent);
                showToast();

            }
        });

    }

    private void showToast() {
        Toast.makeText(this, "Time set Successfully", Toast.LENGTH_SHORT).show();
    }
}
