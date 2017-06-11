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
import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import customfonts.MyTextView;

public class MyProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    ExpandableHeightGridView gridview;
    CoordinatorLayout rootLayout;

    public final static String EXTRA_USER_ID = "userId";

    private ArrayList<Beanclass> beans;
    private GridviewAdapter gridviewAdapter;
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean isHideToolbarView = false;

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

        imgvProfile = (ImageView) findViewById(R.id.user_profile_image);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        appBarLayout.addOnOffsetChangedListener(this);
        mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
        //initUi();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



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




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyListingFragment(), "My Listing");
        adapter.addFragment(new SoldFragment(), "Sold");
        adapter.addFragment(new WishListFragment(), "Wish List");
        viewPager.setAdapter(adapter);
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
