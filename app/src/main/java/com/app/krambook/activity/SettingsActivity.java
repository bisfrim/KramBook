package com.app.krambook.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.krambook.R;
import com.app.krambook.models.SettingsItem;
import com.app.krambook.other.TimeAgo;
import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private HomeItemListAdapter adapter;

    ListView list;
    private List<SettingsItem> data = null;


    ListView listView;
    List<ParseObject> ob;
    TimeAgo timeAgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timeAgo = new TimeAgo(SettingsActivity.this);

        new RemoteDataTask().execute();
    }


    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            data = new ArrayList<SettingsItem>();
            Date regtime;
            long diftime;
            String diftimestr = null;
            int diftimeint;
            Date curenttime = new Date(System.currentTimeMillis());
            try {
                ParseQuery<ParseObject> photosFromCurrentUserQuery = ParseQuery.getQuery("Alert");
                photosFromCurrentUserQuery.whereEqualTo("touser", ParseUser.getCurrentUser());
                photosFromCurrentUserQuery.include("fromuser");
                photosFromCurrentUserQuery.include("photo");
                ob = photosFromCurrentUserQuery.find();
                for (ParseObject country : ob) {
                    // Locate images in flag column
                    ParseUser fromuser = country.getParseUser("fromuser");
                    ParseObject photo = country.getParseObject("photo");
                    ParseFile productimage = (ParseFile) photo.get("image");
                    ParseFile image = (ParseFile) fromuser.get("photo");

                    final SettingsItem map = new SettingsItem();
//                    if(!(image ==null)){
//                        map.setUserimageurl(image.getUrl());
//                    }
//
//                    else {
//                        map.setUserimageurl("");
//                    }
//                    //


                    if (!(fromuser.get("photo") == null)) {
                        ParseFile userimage = (ParseFile) fromuser.get("photo");
                        map.setUserimageurl(userimage.getUrl());

                    }


                    //
                    map.setUsername((String) fromuser.get("name"));
                    map.setProductimageurl(productimage.getUrl());
                    map.setID(photo.getObjectId());
                    map.setComment((String) country.get("contente"));
                    regtime = country.getCreatedAt();
//                    diftime=curenttime.getTime()-regtime.getTime();
//                    if(diftime>60000&&diftime<=3600000){
//                        diftimeint= (int) (diftime/(60*1000));//min
//                        diftimestr=Integer.toString(diftimeint)+" min";
//                    }
//                    if(diftime>3600000&&diftime<=86400000){
//                        diftimeint= (int) (diftime/(3600*1000));//hours
//                        diftimestr=Integer.toString(diftimeint)+" hours";
//                    }
//                    if(diftime>86400000){
//                        diftimeint=(int)(diftime/1000 / 60 / 60 / 24);//days
//                        diftimestr=Integer.toString(diftimeint)+" days";
//                    }
                    diftimestr = timeAgo.timeAgo(regtime);
                    map.setTime(diftimestr);


                    data.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listView = (ListView) findViewById(R.id.alert_listiview);
            // Pass the results into ListViewAdapter.java
            adapter = new HomeItemListAdapter(SettingsActivity.this,
                    data);
            // Binds the Adapter to the ListView
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    data.get(position).getID();
                    Intent zoom = new Intent(SettingsActivity.this, MyProfileActivity.class);
                    zoom.putExtra("photoID", data.get(position).getID());
                    startActivity(zoom);
                    // Toast.makeText(HomeActivity.this,worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();

                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            // Close the progressdialog
            //mProgressDialog.dismiss();
            hideProgressDialog();
        }

    }


    public class HomeItemListAdapter extends BaseAdapter {
        boolean flag = true;
        Context context;
        LayoutInflater inflater;
        //CommentImageLoader imageLoader;
        private ParseFile image;
        private List<SettingsItem> worldpopulationlist = null;
        private ArrayList<SettingsItem> arraylist;

        /**
         * Constructor from a list of items
         */
        public HomeItemListAdapter(Context context, List<SettingsItem> worldpopulationlist) {

            this.context = context;
            this.worldpopulationlist = worldpopulationlist;
            inflater = LayoutInflater.from(context);
            this.arraylist = new ArrayList<SettingsItem>();
            this.arraylist.addAll(worldpopulationlist);
            //imageLoader = new CommentImageLoader(context);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.settings_item, null);

                holder.productimageview = (ImageView) view.findViewById(R.id.product_alertimageView);
                holder.userphotoimageview = (ImageView) view.findViewById(R.id.user_alertimageView);
                holder.usernametextview = (TextView) view.findViewById(R.id.username_alerttextView);
                holder.contenttextview = (TextView) view.findViewById(R.id.commet_alerttextView);
                holder.timetextview = (TextView) view.findViewById(R.id.time_alertextView);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


//            imageLoader.DisplayImage(worldpopulationlist.get(position).getProductimageurl(), holder.productimageview);
            Glide.with(context).load(worldpopulationlist.get(position).getProductimageurl()).into(holder.productimageview);
            if (!worldpopulationlist.get(position).getUserimageurl().equals("")) {
//                imageLoader.DisplayImage(worldpopulationlist.get(position).getUserimageurl(), holder.userphotoimageview);
                Glide.with(context).load(worldpopulationlist.get(position).getUserimageurl()).into(holder.userphotoimageview);
            } else {
                holder.userphotoimageview.setImageResource(R.drawable.man);
            }
            holder.usernametextview.setText(worldpopulationlist.get(position).getUsername());
            //holder.usernametextview.setTypeface(font);
            holder.contenttextview.setText(worldpopulationlist.get(position).getComment());
            //holder.contenttextview.setTypeface(font);
            holder.timetextview.setText(worldpopulationlist.get(position).getTime());
            //holder.timetextview.setTypeface(font);


            // Restore the checked state properly
            final ListView lv = (ListView) parent;
//        holder.layout.setChecked(lv.isItemChecked(position));

            return view;
        }

        @Override
        public int getCount() {
            return worldpopulationlist.size();
        }

        @Override
        public Object getItem(int position) {
            return worldpopulationlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {

            public ImageView productimageview;
            public ImageView userphotoimageview;
            public TextView usernametextview;
            public TextView contenttextview;
            public TextView timetextview;

        }
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

    private void showProgressDialog() {
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

}
