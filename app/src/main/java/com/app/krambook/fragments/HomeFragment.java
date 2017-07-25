package com.app.krambook.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.activity.FilterActivity;
import com.app.krambook.adapter.BooksRecyclerViewAdapter;
import com.app.krambook.models.Homeitem;
import com.app.krambook.models.Photo;
import com.app.krambook.models.UserData;
import com.app.krambook.other.EndlessRecyclerViewScrollListener;
import com.app.krambook.other.SpacesItemDecoration;
import com.app.krambook.utils.FileCache;
import com.app.krambook.utils.MemoryCache;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private BooksRecyclerViewAdapter booksAdapter;
    private List<ParseObject> mMoreBooks = new ArrayList<>();
    private List<ParseObject> mLatestBooks = new ArrayList<>();
    private Date lastBookDate,lastUpdated;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView recyclerView;
    private String username;
    public List<Homeitem> data = null;
    public String curentusername;

    ListView list;
    StringBuffer buf;
    String test;
    List<ParseObject> productlist;
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
    View rootView;

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

        retailerstring = filterintent.getStringExtra("publisher");
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

        setRetainInstance(true);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        new RemoteDataTask().execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadUpdatedData();
            }
        });

        int color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        swipeRefreshLayout.setColorSchemeColors(color);



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
            mProgressDialog.setCancelable(false);
            showProgressDialog();
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
                        }

                    }
                });

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

                                lastUpdated = ob.get(ob.size() - ob.size()).getUpdatedAt();
                                lastBookDate = ob.get(ob.size() - 1).getUpdatedAt();

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
            booksAdapter = new BooksRecyclerViewAdapter(getContext(), data);
            recyclerView.setAdapter(booksAdapter);
            SpacesItemDecoration decoration = new SpacesItemDecoration(16);
            recyclerView.addItemDecoration(decoration);

            //add endless scrolling
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    //load more rooms and add to the end of the list
                    new FetchMoreBooks().execute();
                }
            });

            hideProgressDialog();

        }
    }




    private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                photosFromCurrentUserQuery.orderByDescending("updatedAt");
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
                    //if (!(homeuser.get("facebookId") == null)) {
                    //String url = "https://graph.facebook.com/" + homeuser.get("facebookId") + "/picture?type=large";
                    //map.setUserimageurl(url);
                    //}
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

                    //booksAdapter.addAll(data);


                    //data.add(map);


//                    }


                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Void result) {
            // Locate listview last item
            //int position = listview.getLastVisiblePosition();
            // Pass the results into ListViewAdapter.java
            booksAdapter = new BooksRecyclerViewAdapter(getActivity(),
                    data);
            // Binds the Adapter to the ListView
            recyclerView.setAdapter(booksAdapter);
            // Show the latest retrived results on the top
            recyclerView.swapAdapter(booksAdapter, false);
            booksAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setRefreshing(false);

            //hideProgressDialog();


        }
    }



    private class FetchMoreBooks extends AsyncTask<Void, Integer, List<ParseObject>>{

        @Override
        protected List<ParseObject> doInBackground(Void... params) {
            //fetch the latest rooms
            ParseQuery<ParseObject> getMoreBookssQuery = ParseQuery.getQuery("Photo");
            getMoreBookssQuery.whereLessThan("updatedAt", lastBookDate);
            getMoreBookssQuery.setLimit(setlimite += 10);
            getMoreBookssQuery.orderByDescending("updatedAt");
            getMoreBookssQuery.whereExists("image");
            getMoreBookssQuery.include("user");
            if (!(fillterstring == null) && !fillterstring.equals("all")) {

                getMoreBookssQuery.whereContainedIn("category", limits);
//                    photosFromCurrentUserQuery.whereEqualTo("category",fillterstring);
            }
            if (!(retailerstring == null)) {

                getMoreBookssQuery.whereEqualTo("retail", retailerstring);
            }



            try {
                mMoreBooks = getMoreBookssQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //check if there are new rooms and assign the bottom most element's date to lastRoomDate
            if(mMoreBooks.size() == 0){
                //check if mRooms has any elements
                if(ob.size() > 0) {
                    lastBookDate = ob.get(ob.size() - 1).getUpdatedAt();
                }
            }else {
                lastBookDate = mMoreBooks.get(mMoreBooks.size() - 1).getUpdatedAt();
            }

            return mMoreBooks;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //show progressbar


        }

        //update main UI with the results from doInBackground
        @Override
        protected void onPostExecute(List<ParseObject> moreBooks){
            //add the latest rooms added to the adapter
            int curSize = booksAdapter.getItemCount();
            //add the new rooms to the list of existing rooms
            ob.addAll(moreBooks);
            booksAdapter.notifyItemRangeInserted(curSize, ob.size() - 1);
            //dismiss progressbar

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
