package com.app.krambook.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.app.krambook.R;
import com.app.krambook.adapter.SuggestionAdapter;
import com.app.krambook.models.Colg;

import customfonts.MyAutoCompleteView;
import customfonts.MyEditText;

public class CollegeDetailsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private MyAutoCompleteView schoolSearch;
    private MyEditText schoolLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        schoolSearch = (MyAutoCompleteView) findViewById(R.id.school_autoComplete); //search school autocompleteView
        schoolSearch.setAdapter(new SuggestionAdapter(this, schoolSearch.getText().toString()));
        schoolLocation = (MyEditText) findViewById(R.id.school_location);
    }

    public void onSubmitHandler(View view) {
        Colg colgeDetails = new Colg();
        colgeDetails.setName(schoolSearch.getText().toString());
        Log.d("name", "CollgeLocation:" +  String.valueOf(schoolSearch.getText()));
        colgeDetails.setLocation(schoolLocation.getText().toString());
        colgeDetails.saveInBackground();
        startActivity(new Intent(getApplicationContext(), ChooseColgActivity.class));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
