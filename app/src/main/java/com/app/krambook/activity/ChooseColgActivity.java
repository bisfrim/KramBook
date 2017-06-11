package com.app.krambook.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.adapter.Colgadapter;
import com.app.krambook.models.Colg;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ChooseColgActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView listView;
    private ArrayList<Colg> mColgs;
    private Colgadapter mAdapter;
    public String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_colg);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        listView = (ListView) findViewById(R.id.list);
        mColgs = new ArrayList<Colg>();
        mAdapter = new Colgadapter(ChooseColgActivity.this,   mColgs);
        listView.setAdapter(mAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (listView.getItemAtPosition(myItemInt));
                Log.d("SelectedItem", "listview" + String.valueOf(selectedFromList));
                Toast.makeText(getApplicationContext(), selectedFromList, Toast.LENGTH_LONG).show();

            }
        });

        receiveColg();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseColgActivity.this, CollegeDetailsActivity.class));
            }
        });




    }


    private void receiveColg() {
        ParseQuery<Colg> query = ParseQuery.getQuery(Colg.class);
        query.setLimit(100);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<Colg>() {
            @Override
            public void done(List<Colg> messages, ParseException e) {
                if (e == null) {
                    mColgs.clear();
                    mColgs.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    listView.invalidate();//allows for the listview to be redrawn
                } else {
                    Log.v("Error:", "Error:" + e.getMessage());
                }
            }
        });

    }
}
