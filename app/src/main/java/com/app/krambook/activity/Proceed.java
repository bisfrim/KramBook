package com.app.krambook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.krambook.R;
import com.parse.ParseUser;

public class Proceed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ParseUser.getCurrentUser().getBoolean("emailVerified") == true) {
            startActivity(new Intent(Proceed.this, ChooseColgActivity.class)); ////ChooseColgActivity -> MainActivity
        }
        else{
            ParseUser.getCurrentUser().logOutInBackground();
            startActivity(new Intent(Proceed.this, UserDispatchActivity.class));
        }
    }

}
