package com.example.android.homeautomation;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import static com.example.android.homeautomation.Utils.address;
import static com.example.android.homeautomation.Utils.notificationDetails;

/**
 * Created by HP on 21-01-2018.
 */

public class NotificationIntentService extends IntentService {

    public NotificationIntentService(String name) {
        super(name);
    }

    public NotificationIntentService() {
        super("HELO");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        setDetails();
        sendNotification();
    }

    private void sendNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), Control.class);
        notificationIntent.putExtra("Extra address",address);

        notificationIntent.putExtra(Utils.statusONE,Utils.bulbStatusONE);
        Log.i("State of 1", Utils.bulbStatusONE+"");

        notificationIntent.putExtra(Utils.statusTWO,Utils.bulbStatusTWO);
        Log.i("State of 2", Utils.bulbStatusTWO+"");

        notificationIntent.putExtra(Utils.launchedFromNotification,true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Control.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentIntent(notificationPendingIntent)
                .setContentText("Cick here to open app");

        builder.setAutoCancel(true);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    public void setDetails(){
        if(Utils.bulbStatusONE == 0)
            notificationDetails = "Bulb 1 is OFF";
        else if(Utils.bulbStatusONE == 1)
            notificationDetails = "Bulb 1 is ON";
        if(Utils.bulbStatusTWO == 0)
            notificationDetails = notificationDetails + "\nBulb 2 is OFF";
        else if(Utils.bulbStatusTWO == 1)
            notificationDetails = notificationDetails + "\nBulb 2 is ON";
    }
}
