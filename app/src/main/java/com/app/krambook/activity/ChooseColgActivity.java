package com.app.krambook.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.app.krambook.fragments.MyListingFragment;
import com.app.krambook.models.Colg;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.games.video.Videos;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import customfonts.MyTextView;

public class ChooseColgActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView listView;
    private ArrayList<Colg> mColgs;
    private Colgadapter mAdapter;
    private ProgressDialog mProgressDialog;
    private MyTextView collgeView;
    public String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_colg);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        collgeView = (MyTextView)findViewById(android.R.id.empty);
        setSupportActionBar(mToolbar);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        listView = (ListView) findViewById(R.id.list);
        mColgs = new ArrayList<Colg>();
        mAdapter = new Colgadapter(ChooseColgActivity.this,   mColgs);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(collgeView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (listView.getItemAtPosition(myItemInt));
                Log.d("SelectedItem", "listview" + String.valueOf(selectedFromList));
                Toast.makeText(getApplicationContext(), selectedFromList, Toast.LENGTH_LONG).show();

            }
        });

        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                new AlertDialog.Builder(ChooseColgActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this school?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Colg");
                                query.getInBackground(mColgs.get(position).getColgId(), new GetCallback<ParseObject>() {
                                            public void done(ParseObject college, ParseException e) {
                                                if (e == null) {
                                                    try {
                                                        college.delete();
                                                    } catch (ParseException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                );  // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
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
        showProgressDialog();

        query.findInBackground(new FindCallback<Colg>() {
            @Override
            public void done(List<Colg> messages, ParseException e) {
                hideProgressDialog();
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


    private void showProgressDialog() {
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }
}
