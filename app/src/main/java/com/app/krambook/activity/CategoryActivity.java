package com.app.krambook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.app.krambook.R;
import com.app.krambook.models.Catagorysearchitem;
import com.app.krambook.other.CheckableRelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class CategoryActivity extends Activity {
    String fileuri;
    String tagling;
    String retailerstr, title, isbn, author, price, condition;
    ListView list;
    ImageButton category_save_imagebutton;
    private List<Catagorysearchitem> data;
    private CatagoryListAdapter adapter;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Intent datagetintent = getIntent();
        fileuri = datagetintent.getStringExtra("serchresult");
        tagling = datagetintent.getStringExtra("tagline");
        retailerstr = datagetintent.getStringExtra("retailer");
        title = datagetintent.getStringExtra("title");
        isbn = datagetintent.getStringExtra("isbn");
        author = datagetintent.getStringExtra("author");
        price = datagetintent.getStringExtra("price");
        condition = datagetintent.getStringExtra("condition");
        data = new ArrayList<Catagorysearchitem>();
        data.add(new Catagorysearchitem(1, "Automotive"));
        data.add(new Catagorysearchitem(2, "Books & Magazines"));
        data.add(new Catagorysearchitem(3, "Business & Office"));
        data.add(new Catagorysearchitem(4, "Computing"));
        data.add(new Catagorysearchitem(5, "Education"));
        data.add(new Catagorysearchitem(6, "Electrical & Electronics"));
        data.add(new Catagorysearchitem(7, "Entertainment"));
        data.add(new Catagorysearchitem(8, "Fashion & Apparel"));
        data.add(new Catagorysearchitem(9, "Financial"));
        data.add(new Catagorysearchitem(10, "Food & Grocery"));
        data.add(new Catagorysearchitem(11, "Gaming"));
        data.add(new Catagorysearchitem(12, "Health & Beauty"));
        data.add(new Catagorysearchitem(13, "Home & Garden"));
        data.add(new Catagorysearchitem(14, "Internet"));
        data.add(new Catagorysearchitem(15, "Mobile"));
        data.add(new Catagorysearchitem(16, "Pets"));
        data.add(new Catagorysearchitem(17, "Restaurants & Dining"));
        data.add(new Catagorysearchitem(18, "Sports & Outdoors"));
        data.add(new Catagorysearchitem(19, "Toys & Kids"));
        data.add(new Catagorysearchitem(20, "Travel"));
        data.add(new Catagorysearchitem(21, "Other"));
        font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        TextView tile = (TextView) findViewById(R.id.category_title_textView);
        tile.setTypeface(font);
        list = (ListView) findViewById(R.id.categorylistView);
        //  list.setSelector(R.drawable.itemselect);
        adapter = new CatagoryListAdapter(this, data);
        list.setAdapter(adapter);
        //list.setItemsCanFocus(false);
        category_save_imagebutton = (ImageButton) findViewById(R.id.category_save_imageButton);
        category_save_imagebutton.setOnClickListener(new View.OnClickListener() {
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

                        ctagory = data.get(position).getCatagory();
                        isFirstSelected = false;
                    }
                }
                Intent intent;
                intent = new Intent(CategoryActivity.this, EnterBookInfoActivity.class);
                intent.putExtra("serchresult", fileuri);
                intent.putExtra("cotagoryresult", ctagory);
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
        });
        ImageButton fillterButton = (ImageButton) findViewById(R.id.categoyr_backbutton);

        fillterButton.setOnClickListener(new View.OnClickListener() {
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

                        ctagory = data.get(position).getCatagory();
                        isFirstSelected = false;
                    }
                }
                Intent intent;
                intent = new Intent(CategoryActivity.this, EnterBookInfoActivity.class);
                intent.putExtra("serchresult", fileuri);
                intent.putExtra("cotagoryresult", ctagory);
                intent.putExtra("tagline", tagling);
                intent.putExtra("retailer", retailerstr);
                intent.putExtra("title", title);
                intent.putExtra("isbn", isbn);
                intent.putExtra("author", author);
                intent.putExtra("price", price);

                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);

            }
        });


    }

    public class CatagoryListAdapter extends ArrayAdapter<Catagorysearchitem> {


        private LayoutInflater li;

        /**
         * Constructor from a list of items
         */
        public CatagoryListAdapter(Context context, List<Catagorysearchitem> items) {
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


}
