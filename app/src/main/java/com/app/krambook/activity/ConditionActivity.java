package com.app.krambook.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.models.Catagorysearchitem;
import com.app.krambook.other.CheckableRelativeLayout;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConditionActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private List<Catagorysearchitem> data;
    private ConditionListAdapter conditionAdapter;
    String fileuri;
    String tagling;
    String retailerstr,title,isbn,author,price,categorys;
    ListView mList;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent dataCategoryIntent = getIntent();
        fileuri = dataCategoryIntent.getStringExtra("serchresult");
        categorys = dataCategoryIntent.getStringExtra("cotagoryresult");
        tagling=dataCategoryIntent.getStringExtra("tagline");
        retailerstr=dataCategoryIntent.getStringExtra("retailer");
        title = dataCategoryIntent.getStringExtra("title");
        isbn = dataCategoryIntent.getStringExtra("isbn");
        author = dataCategoryIntent.getStringExtra("author");
        price = dataCategoryIntent.getStringExtra("price");

        data = new ArrayList<Catagorysearchitem>();
        data.add(new Catagorysearchitem(1, "New"));
        data.add(new Catagorysearchitem(2, "Like New"));
        data.add(new Catagorysearchitem(3, "Used"));
        data.add(new Catagorysearchitem(4, "Other"));
        font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");

        mList = (ListView) findViewById(R.id.condition_listView);
        //  list.setSelector(R.drawable.itemselect);
        conditionAdapter = new ConditionListAdapter(this, data);
        mList.setAdapter(conditionAdapter);
    }


    public class ConditionListAdapter extends ArrayAdapter<Catagorysearchitem>{

        private LayoutInflater li;

        /**
         * Constructor from a list of items
         */
        public ConditionListAdapter(Context context, List<Catagorysearchitem> items) {
            super(context, 0, items);
            li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // The item we want to get the view for
            // --
            final Catagorysearchitem item = getItem(position);

            // Re-use the view if possible
            // --
            ViewHolder holder;
            if (convertView == null) {
                convertView = li.inflate(R.layout.catagory_itemlayout, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(R.id.holder, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.id.holder);
            }

            // Set some view properties
            font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
            holder.filtertext.setText("" + item.getCatagory());
            holder.filtertext.setTypeface(font);

            // Restore the checked state properly
            final ListView lv = (ListView) parent;
            holder.layout.setChecked(lv.isItemChecked(position));

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private class ViewHolder {
            public TextView filtertext;
            public CheckableRelativeLayout layout;

            public ViewHolder(View root) {
                filtertext = (TextView) root.findViewById(R.id.filter_textView);

                layout = (CheckableRelativeLayout) root.findViewById(R.id.layout);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_enterbook_complete, menu);

        return true;
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

            final StringBuffer sb = new StringBuffer();
            final SparseBooleanArray checkedItems = mList.getCheckedItemPositions();

            boolean isFirstSelected = true;
            String ctagory = null;
            final int checkedItemsCount = checkedItems.size();
            for (int i = 0; i < checkedItemsCount; ++i) {
                final int position = checkedItems.keyAt(i);

                // This tells us the item status at the above position
                // --
                final boolean isChecked = checkedItems.valueAt(i);

                if (isChecked) {

                    ctagory = data.get(position).getCatagory();
                    isFirstSelected = false;
                }
            }
            Intent intent;
            intent = new Intent(ConditionActivity.this, EnterBookInfoActivity.class);
            intent.putExtra("serchresult", fileuri);
            intent.putExtra("condition", ctagory);
            intent.putExtra("cotagoryresult",categorys);
            intent.putExtra("tagline", tagling);
            intent.putExtra("retailer", retailerstr);
            intent.putExtra("title", title);
            intent.putExtra("isbn", isbn);
            intent.putExtra("author", author);
            intent.putExtra("price", price);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);



            onBackPressed();
            return true;
        }

        if (id == R.id.save_book_info) {
            //Toast.makeText(this, "All notifications marked as read!", Toast.LENGTH_LONG).show();

            final StringBuffer sb = new StringBuffer();
            final SparseBooleanArray checkedItems = mList.getCheckedItemPositions();

            boolean isFirstSelected = true;
            String ctagory = null;
            final int checkedItemsCount = checkedItems.size();
            for (int i = 0; i < checkedItemsCount; ++i) {
                final int position = checkedItems.keyAt(i);

                // This tells us the item status at the above position
                // --
                final boolean isChecked = checkedItems.valueAt(i);

                if (isChecked) {

                    ctagory = data.get(position).getCatagory();
                    isFirstSelected = false;
                }
            }
            Intent intent;
            intent = new Intent(ConditionActivity.this, EnterBookInfoActivity.class);
            intent.putExtra("serchresult", fileuri);
            intent.putExtra("condition", ctagory);
            intent.putExtra("cotagoryresult",categorys);
            intent.putExtra("tagline", tagling);
            intent.putExtra("retailer", retailerstr);
            intent.putExtra("title", title);
            intent.putExtra("isbn", isbn);
            intent.putExtra("author", author);
            intent.putExtra("price", price);
            //intent.putExtra("conditoin", condition);

            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);


        }

        return super.onOptionsItemSelected(item);
    }

}
