package com.app.krambook.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.krambook.R;
import com.app.krambook.activity.SettingsActivity;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sid on 17-Jan-16.
 */
public class CustomNotificationReceiver extends ParsePushBroadcastReceiver {

    String disid,fullmsg,usern,text,title,body,type;

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        Log.d("TAG", "Inside Push Open");

        Log.e("Push", "Clicked");
        Intent i = new Intent(context, SettingsActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        JSONObject pushData = null;

        try {
            pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
        } catch (JSONException var7) {
            //Parse.logE("com.parse.ParsePushReceiver", "Unexpected JSONException when receiving push data: ", var7);
            Log.d("ParsePushReceiver","Unexpected: " + var7);
        }

        String action = null;
        if(pushData != null) {
            action = pushData.optString("action", (String)null);
        }

        if(action != null) {
            Bundle notification = intent.getExtras();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtras(notification);
            broadcastIntent.setAction(action);
            broadcastIntent.setPackage(context.getPackageName());
            context.sendBroadcast(broadcastIntent);
        }
        ParseObject alert = new ParseObject("Alert");
        NotificationCompat.InboxStyle Style = new NotificationCompat.InboxStyle();
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context);

        Notification notification = mbuilder
                .setSmallIcon(R.drawable.notification_icon)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Message from " + alert.getParseUser("fromuser"))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(alert.getString("contente"))
                .setStyle(Style)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, notification);


    }

}

