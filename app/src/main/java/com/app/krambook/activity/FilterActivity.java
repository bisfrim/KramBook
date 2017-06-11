package com.app.krambook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.app.krambook.R;
import com.app.krambook.fragments.HomeFragment;
import com.app.krambook.models.Filter;
import com.app.krambook.other.CheckableRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends Activity {
    ListView list;
    private List<Filter> data;
    private FilterListAdapter adapter;
    StringBuffer buffer;
    boolean flag=true;
    Typeface font;
    Button selectbut;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    String filterindex;
    String filtercontente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        preferences= getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        editor= preferences.edit();
        filterindex= preferences.getString("index","");
        data = new ArrayList<Filter>();
        data.add(new Filter(1,"Automotive"));
        data.add(new Filter(2,"Books & Magazines"));
        data.add(new Filter(3,"Business & Office"));
        data.add(new Filter(4,"Computing"));
        data.add(new Filter(5,"Education"));
        data.add(new Filter(6,"Electrical & Electronics"));
        data.add(new Filter(7,"Entertainment"));
        data.add(new Filter(8,"Fashion & Apparel"));
        data.add(new Filter(9,"Financial"));
        data.add(new Filter(10,"Food & Grocery"));
        data.add(new Filter(11,"Gaming"));
        data.add(new Filter(12,"Health & Beauty"));
        data.add(new Filter(13,"Home & Garden"));
        data.add(new Filter(14,"Internet"));
        data.add(new Filter(15,"Mobile"));
        data.add(new Filter(16,"Pets"));
        data.add(new Filter(17,"Restaurants & Dining"));
        data.add(new Filter(18,"Sports & Outdoors"));
        data.add(new Filter(19,"Toys & Kids"));
        data.add(new Filter(20,"Travel"));
        data.add(new Filter(21,"Other"));


        list = (ListView) findViewById(R.id.filter_listView);
     //  list.setSelector(R.drawable.itemselect);
        adapter = new FilterListAdapter(this, data);
        list.setAdapter(adapter);
        if (!filterindex.equals("")){

            String[]indexarray=filterindex.split(",");
            for (int i=0;i<indexarray.length;i++){

                list.setItemChecked(Integer.parseInt(indexarray[i]),true);
            }

        }
        else {

            for(int i = 0; i<=20; i++){
                list.setItemChecked(i, true);
            }

        }

//        selectbut=(Button)findViewById(R.id.select_button);

        font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        TextView filter_title=(TextView)findViewById(R.id.filter_title_textView);
        filter_title.setTypeface(font);
//        selectbut.setTypeface(font);
//       //list.setItemsCanFocus(false);
//
//        selectbut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                flag=!flag;
//                if(flag==true){
//
//                    selectbut.setText("Select All");
//                    for(int i = 0; i<=20; i++){
//                        list.setItemChecked(i, false);
//                    }
//
//                }
//                else {
//                    selectbut.setText("Unselect All");
//                    int size = list.getCount();
////                    if(list.isItemChecked(0)){
//                    for(int i = 0; i<=20; i++){
//                        list.setItemChecked(i, true);
//                    }
////                    } else {
////                        for(int i = 0; i<=size; i++){
////                            list.setItemChecked(i, true);
////                        }
////                    }
//                }
//
//        }});

        final ImageButton checkimagebutton=(ImageButton)findViewById(R.id.selelct_imageButton);
        checkimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuffer sb = new StringBuffer();
                final StringBuffer filterbufer=new StringBuffer();
                final SparseBooleanArray checkedItems = list.getCheckedItemPositions();

                boolean isFirstSelected = true;
                String ctagory = null;
                String filter_selecindex;
                final int checkedItemsCount = checkedItems.size();
                for (int i = 0; i < checkedItemsCount; ++i) {
                    final int position = checkedItems.keyAt(i);

                    // This tells us the item status at the above position
                    // --
                    final boolean isChecked = checkedItems.valueAt(i);

                    if (isChecked) {

                        if (!isFirstSelected) {
                            sb.append(",");
                            filterbufer.append(",");
                        }





                        sb.append(data.get(position).getFiltername());
                        filterbufer.append(position);
                        isFirstSelected = false;
                    }
                }
                ctagory=sb.toString();
                filter_selecindex=filterbufer.toString();

                Intent intent;
                intent = new Intent(FilterActivity.this, MainActivity.class);
                intent.putExtra("Filter",ctagory);
                editor.putString("index",filter_selecindex);
                editor.putString("Filter",ctagory);
                editor.commit();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });
        //ImageButton fillterButton = (ImageButton) findViewById(R.id.home_filter_imageButton);

     /*   fillterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final StringBuffer sb = new StringBuffer();
                final SparseBooleanArray checkedItems = list.getCheckedItemPositions();

                boolean isFirstSelected = true;
                String ctagory = null;
                final int checkedItemsCount = checkedItems.size();
                for (int i = 0; i < checkedItemsCount; ++i) {
                    final int position = checkedItems.keyAt(i);

                    // This tells us the item status at the above position
                    // --
                    final boolean isChecked = checkedItems.valueAt(i);

                    if (isChecked) {

                        if (!isFirstSelected) {
                            sb.append(",");
                        }





                        sb.append(data.get(position).getFiltername());

                        isFirstSelected = false;
                    }
                }
                ctagory=sb.toString();
                if(!flag){
                    ctagory="all";
                }
                Intent intent;
                intent = new Intent(FilterActivity.this, HomeFragment.class);
                intent.putExtra("Filter",ctagory);

                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);

            }
        });
*/

//        ImageButton saveButton = (ImageButton) findViewById(R.id.save_filter_imageButton);
//
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final StringBuffer sb = new StringBuffer();
//                final SparseBooleanArray checkedItems = list.getCheckedItemPositions();
//
//                boolean isFirstSelected = true;
//                String ctagory = null;
//                final int checkedItemsCount = checkedItems.size();
//                for (int i = 0; i < checkedItemsCount; ++i) {
//                    final int position = checkedItems.keyAt(i);
//
//                    // This tells us the item status at the above position
//                    // --
//                    final boolean isChecked = checkedItems.valueAt(i);
//
//                    if (isChecked) {
//
//                        if (!isFirstSelected) {
//                            sb.append(",");
//                        }
//
//
//
//
//
//                        sb.append(data.get(position).getFiltername());
//
//                        isFirstSelected = false;
//                    }
//                }
//                ctagory=sb.toString();
//                if(!flag){
//                    ctagory="all";
//                }
//                Intent intent;
//                intent = new Intent(FilterActivity.this, HomeActivity.class);
//                intent.putExtra("Filter",ctagory);
//
//                startActivity(intent);
//                overridePendingTransition(R.anim.left_in, R.anim.right_out);
//
//            }
//        });



        ImageButton backbut = (ImageButton) findViewById(R.id.back_fillter_imageButton);

        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final StringBuffer sb = new StringBuffer();
                final SparseBooleanArray checkedItems = list.getCheckedItemPositions();
                boolean isFirstSelected = true;
                String ctagory = null;
                final int checkedItemsCount = checkedItems.size();
                for (int i = 0; i < checkedItemsCount; ++i) {
                    final int position = checkedItems.keyAt(i);

                    // This tells us the item status at the above position
                    // --
                    final boolean isChecked = checkedItems.valueAt(i);

                    if (isChecked) {

                        if (!isFirstSelected) {
                            sb.append(",");
                        }





                        sb.append(data.get(position).getFiltername());

                        isFirstSelected = false;
                    }
                }
                ctagory=sb.toString();
                if(!flag){
                    ctagory="all";
                }
                Intent intent;
                intent = new Intent(FilterActivity.this, MainActivity.class);
                intent.putExtra("Filter",ctagory);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);


            }
        });
    }


    public class FilterListAdapter extends ArrayAdapter<Filter> {

        private LayoutInflater li;

        /**
         * Constructor from a list of items
         */
        public FilterListAdapter(Context context, List<Filter> items) {
            super(context, 0, items);
            li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // The item we want to get the view for
            // --
            final Filter item = getItem(position);

            // Re-use the view if possible
            // --
            ViewHolder holder;
            if (convertView == null) {
                convertView = li.inflate(R.layout.filter_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(R.id.holder, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.id.holder);
            }

            // Set some view properties
            holder.filtertext.setTypeface(font);
            holder.filtertext.setText("" + item.getFiltername());


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

        private  class ViewHolder {
            public TextView filtertext;
            public CheckableRelativeLayout layout;

            public ViewHolder(View root) {
                filtertext= (TextView) root.findViewById(R.id.filter_textView);

                layout = (CheckableRelativeLayout) root.findViewById(R.id.layout);
            }
        }
    }


}
