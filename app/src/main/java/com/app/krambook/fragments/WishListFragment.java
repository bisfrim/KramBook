package com.app.krambook.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.activity.BookDetailActivity;
import com.app.krambook.activity.MainActivity;
import com.app.krambook.activity.MyProfileActivity;
import com.app.krambook.models.Favourteitem;
import com.app.krambook.utils.ImageLoaderGrid;
import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import customfonts.MyTextView;

/**
 * Created by hp1 on 21-01-2015.
 */
public class WishListFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    View rootView;
    public List<Favourteitem> data = null;
    private HomeItemListAdapter adapter;
    private MyTextView visibilty_view;
    GridView gridview;
    List<ParseObject> ob;
    ArrayList<String> follows;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        setRetainInstance(true);
        new RemoteDataTask().execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_wishlist,container,false);
        gridview=(GridView)rootView.findViewById(R.id.wish_gridList);
        visibilty_view = (MyTextView)rootView.findViewById(R.id.wishlist_view);
        follows = new ArrayList<String>();

        return rootView;
    }


    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            showProgressDialog();
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            data = new ArrayList<Favourteitem>();

            try {
                ParseQuery<ParseObject> photosFromCurrentUserQuery = ParseQuery.getQuery("Follow");
                photosFromCurrentUserQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                photosFromCurrentUserQuery.include("photo");
                ob = photosFromCurrentUserQuery.find();
                for (ParseObject country : ob) {
                    // Locate images in flag column

                    ParseObject photo=country.getParseObject("photo");
                    ParseFile image = (ParseFile) photo.get("image");
                    final Favourteitem map = new Favourteitem();

                    map.setImageID(photo.getObjectId());
                    map.setFollowID(country.getObjectId());
                    map.setInmageurl(image.getUrl());

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
//            gridview = (GridView) findViewById(R.id.save_gridView);
            // Pass the results into ListViewAdapter.java
            adapter = new HomeItemListAdapter(getActivity(),
                    data);
            // Binds the Adapter to the ListView
            gridview.setAdapter(adapter);
            if(gridview.getCount() == 0){
                visibilty_view.setVisibility(View.VISIBLE);
            }
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    data.get(position).getImageID();
                    Intent zoom = new Intent(getActivity(), BookDetailActivity.class); //CommentActivityClass
                    zoom.putExtra("photoID", data.get(position).getImageID());
                    startActivity(zoom);
                    // Toast.makeText(HomeActivity.this,worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();

//                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Unfollow Photo")
                            .setMessage("Are you sure you want to unfollow?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
                                    query.getInBackground(data.get(position).getFollowID(), new GetCallback<ParseObject>() {
                                                public void done(ParseObject country, ParseException e) {
                                                    if (e == null) {


                                                        try {
                                                            country.delete();
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }



//

                                                    }
                                                }
                                            }
                                    );


                                    ParseQuery<ParseObject> followquery = ParseQuery.getQuery("Photo");
                                    followquery.getInBackground(data.get(position).getImageID(), new GetCallback<ParseObject>() {
                                                public void done(ParseObject country, ParseException e) {
                                                    if (e == null) {


                                                        if (!(country.get("userfollow") == null)) {
                                                            follows = (ArrayList<String>) country.get("userfollow");
                                                        }
                                                        ParseUser likeuser = ParseUser.getCurrentUser();
                                                        follows.remove(likeuser.getObjectId());
                                                        country.put("userfollow", follows);

                                                        try {
                                                            country.save();
                                                        } catch (ParseException ee) {
                                                            ee.printStackTrace();
                                                        }
//

                                                    }
                                                }
                                            }
                                    );




                                    new RemoteDataTask().execute();

                                    // continue with delete
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
            // Close the progressdialog
            hideProgressDialog();
        }
    }


    public class HomeItemListAdapter extends BaseAdapter {
        boolean flag = true;
        Context context;
        LayoutInflater inflater;
        ImageLoaderGrid imageLoader;
        private ParseFile image;
        private List<Favourteitem> worldpopulationlist = null;
        private ArrayList<Favourteitem> arraylist;

        /**
         * Constructor from a list of items
         */
        public HomeItemListAdapter(Context context, List<Favourteitem> worldpopulationlist) {

            this.context = context;
            this.worldpopulationlist = worldpopulationlist;
            inflater = LayoutInflater.from(context);
            this.arraylist = new ArrayList<Favourteitem>();
            this.arraylist.addAll(worldpopulationlist);
            imageLoader = MyProfileActivity.imageLoader;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.favourits_item_layout, null);

                holder.productimageview = (ImageView) view.findViewById(R.id.product_portfolio_imageView);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


//            imageLoader.DisplayImage(worldpopulationlist.get(position).getInmageurl(), holder.productimageview);
            Glide.with(context).load(worldpopulationlist.get(position).getInmageurl()).into(holder.productimageview);



            // Restore the checked state properly
            final GridView lv = (GridView) parent;
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

        }
    }


    private void showProgressDialog() {
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }
}