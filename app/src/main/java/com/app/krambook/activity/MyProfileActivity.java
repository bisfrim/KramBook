package com.app.krambook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.krambook.R;
import com.app.krambook.adapter.GridviewAdapter;
import com.app.krambook.entity.ExpandableHeightGridView;
import com.app.krambook.entity.HeaderView;
import com.app.krambook.fragments.MyListingFragment;
import com.app.krambook.fragments.SoldFragment;
import com.app.krambook.fragments.WishListFragment;
import com.app.krambook.models.Beanclass;
import com.app.krambook.models.UserData;
import com.app.krambook.utils.FileCache;
import com.app.krambook.utils.ImageLoader;
import com.app.krambook.utils.ImageLoaderGrid;
import com.app.krambook.utils.MemoryCache;
import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import customfonts.MyTextView;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MyProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, MaterialTabListener {

    int favoriteCount;
    int postCount;
    ExpandableHeightGridView gridview;
    CoordinatorLayout rootLayout;
    ImageLoader imageLoaderprofile;
    MaterialTabHost tabHost;
    MyListingFragment myListing;
    SoldFragment sold;
    WishListFragment wishList;

    private ArrayList<Beanclass> beans;
    private GridviewAdapter gridviewAdapter;
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private boolean isHideToolbarView = false;
    public static ImageLoaderGrid imageLoader;


    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    protected UserData mCurrentUser;
    protected ImageView imgvThubnail, imgvProfile;
    protected MyTextView userFullName, userGender, userLocation, userDeptment, userLastSeen;
    private MyTextView itemPostedCount, itemFavoriteCount;



    private final FetchCallback fetchUserCallback = new FetchCallback();
    public void updateInfo() {
        if (fetchUserCallback != null)
            mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        Context context;

        mCurrentUser = (UserData) UserData.getCurrentUser();
        //mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        new FileCache(this).clear();
        new MemoryCache().clear();
        imageLoader=new ImageLoaderGrid(MyProfileActivity.this);
        imageLoaderprofile=new ImageLoader(MyProfileActivity.this);

        imgvProfile = (ImageView) findViewById(R.id.user_profile_image);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        appBarLayout.addOnOffsetChangedListener(this);
        mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
        //initUi();

        itemPostedCount = (MyTextView)findViewById(R.id.item_postedCountView);
        itemFavoriteCount = (MyTextView)findViewById(R.id.item_favoriteCountView);

        myListing = new MyListingFragment();
        sold = new SoldFragment();
        wishList = new WishListFragment();


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }
        //setupViewPager(viewPager);

        //tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(viewPager);



    }


    @Override
    protected void onResume() {
        super.onResume();


        ParseQuery<ParseObject> postquaryCount = ParseQuery.getQuery("Photo");
        postquaryCount.whereEqualTo("user", ParseUser.getCurrentUser());
        postquaryCount.include("photo");
        try{
            postCount = postquaryCount.count();
        }catch(ParseException e){
            e.printStackTrace();
        }
        itemPostedCount.setText(Integer.toString(postCount));


        ParseQuery<ParseObject> favoritequaryCount = ParseQuery.getQuery("Follow");
        favoritequaryCount.whereEqualTo("user", ParseUser.getCurrentUser());
        favoritequaryCount.include("photo");
        try {
            favoriteCount = favoritequaryCount.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        itemFavoriteCount.setText(Integer.toString(favoriteCount));

    }


    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);

        toolbarHeaderView.bindTo(mCurrentUser.getFullName(), mCurrentUser.getDept());
        floatHeaderView.bindTo(mCurrentUser.getFullName(), mCurrentUser.getDept());

        Glide.with(MyProfileActivity.this)
                .load(mCurrentUser.getPhotoUrl())
                //.fitCenter()
                //.centerCrop()
                .into(imgvProfile);
    }




    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }

    }



    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return myListing;
                case 1:
                    return sold;
                case 2:
                    return wishList;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "My Listing";
                case 1:
                    return "Sold";
                case 2:
                    return "WishList";

            }
            return null;
        }

    }


    //fetch and update user info from this callback
    private class FetchCallback implements GetCallback<ParseUser> {


        @Override
        public void done(ParseUser parseUser, ParseException e) {

            if (e != null) {
                rootLayout.setVisibility(View.GONE);
            } else if (parseUser != null) {
                mCurrentUser = (UserData) parseUser;

                toolbarHeaderView.bindTo(mCurrentUser.getFullName(), mCurrentUser.getDept());
                floatHeaderView.bindTo(mCurrentUser.getFullName(), mCurrentUser.getDept());

                Glide.with(MyProfileActivity.this)
                        .load(mCurrentUser.getPhotoUrl())
                        //.fitCenter()
                        //.centerCrop()
                        .into(imgvProfile);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile, menu);
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
            onBackPressed();
            return true;
        }

        if(id == R.id.edit_profile){
            Intent editProfileIntent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
            editProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(editProfileIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
