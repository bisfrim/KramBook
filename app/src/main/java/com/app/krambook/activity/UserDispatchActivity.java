package com.app.krambook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.app.krambook.models.UserData;
import com.parse.ParseUser;

/**
 * Created by bismark.frimpong on 12/7/2015.
 */
public class UserDispatchActivity extends Activity {
    private static String DISPATCHER = "Dispatcher";
    private UserData mCurrentUser;

    public UserDispatchActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //check the current parse user info
        if(ParseUser.getCurrentUser() != null){
            // Log the user into the main activity
           /* mCurrentUser = (User) User.getCurrentUser();
            mCurrentUser.setOnline(true);
            mCurrentUser.saveInBackground();*/
            ParseUser user = ParseUser.getCurrentUser();
            if(user.getBoolean("isComplete") == true){
                if(user.getBoolean("emailVerified") == false){
                    user.logOut();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You have not yet verified. Please check your email")
                            .setTitle("Verification Pending!")
                            .setCancelable(false)
                            .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(UserDispatchActivity.this, ChooseColgActivity.class); //->LoginActivity
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    startActivity(new Intent(UserDispatchActivity.this, MainActivity.class)); //ChooseColgActivity -> MainActivity
                }

            }else{
                startActivity(new Intent(UserDispatchActivity.this, SignUpDetails.class));
            }
        } else{
            // start an intent for this logged out user
            startActivity(new Intent(this, ChooseColgActivity.class)); //LoginActivity <-(original)
        }
    }

    public void onResume() {
        super.onResume();
        if(!isNetworkAvailable()) {
            Log.d(DISPATCHER, "Network is unavailable!");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivitymanager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnected();
    }
}
