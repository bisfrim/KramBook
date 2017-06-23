package com.app.krambook.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.activity.BookDetailActivity;
import com.app.krambook.activity.FilterActivity;
import com.app.krambook.activity.MyProfileActivity;
import com.app.krambook.models.Homeitem;
import com.app.krambook.models.Photo;
import com.app.krambook.models.UserData;
import com.app.krambook.other.CircleTransform;
import com.app.krambook.utils.FileCache;
import com.app.krambook.utils.ImageLoader;
import com.app.krambook.utils.ImageLoaderGrid;
import com.app.krambook.utils.MemoryCache;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    public List<Homeitem> data = null;
    private HomeItemListAdapter adapter;
    private Myadapter mainadapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListView list;
    public String curentusername;
    StringBuffer buf;
    String test;
    List<ParseObject> productlist;
    private String username;
    private AdView adView;
    View rootView;

    GridView listview;
    List<ParseObject> ob;
    List<ParseObject> com_ob;
    List<ParseObject> folllow_ob;
    List<ParseObject> like_ob;
    ParseObject photo;
    ParseObject photofollow;
    ParseUser homeuser;
    Photo product;
    String fillterstring;
    String retailerstring;
    ArrayList<String> limits;
    int setlimite = 10;
    Typeface font;
    ArrayList<String> likes;
    ArrayList<String> follows;
    UserData currentuser;
    String userid;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    String filtercontente;
    ParseUser search_user;
    String photoID;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));


        preferences = this.getActivity().getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        editor = preferences.edit();
        filtercontente = preferences.getString("filter", "");
        new FileCache(getActivity()).clear();
        new MemoryCache().clear();
        likes = new ArrayList<String>();
        follows = new ArrayList<String>();
        Intent filterintent = this.getActivity().getIntent();
        fillterstring = filterintent.getStringExtra("filter");
        userid = filterintent.getStringExtra("userID");
        photoID = filterintent.getStringExtra("photoID");
        font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Questrial-Regular.ttf");
        currentuser = (UserData) UserData.getCurrentUser();

        if (!filtercontente.equals("")) {

            limits = new ArrayList<String>();
            String[] names = filtercontente.split(",");
            for (int i = 0; i < names.length; i++) {
                limits.add(names[i]);

            }
        } else if (!(fillterstring == null)) {

            limits = new ArrayList<String>();
            String[] names = fillterstring.split(",");
            for (int i = 0; i < names.length; i++) {
                limits.add(names[i]);
            }
        }

        retailerstring = filterintent.getStringExtra("retailer");
        username = UserData.getCurrentUser().getUsername();
        product = new Photo();
        buf = new StringBuffer();

        if (!(userid == null)) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                search_user = query.get(userid);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        new RemoteDataTask().execute();
        setRetainInstance(true);
        //setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        listview = (GridView) rootView.findViewById(android.R.id.list);


        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadUpdatedData();
            }
        });

        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);*/



        return rootView;
    }


    private void LoadUpdatedData(){
        new LoadMoreDataTask().execute();
    }

    //Fetch items
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            // mProgressDialog.setTitle("Message");
            // Set progressdialog message
            //mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
            showProgressDialog();
            // Show progressdialog
//            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            data = new ArrayList<Homeitem>();
            if (!(photoID == null)) {

                Log.d("photoID", photoID);
                ParseQuery<ParseObject> photoQuery = ParseQuery.getQuery("Photo");


                photoQuery.include("user");


                photoQuery.getInBackground(photoID, new GetCallback<ParseObject>() {
                    public void done(ParseObject country, ParseException e) {
                        if (e == null) {
                            photo = country;
                            final Homeitem map = new Homeitem();
                            int inappcount = country.getInt("inapp");
                            if (inappcount < 4) {

                                Date today = new Date(System.currentTimeMillis());
                                Date c = (Date) country.get("expiry");


                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

                                String formattedDate = df.format(c);
                                if (today.compareTo(c) == 0) {
                                    map.setExpirdate("EXPIRED ");
                                    ParseFile image = (ParseFile) country.get("image");
                                    ParseUser homeuser = country.getParseUser("user");


                                    map.setUserID(homeuser.getObjectId());
                                    map.setID(country.getObjectId());
                                    map.setusername((String) homeuser.get("name"));
                                    if (!((String) homeuser.get("name") == null)) {
                                        map.setUserrealname((String) homeuser.get("name"));
                                    } else {
                                        map.setUserrealname("");
                                    }


                                    map.setCategory((String) country.get("category"));
                                    map.setTagline((String) country.get("tagline"));


                                    if (!(homeuser.get("photo") == null)) {
                                        ParseFile userimage = (ParseFile) homeuser.get("photo");
                                        map.setUserimageurl(userimage.getUrl());
                                    }


                                    map.setIamgeURL(image.getUrl());
                                    //map.setLocation((String) country.get("location"));
                                    map.setRetail((String) country.get("retail"));
                                    if (!(country.<String>getList("userlike") == null)) {
                                        likes = (ArrayList<String>) country.get("userlike");
                                    }
                                    if (!(country.<String>getList("userfollow") == null)) {
                                        follows = (ArrayList<String>) country.get("userfollow");

                                    }
                                    map.setComment(Integer.toString(country.getInt("commentnumber")));
                                    map.setLike(Integer.toString(likes.size()));
                                    String likecompare = likes.toString();
                                    if (likecompare.contains(currentuser.getObjectId())) {
                                        map.setLikeflag("true");
                                    } else {
                                        map.setLikeflag("false");
                                    }
                                    likes.clear();

                                    String followcompare = follows.toString();
                                    if (followcompare.contains(currentuser.getObjectId())) {
                                        map.setFollowflag("true");
                                    } else {
                                        map.setFollowflag("false");
                                    }
                                    follows.clear();
                                    data.add(map);
                                } else if (today.compareTo(c) < 0) {

                                    map.setExpirdate("EXP: " + formattedDate);
                                    ParseFile image = (ParseFile) country.get("image");
                                    ParseUser homeuser = country.getParseUser("user");


                                    map.setUserID(homeuser.getObjectId());
                                    map.setID(country.getObjectId());
                                    map.setusername((String) homeuser.get("name"));
                                    if (!((String) homeuser.get("name") == null)) {
                                        map.setUserrealname((String) homeuser.get("name"));
                                    } else {
                                        map.setUserrealname("");
                                    }


                                    map.setCategory((String) country.get("category"));
                                    map.setTagline((String) country.get("tagline"));


                                    if (!(homeuser.get("photo") == null)) {
                                        ParseFile userimage = (ParseFile) homeuser.get("photo");
                                        map.setUserimageurl(userimage.getUrl());
                                    }


                                    map.setIamgeURL(image.getUrl());
                                    //map.setLocation((String) country.get("location"));
                                    map.setRetail((String) country.get("retail"));
                                    if (!(country.<String>getList("userlike") == null)) {
                                        likes = (ArrayList<String>) country.get("userlike");
                                    }
                                    if (!(country.<String>getList("userfollow") == null)) {
                                        follows = (ArrayList<String>) country.get("userfollow");

                                    }
                                    map.setComment(Integer.toString(country.getInt("commentnumber")));
                                    map.setLike(Integer.toString(likes.size()));
                                    String likecompare = likes.toString();
                                    if (likecompare.contains(currentuser.getObjectId())) {
                                        map.setLikeflag("true");
                                    } else {
                                        map.setLikeflag("false");
                                    }
                                    likes.clear();

                                    String followcompare = follows.toString();
                                    if (followcompare.contains(currentuser.getObjectId())) {
                                        map.setFollowflag("true");
                                    } else {
                                        map.setFollowflag("false");
                                    }
                                    follows.clear();
                                    data.add(map);

                                }


                            }


//                    ParseQuery<ParseObject> inappequery = ParseQuery.getQuery("Inappropriate");
//                    inappequery.whereEqualTo("photo", photo);


//                    if(inappequery.count()>=3){
//                       photo.deleteInBackground();
//                    }
//                    else {


//                    }
                        }

                    }
                });

                // Set the limit of objects to show
//                photosFromCurrentUserQuery.setLimit(setlimite);


            } else {


                try {
                    ParseQuery<ParseObject> photosFromCurrentUserQuery = ParseQuery.getQuery("Photo");
                    if (!(fillterstring == null)) {

                        photosFromCurrentUserQuery.whereContainedIn("category", limits);
//                    photosFromCurrentUserQuery.whereEqualTo("category",fillterstring);
                    }
                    if (!(retailerstring == null)) {

                        photosFromCurrentUserQuery.whereEqualTo("retail", retailerstring);
                    }
                    if (!(search_user == null)) {

                        photosFromCurrentUserQuery.whereEqualTo("user", search_user);
                    }
                    photosFromCurrentUserQuery.whereExists("image");
                    photosFromCurrentUserQuery.whereEqualTo("expflag", "false");
                    photosFromCurrentUserQuery.include("user");
                    photosFromCurrentUserQuery.orderByDescending("createdAt");
                    // Set the limit of objects to show
//                photosFromCurrentUserQuery.setLimit(setlimite);
                    ob = photosFromCurrentUserQuery.find();
                    for (ParseObject country : ob) {
                        // Locate images in flag column
                        photo = country;
                        final Homeitem map = new Homeitem();
                        int inappcount = country.getInt("inapp");
                        if (inappcount < 4) {

                            Date today = new Date(System.currentTimeMillis());
                            Date c = (Date) country.get("expiry");


                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

                            String formattedDate = df.format(c);
                            if (today.compareTo(c) == 0) {
                                map.setExpirdate("EXPIRED ");
                                ParseFile image = (ParseFile) country.get("image");
                                ParseUser homeuser = country.getParseUser("user");


                                map.setUserID(homeuser.getObjectId());
                                map.setID(country.getObjectId());
                                map.setusername((String) homeuser.get("name"));
                                if (!((String) homeuser.get("name") == null)) {
                                    map.setUserrealname((String) homeuser.get("name"));
                                } else {
                                    map.setUserrealname("");
                                }


                                map.setCategory((String) country.get("category"));
                                map.setTagline((String) country.get("tagline"));


                                if (!(homeuser.get("photo") == null)) {
                                    ParseFile userimage = (ParseFile) homeuser.get("photo");
                                    map.setUserimageurl(userimage.getUrl());
                                }


                                map.setIamgeURL(image.getUrl());
                                //map.setLocation((String) country.get("location"));
                                map.setRetail((String) country.get("retail"));
                                if (!(photo.<String>getList("userlike") == null)) {
                                    likes = (ArrayList<String>) photo.get("userlike");
                                }
                                if (!(photo.<String>getList("userfollow") == null)) {
                                    follows = (ArrayList<String>) photo.get("userfollow");

                                }
                                map.setComment(Integer.toString(country.getInt("commentnumber")));
                                map.setLike(Integer.toString(likes.size()));
                                String likecompare = likes.toString();
                                if (likecompare.contains(currentuser.getObjectId())) {
                                    map.setLikeflag("true");
                                } else {
                                    map.setLikeflag("false");
                                }
                                likes.clear();

                                String followcompare = follows.toString();
                                if (followcompare.contains(currentuser.getObjectId())) {
                                    map.setFollowflag("true");
                                } else {
                                    map.setFollowflag("false");
                                }
                                follows.clear();
                                data.add(map);
                            } else if (today.compareTo(c) < 0) {

                                map.setExpirdate("EXP: " + formattedDate);
                                ParseFile image = (ParseFile) country.get("image");
                                ParseUser homeuser = country.getParseUser("user");


                                map.setUserID(homeuser.getObjectId());
                                map.setID(country.getObjectId());
                                map.setusername((String) homeuser.get("name"));
                                if (!((String) homeuser.get("name") == null)) {
                                    map.setUserrealname((String) homeuser.get("name"));
                                } else {
                                    map.setUserrealname("");
                                }


                                map.setCategory((String) country.get("category"));
                                map.setTagline((String) country.get("tagline"));


                                if (!(homeuser.get("photo") == null)) {
                                    ParseFile userimage = (ParseFile) homeuser.get("photo");
                                    map.setUserimageurl(userimage.getUrl());
                                }


                                map.setIamgeURL(image.getUrl());
                                //map.setLocation((String) country.get("location"));
                                map.setRetail((String) country.get("retail"));
                                if (!(photo.<String>getList("userlike") == null)) {
                                    likes = (ArrayList<String>) photo.get("userlike");
                                }
                                if (!(photo.<String>getList("userfollow") == null)) {
                                    follows = (ArrayList<String>) photo.get("userfollow");

                                }
                                map.setComment(Integer.toString(country.getInt("commentnumber")));
                                map.setLike(Integer.toString(likes.size()));
                                String likecompare = likes.toString();
                                if (likecompare.contains(currentuser.getObjectId())) {
                                    map.setLikeflag("true");
                                } else {
                                    map.setLikeflag("false");
                                }
                                likes.clear();

                                String followcompare = follows.toString();
                                if (followcompare.contains(currentuser.getObjectId())) {
                                    map.setFollowflag("true");
                                } else {
                                    map.setFollowflag("false");
                                }
                                follows.clear();
                                data.add(map);

                            }


                        }


//                    ParseQuery<ParseObject> inappequery = ParseQuery.getQuery("Inappropriate");
//                    inappequery.whereEqualTo("photo", photo);


//                    if(inappequery.count()>=3){
//                       photo.deleteInBackground();
//                    }
//                    else {


//                    }


                    }
                } catch (ParseException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            //listview = (ListView) findViewById(R.id.home_listView);
            // Pass the results into ListViewAdapter.java
            adapter = new HomeItemListAdapter(getActivity(),
                    data);
            mainadapter = new Myadapter(getActivity(), adapter, data.size());
            // Binds the Adapter to the ListView
            listview.setAdapter(mainadapter);
            // Close the progressdialog
//            mProgressDialog.dismiss();
            hideProgressDialog();

//            listview.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//                @Override
//                public void onScrollStateChanged(AbsListView view,
//                                                 int scrollState) { // TODO Auto-generated method stub
//                    int threshold = 1;
//                    int count = listview.getCount();
//
//                    if (scrollState == SCROLL_STATE_IDLE) {
//                        if (listview.getLastVisiblePosition() >= count
//                                - threshold) {
//                            // Execute LoadMoreDataTask AsyncTask
//                            new LoadMoreDataTask().execute();
//                        }
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem,
//                                     int visibleItemCount, int totalItemCount) {
//                    // TODO Auto-generated method stub
//
//                }
//
//            });
        }
    }


    private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //mProgressDialog = new ProgressDialog(HomeActivity.this);
            // Set progressdialog title

            // Set progressdialog message
            //mProgressDialog.setMessage("Loading more...");
            //mProgressDialog.setCancelable(false);
            // Show progressdialog
            //mProgressDialog.show();
            //showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            data = new ArrayList<Homeitem>();

            try {
                ParseQuery<ParseObject> photosFromCurrentUserQuery = ParseQuery.getQuery("Photo");
                if (!(fillterstring == null) && !fillterstring.equals("all")) {

                    photosFromCurrentUserQuery.whereContainedIn("category", limits);
//                    photosFromCurrentUserQuery.whereEqualTo("category",fillterstring);
                }
                if (!(retailerstring == null)) {

                    photosFromCurrentUserQuery.whereEqualTo("retail", retailerstring);
                }

                photosFromCurrentUserQuery.whereExists("image");
                photosFromCurrentUserQuery.include("user");
                photosFromCurrentUserQuery.orderByDescending("createdAt");
                // Set the limit of objects to show
                photosFromCurrentUserQuery.setLimit(setlimite += 10);
                ob = photosFromCurrentUserQuery.find();
                for (ParseObject country : ob) {
                    // Locate images in flag column
                    photo = country;

                    Date c = (Date) country.get("expiry");
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c);

                    ParseFile image = (ParseFile) country.get("image");
                    ParseUser homeuser = country.getParseUser("user");
                    ParseFile userimage = (ParseFile) homeuser.get("photo");
                    final Homeitem map = new Homeitem();
                    map.setID(country.getObjectId());
                    map.setusername(homeuser.getUsername());
                    if (!((String) homeuser.get("name") == null)) {
                        map.setUserrealname((String) homeuser.get("name"));
                    } else {
                        map.setUserrealname("unknown");
                    }
                    map.setExpirdate("EXP: " + formattedDate);
                    map.setCategory((String) country.get("category"));
                    map.setTagline((String) country.get("tagline"));
                   /* if (!(homeuser.get("facebookId") == null)) {
                        String url = "https://graph.facebook.com/" + homeuser.get("facebookId") + "/picture?type=large";
                        map.setUserimageurl(url);
                    }*/
                    if (!(userimage == null)) {
                        map.setUserimageurl(userimage.getUrl());
                    } else {
                        map.setUserimageurl("");
                    }


                    map.setIamgeURL(image.getUrl());
                    //map.setLocation((String) country.get("location"));
                    map.setRetail((String) country.get("retail"));
                    if (!(photo.<String>getList("userlike") == null)) {
                        likes = (ArrayList<String>) photo.get("userlike");
                    }
                    if (!(photo.<String>getList("userfollow") == null)) {
                        follows = (ArrayList<String>) photo.get("userfollow");

                    }
                    map.setComment(Integer.toString(country.getInt("commentnumber")));
                    map.setLike(Integer.toString(likes.size()));
                    String likecompare = likes.toString();
                    if (likecompare.contains(currentuser.getObjectId())) {
                        map.setLikeflag("true");
                    } else {
                        map.setLikeflag("false");
                    }
                    likes.clear();

                    String followcompare = follows.toString();
                    if (followcompare.contains(currentuser.getObjectId())) {
                        map.setFollowflag("true");
                    } else {
                        map.setFollowflag("false");
                    }
                    follows.clear();


                    data.add(map);


                    data.add(map);


//                    }


                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate listview last item
            int position = listview.getLastVisiblePosition();
            // Pass the results into ListViewAdapter.java
            adapter = new HomeItemListAdapter(getActivity(),
                    data);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Show the latest retrived results on the top
            listview.setSelectionFromTop(position, 0);
            // Close the progressdialog
            //mProgressDialog.dismiss();

            //swipeRefreshLayout.setRefreshing(false);

            //hideProgressDialog();


        }
    }


    public class Myadapter extends BaseAdapter {
        private int k = 20;
        private Activity activity;
        private static final String ADMOB_ID = "ca-app-pub-7340006778875243/6974705813";
        private HomeItemListAdapter delegate;
        private int count;

        // Constructor takes in a BaseAdapter also
        public Myadapter(Activity activity, HomeItemListAdapter delegate, int count) {
            this.activity = activity;
            this.delegate = delegate;
            this.count = count;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return this.count;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if ((position % k) == 0 && !(position == 0)) {
                if (convertView instanceof AdView) {
                    // Don’t instantiate new AdView, reuse old one
                    return convertView;
                } else {

                    //


//                mInterstitialAd = new InterstitialAd(a);
//                mInterstitialAd.setAdUnitId("ur ad id");
//                AdRequest adRequest = new AdRequest.Builder().build();
//                mInterstitialAd.loadAd(adRequest);
//                mInterstitialAd.setAdListener(new AdListener() {
//                    public void onAdLoaded() {
//                        displayInterstitial();
//                    }
//                });
//
//                public void displayInterstitial() {
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    }
//                }


                    // Create a new AdView
                    adView = new AdView(activity);
                    adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
                    adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                    return adView;
                }
            } else {
                // Offload displaying other items to the delegate
                return delegate.getView(position - (int) Math.ceil(position / k),
                        convertView, parent);
            }
        }
    }


    public class HomeItemListAdapter extends BaseAdapter {
        boolean flag = true;
        Context context;
        LayoutInflater inflater;
        ImageLoader imageLoader;
        ImageLoaderGrid userimageloader;
        private ParseFile image;
        private List<Homeitem> worldpopulationlist = null;
        private ArrayList<Homeitem> arraylist;

        /**
         * Constructor from a list of items
         */
        public HomeItemListAdapter(Context context, List<Homeitem> worldpopulationlist) {

            this.context = context;
            this.worldpopulationlist = worldpopulationlist;
            inflater = LayoutInflater.from(context);
            this.arraylist = new ArrayList<Homeitem>();
            this.arraylist.addAll(worldpopulationlist);
            imageLoader = new ImageLoader(context);
            userimageloader = new ImageLoaderGrid(context);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;


//            if (view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.home_viewitem, null);
//                holder.usernametextview = (TextView) view.findViewById(R.id.username_hometextView);

          /*  holder.expiredatetextview = (TextView) view.findViewById(R.id.expierdate_hometextView);

            holder.moreimagebut = (ImageButton) view.findViewById(R.id.more_imageButton);
            holder.taglinetextview = (TextView) view.findViewById(R.id.niketextView);

            holder.liketextview = (TextView) view.findViewById(R.id.likenumber_hometextView);
            holder.retailtextview = (TextView) view.findViewById(R.id.Retailer_textView);*/
            holder.productimageview = (ImageView) view.findViewById(R.id.deal_imageView);
          /*  holder.userimageview = (ImageView) view.findViewById(R.id.userphoto_details_imageView);
            holder.likebutimagebut = (ImageButton) view.findViewById(R.id.like_home_imageButton);
            holder.commentimagebut = (ImageButton) view.findViewById(R.id.comment_home_imageButton);
            holder.shareimagebut = (ImageButton) view.findViewById(R.id.share_home_imageButton);
            holder.followmagebut = (ImageButton) view.findViewById(R.id.follow_home_imageButton);
            holder.userrealname_textview = (TextView) view.findViewById(R.id.name_hometextView);

            holder.commenttextview = (TextView) view.findViewById(R.id.commentnumber_hometextView);*/
            view.setTag(holder);
//            }
//
//            else {
//                view=
//            }


          /*  holder.userrealname_textview.setTypeface(font);
            holder.liketextview.setTypeface(font);
            holder.retailtextview.setTypeface(font);
            // Set some view properties
            holder.commenttextview.setTypeface(font);
            holder.taglinetextview.setTypeface(font);
            holder.expiredatetextview.setTypeface(font);
//            holder.usernametextview.setTypeface(font);
//            holder.usernametextview.setText(worldpopulationlist.get(position).getusername());
            holder.expiredatetextview.setText(worldpopulationlist.get(position).getExpirdate());
            holder.taglinetextview.setText("#" + worldpopulationlist.get(position).getTagline());
            holder.liketextview.setText(worldpopulationlist.get(position).getLike());
            holder.commenttextview.setText(worldpopulationlist.get(position).getComment());
            holder.userrealname_textview.setText(worldpopulationlist.get(position).getUserrealname());
            holder.retailtextview.setText(worldpopulationlist.get(position).getRetail());
//            Log.d("sdfasdfadfadfasdfadfadfasdfadf",worldpopulationlist.get(position).getIamgeURL());
//              imageLoader.DisplayImage(worldpopulationlist.get(position).getIamgeURL(), holder.productimageview);*/

            Glide.with(context).load(worldpopulationlist.get(position).getIamgeURL()).into(holder.productimageview);
           /* if (!worldpopulationlist.get(position).getUserimageurl().equals("")) {
//                imageLoader.DisplayImage(worldpopulationlist.get(position).getUserimageurl(), holder.userimageview);
                Glide.with(context).load(worldpopulationlist.get(position).getUserimageurl())
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getActivity()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.userimageview);
            } else {
                holder.userimageview.setImageResource(R.drawable.man);
            }


            if (worldpopulationlist.get(position).getLikeflag().equals("true")) {
                holder.likebutimagebut.setImageResource(R.drawable.like_banner_press);
            }
            if (worldpopulationlist.get(position).getLikeflag().equals("false")) {
                holder.likebutimagebut.setImageResource(R.drawable.like_banner);
            }
            if (worldpopulationlist.get(position).getFollowflag().equals("true")) {
                holder.followmagebut.setImageResource(R.drawable.favourites_press);
            }
            if (worldpopulationlist.get(position).getFollowflag().equals("false")) {
                holder.followmagebut.setImageResource(R.drawable.favourites);
            }
*/

            /*holder.likebutimagebut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (worldpopulationlist.get(position).getLikeflag().equals("true")) {
                        holder.likebutimagebut.setImageResource(R.drawable.like_banner);

                        final String like = Integer.toString(Integer.parseInt(worldpopulationlist.get(position).getLike()) - 1);
                        holder.liketextview.setText(like);
//                        mProgressDialog.show();
                        Homeitem dd = data.get(position);
                        dd.setLikeflag("false");
                        dd.setLike(Integer.toString(Integer.parseInt(dd.getLike()) - 1));
                        data.set(position, dd);

//                        adapter = new homeItemListAdapter(HomeActivity.this,
//                                data);
//                        listview.setAdapter(adapter);
//                        listview.setSelectionFromTop(position, 0);
//                        mProgressDialog.dismiss();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                        query.getInBackground(worldpopulationlist.get(position).getID(), new GetCallback<ParseObject>() {
                            public void done(ParseObject photo_origin, ParseException e) {
                                if (e == null) {

                                    photo = photo_origin;
                                    if (!(photo.get("userlike") == null)) {
                                        likes = (ArrayList<String>) photo.get("userlike");
                                    }
                                    ParseUser touserlike = photo_origin.getParseUser("user");
                                    ParseUser likeuser = ParseUser.getCurrentUser();
                                    Log.d("asdfasdfasdfadf", likes.toString());
                                    likes.remove(likeuser.getObjectId());
                                    Log.d("asdfasdfasdfadf", likes.toString());
                                    photo.put("userlike", likes);
                                    try {
                                        photo.save();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                    ParseUser currentuser = ParseUser.getCurrentUser();
                                    if (!(currentuser.getObjectId().equals(worldpopulationlist.get(position).getUserID()))) {


                                        ParseObject alert = new ParseObject("Alert");
                                        alert.put("fromuser", ParseUser.getCurrentUser());
                                        alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " unlike!");
                                        alert.put("photo", photo);
                                        alert.put("touser", touserlike);
                                        alert.saveInBackground();
                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user", touserlike);
                                        pushQuery.whereEqualTo("notification", "true");

// Send push notification to query
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
// Set our Installation query
                                        push.setMessage("From " + (String) ParseUser.getCurrentUser().get("name") + " Unlike!:\n");
                                        push.sendInBackground();


                                    }


                                }
                            }
                        });

                    } else {
                        holder.likebutimagebut.setImageResource(R.drawable.like_banner_press);
                        final String like = Integer.toString(Integer.parseInt(worldpopulationlist.get(position).getLike()) + 1);
                        holder.liketextview.setText(like);
//                        mProgressDialog.show();

                        Homeitem dd = data.get(position);
                        dd.setLikeflag("true");
                        dd.setLike(Integer.toString(Integer.parseInt(dd.getLike()) + 1));
                        data.set(position, dd);
//                        adapter = new homeItemListAdapter(HomeActivity.this,
//                                data);
//                        listview.setAdapter(adapter);
//                        listview.setSelectionFromTop(position, 0);
//                        mProgressDialog.dismiss();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                        query.getInBackground(worldpopulationlist.get(position).getID(), new GetCallback<ParseObject>() {
                            public void done(ParseObject photo_origin, ParseException e) {
                                if (e == null) {

                                    photo = photo_origin;

                                    ParseUser touserlike = photo_origin.getParseUser("user");
                                    ParseUser user = ParseUser.getCurrentUser();
                                    String userid = user.getObjectId();
                                    if (!(photo.get("userlike") == null)) {
                                        likes = (ArrayList<String>) photo.get("userlike");
                                    }
                                    likes.add(userid);
                                    photo.put("userlike", likes);
                                    try {
                                        photo.save();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    Log.d("adfadfasdfasdfadsfasdf", (String) photo.get("tagline"));


                                    ParseUser currentuser = ParseUser.getCurrentUser();
                                    if (!(currentuser.getObjectId().equals(worldpopulationlist.get(position).getUserID()))) {
                                        ParseObject alert = new ParseObject("Alert");
                                        alert.put("fromuser", ParseUser.getCurrentUser());
                                        alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " like your product");
                                        alert.put("photo", photo);
                                        alert.put("touser", touserlike);
                                        alert.saveInBackground();
                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user", touserlike);
                                        pushQuery.whereEqualTo("notification", "true");
// Send push notification to query
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
// Set our Installation query
                                        push.setMessage("From " + (String) ParseUser.getCurrentUser().get("name") + " like your product\n");
                                        push.sendInBackground();
                                    }
                                }


                                // new RemoteDataTask().execute();


                            }


                        });


                    }

                }
            });*/
            /*holder.followmagebut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (worldpopulationlist.get(position).getFollowflag().equals("true")) {
                        holder.followmagebut.setImageResource(R.drawable.favourites);
//                        mProgressDialog.show();
//                        follows.clear();
                        Homeitem dd = data.get(position);
                        dd.setFollowflag("false");
                        data.set(position, dd);
//                        adapter = new homeItemListAdapter(HomeActivity.this,
//                                data);
//                        listview.setAdapter(adapter);
//                        listview.setSelectionFromTop(position, 0);
//                        mProgressDialog.dismiss();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                        query.getInBackground(worldpopulationlist.get(position).getID(), new GetCallback<ParseObject>() {
                            public void done(ParseObject photo_origin, ParseException e) {
                                if (e == null) {

                                    photo = photo_origin;
                                    if (!(photo.get("userfollow") == null)) {
                                        follows = (ArrayList<String>) photo.get("userfollow");
                                    }
                                    ParseUser likeuser = ParseUser.getCurrentUser();
                                    follows.remove(likeuser.getObjectId());
                                    photo.put("userfollow", follows);

                                    try {
                                        photo.save();
                                    } catch (ParseException ee) {
                                        ee.printStackTrace();
                                    }

//                    photo.removeAll("userlike",likes);
//                    try {
//                        photo.save();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }

                                    ParseUser touser = photo_origin.getParseUser("user");
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
                                    query.whereEqualTo("user", ParseUser.getCurrentUser());
                                    query.whereEqualTo("photo", photo);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject object, ParseException e) {
                                            if (object == null) {
                                                Log.d("score", "The getFirst request failed.");
                                            } else {
                                                object.deleteInBackground();
                                            }
//                                            new RemoteDataTask().execute();


                                        }
                                    });
                                    ParseUser currentuser = ParseUser.getCurrentUser();
                                    if (!(currentuser.getObjectId().equals(worldpopulationlist.get(position).getUserID()))) {
                                        ParseObject alert = new ParseObject("Alert");
                                        alert.put("fromuser", ParseUser.getCurrentUser());
                                        alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " unfollow you");
                                        alert.put("photo", photo);
                                        alert.put("touser", touser);
                                        alert.saveInBackground();
//                    ParsePush push = new ParsePush();
//                    push.setChannel("Founditt");
//
//                    push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
//                    push.sendInBackground();


                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user", touser);
                                        pushQuery.whereEqualTo("notification", "true");
// Send push notification to query
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
// Set our Installation query
                                        push.setMessage("From " + (String) ParseUser.getCurrentUser().get("name") + " unfollow you\n");
                                        push.sendInBackground();
                                    }

                                }
                            }
                        });


                    } else {
                        holder.followmagebut.setImageResource(R.drawable.favourites_press);


//                        mProgressDialog.show();
//                        follows.clear();
                        Homeitem dd = data.get(position);
                        dd.setFollowflag("true");
                        data.set(position, dd);
//                        adapter = new homeItemListAdapter(HomeActivity.this,
//                                data);
//                        listview.setAdapter(adapter);
//                        listview.setSelectionFromTop(position, 0);
//                        mProgressDialog.dismiss();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                        query.getInBackground(worldpopulationlist.get(position).getID(), new GetCallback<ParseObject>() {
                            public void done(ParseObject photo_origin, ParseException e) {
                                if (e == null) {

                                    photo = photo_origin;
                                    if (!(photo.get("userfollow") == null)) {
                                        follows = (ArrayList<String>) photo.get("userfollow");
                                    }
                                    String userid = currentuser.getObjectId();
                                    follows.add(userid);
                                    photo.put("userfollow", follows);
                                    try {
                                        photo.save();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                    ParseUser touser = photo_origin.getParseUser("user");
                                    ParseObject follow = new ParseObject("Follow");
                                    ParseACL acl = new ParseACL();
                                    acl.setPublicReadAccess(true);
                                    acl.setPublicWriteAccess(true);
                                    follow.setACL(acl);
                                    follow.put("user", ParseUser.getCurrentUser());
                                    follow.put("photo", photo);
                                    follow.saveInBackground();
                                    ParseUser currentuser = ParseUser.getCurrentUser();
                                    if (!(currentuser.getObjectId().equals(worldpopulationlist.get(position).getUserID()))) {
                                        ParseObject alert = new ParseObject("Alert");
                                        alert.put("fromuser", ParseUser.getCurrentUser());
                                        alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " follow you");
                                        alert.put("photo", photo);
                                        alert.put("touser", touser);
                                        alert.saveInBackground();
//                    ParsePush push = new ParsePush();
//                    push.setChannel("Founditt");
//
//                    push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
//                    push.sendInBackground();


                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user", touser);
                                        pushQuery.whereEqualTo("notification", "true");
// Send push notification to query
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
// Set our Installation query
                                        push.setMessage("From " + (String) ParseUser.getCurrentUser().get("name") + " follow you\n");
                                        push.sendInBackground();
                                    }

                                }
                                // new RemoteDataTask().execute();
//                                homeitem dd = data.get(position);
//                                dd.setFollowflag("true");
//                                adapter = new homeItemListAdapter(HomeActivity.this,
//                                        data);
//                                listview.setAdapter(adapter);
                            }
                        });


                    }
                    flag = true;
                }

            });*/

         /*   holder.commentimagebut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("ID", worldpopulationlist.get(position).getID());
                    Intent zoom = new Intent(getActivity(), MyProfileActivity.class);
                    zoom.putExtra("photoID", worldpopulationlist.get(position).getID());
                    startActivity(zoom);
                    // Toast.makeText(HomeActivity.this,worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();

                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);

                }

            });*/
           holder.productimageview.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent zoom = new Intent(getActivity(), BookDetailActivity.class);
                   zoom.putExtra("photoID", worldpopulationlist.get(position).getID());
                   startActivity(zoom);
                   //Toast.makeText(getActivity(),worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();

                   getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
//
//
//
//                   Intent zoom = new Intent(HomeActivity.this, CommentActivity.class);
//                   zoom.putExtra("photoID", worldpopulationlist.get(position).getID());
//                   startActivity(zoom);
//                   // Toast.makeText(HomeActivity.this,worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();
//
//                   overridePendingTransition(R.anim.right_in, R.anim.left_out);
//
              }
           });
            /*holder.shareimagebut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
//                    intent.putExtra(Intent.EXTRA_SUBJECT, ParseUser.getCurrentUser().getUsername());
//
//                    intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(), bitmap));
//                    intent.setType("image*//*");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                    startActivity(Intent.createChooser(intent, null));


                    holder.productimageview.buildDrawingCache();
                    Bitmap bm = holder.productimageview.getDrawingCache();

                    Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Punch");
                    imagesFolder.mkdirs();
                    String fileName = "image" + ".jpg";
                    File output = new File(imagesFolder, fileName);
                    Uri uriSavedImage = Uri.fromFile(output);
                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    OutputStream fos = null;

                    try {
                        fos = getActivity().getContentResolver().openOutputStream(uriSavedImage);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, ParseUser.getCurrentUser().getUsername());

                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(output));
                    intent.setType("image*//*");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(Intent.createChooser(intent, null));


                }
            });*/


            /*holder.moreimagebut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                            getActivity());

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Report inappropriate");
                    arrayAdapter.add("Report expired");

                    builderSingle.setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builderSingle.setAdapter(arrayAdapter,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String strName = arrayAdapter.getItem(which);
                                    AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                            getActivity());
                                    builderInner.setMessage(strName);
                                    builderInner.setPositiveButton("Submit",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {

                                                    if (strName.equals("Report expired")) {


                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                                                        query.getInBackground(worldpopulationlist.get(position).getID(), new GetCallback<ParseObject>() {
                                                            public void done(ParseObject photo_origin, ParseException e) {
                                                                if (e == null) {
                                                                    holder.expiredatetextview.setText("EXPIRED");
                                                                    Homeitem dd = data.get(position);
                                                                    dd.setExpirdate("EXPIRED");
                                                                    data.set(position, dd);
                                                                    adapter = new HomeItemListAdapter(getActivity(),
                                                                            data);
                                                                    listview.setAdapter(adapter);
                                                                    listview.setSelectionFromTop(position, 0);
                                                                    photo = photo_origin;
                                                                    Date today = new Date(System.currentTimeMillis());
                                                                    photo.put("expiry", today);
//                                                                    photo.put("expflag","true");
                                                                    photo.saveInBackground();
                                                                    ParseUser touser = photo_origin.getParseUser("user");
                                                                    ParseUser currentuser = ParseUser.getCurrentUser();
                                                                    if (!(currentuser.getObjectId().equals(worldpopulationlist.get(position).getUserID()))) {
                                                                        ParseObject alert = new ParseObject("Alert");
                                                                        alert.put("fromuser", ParseUser.getCurrentUser());
                                                                        alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " expire your product");
                                                                        alert.put("photo", photo);
                                                                        alert.put("touser", touser);
                                                                        alert.saveInBackground();


                                                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                                                        pushQuery.whereEqualTo("user", touser);
                                                                        pushQuery.whereEqualTo("notification", "true");
// Send push notification to query
                                                                        ParsePush push = new ParsePush();
                                                                        push.setQuery(pushQuery);
// Set our Installation query
                                                                        push.setMessage("From " + (String) ParseUser.getCurrentUser().get("name") + " expire your product");
                                                                        push.sendInBackground();
                                                                    }

                                                                }

                                                            }
                                                        });


                                                    } else if (strName.equals("Report inappropriate")) {


                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                                                        query.getInBackground(worldpopulationlist.get(position).getID(), new GetCallback<ParseObject>() {
                                                            public void done(ParseObject photo_origin, ParseException e) {
                                                                if (e == null) {

                                                                    photo = photo_origin;
                                                                    ParseUser touser = photo_origin.getParseUser("user");
                                                                    ParseObject expire = new ParseObject("Inappropriate");
                                                                    expire.put("user", ParseUser.getCurrentUser());
                                                                    expire.put("photo", photo);
                                                                    expire.saveInBackground();
                                                                    ParseQuery<ParseObject> inappequery = ParseQuery.getQuery("Inappropriate");
                                                                    inappequery.whereEqualTo("photo", photo);
                                                                    try {
                                                                        photo.put("inapp", inappequery.count());
                                                                    } catch (ParseException e1) {
                                                                        e1.printStackTrace();
                                                                    }
                                                                    photo.saveInBackground();
                                                                    ParseUser currentuser = ParseUser.getCurrentUser();
                                                                    if (!(currentuser.getObjectId().equals(worldpopulationlist.get(position).getUserID()))) {
                                                                        ParseObject alert = new ParseObject("Alert");
                                                                        alert.put("fromuser", ParseUser.getCurrentUser());
                                                                        alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " inappropriate your product");
                                                                        alert.put("photo", photo);
                                                                        alert.put("touser", touser);
                                                                        alert.saveInBackground();


                                                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                                                        pushQuery.whereEqualTo("user", touser);
                                                                        pushQuery.whereEqualTo("notification", "true");
// Send push notification to query
                                                                        ParsePush push = new ParsePush();
                                                                        push.setQuery(pushQuery);
// Set our Installation query
                                                                        push.setMessage("From " + (String) ParseUser.getCurrentUser().get("name") + " inappropriate your product");
                                                                        push.sendInBackground();
                                                                    }
                                                                }

                                                            }
                                                        });


                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                    builderInner.show();
                                }
                            });
                    builderSingle.show();


                }
            });*/


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
            //            public TextView usernametextview;
            public TextView expiredatetextview;
            public TextView taglinetextview;
            public TextView liketextview;
            public ImageView productimageview;
            public ImageView userimageview;
            public ImageButton likebutimagebut;
            public ImageButton commentimagebut;
            public ImageButton shareimagebut;
            public ImageButton followmagebut;
            public ImageButton moreimagebut;
            public TextView userrealname_textview;
            public TextView commenttextview;
            public TextView retailtextview;


        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setQueryHint(getString(R.string.search_view_example));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }*/

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.filter_item) {
            Toast.makeText(getActivity(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
            Intent intent;
            intent = new Intent(getActivity(), FilterActivity.class);
            startActivity(intent);
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
