package com.app.krambook.parse_session;

import android.app.Application;
import android.util.Log;

import com.app.krambook.activity.Activity;
import com.app.krambook.activity.SettingsActivity;
import com.app.krambook.models.Colg;
import com.app.krambook.models.GlobalVariables;
import com.app.krambook.models.Photo;
import com.app.krambook.models.UserData;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.rollbar.android.Rollbar;

/**
 * Created by nubxf5 on 05/02/2017.
 */

public class KramBookInitialize extends Application {
    private static String TAG = "KramBookInitialize";
    public static GlobalVariables global= new GlobalVariables();

    public void onCreate(){
        super.onCreate();
        Rollbar.init(this, "86b0d04719444f20b4ee02c9f4c846a8", "production");
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(UserData.class);
        ParseObject.registerSubclass(Colg.class);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(Activity.class);

        Parse.initialize(new Parse.Configuration.Builder(KramBookInitialize.this)
                .applicationId("c89bhRIVTVQ3mRqkErvNPhuY76yZJ5Qjgp3y8b79")
                .clientKey("eW7jYk0LGdq6GQc68vceVWnwoMMdxuaN4Ry8nJp4")
                .server("https://parseapi.back4app.com")
                .build()
        );

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        //defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        Log.i(TAG, "Parse initialized");


        ParsePush.subscribeInBackground("global", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
