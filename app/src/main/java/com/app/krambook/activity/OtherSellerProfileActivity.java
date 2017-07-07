package com.app.krambook.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.krambook.R;
import com.app.krambook.entity.ExpandableHeightGridView;
import com.app.krambook.entity.HeaderView;
import com.app.krambook.fragments.MyListingFragment;
import com.app.krambook.fragments.SellerListingFragment;
import com.app.krambook.fragments.SellerSoldFragment;
import com.app.krambook.models.UserData;
import com.app.krambook.utils.FileCache;
import com.app.krambook.utils.ImageLoader;
import com.app.krambook.utils.ImageLoaderGrid;
import com.app.krambook.utils.MemoryCache;
import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import butterknife.Bind;
import butterknife.ButterKnife;
import customfonts.MyTextView;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class OtherSellerProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, MaterialTabListener {

    int sellerListingCount;
    int sellerSoldCount;
    SellerListingFragment sellerListing;
    SellerSoldFragment sellerSold;
    protected CoordinatorLayout sellerRootLayout;
    private ImageLoader imageLoaderprofile;
    private MaterialTabHost tabHost;
    public final static String EXTRA_USER_ID = "userID";
    private ViewPager viewPager;
    private boolean isHideToolbarView = false;
    private ViewPagerAdapter mAdapter;
    public static ImageLoaderGrid imageLoader;


    @Bind(R.id.seller_toolbar_header_view)
    protected HeaderView sellerToolbarHeaderView;

    @Bind(R.id.seller_float_header_view)
    protected HeaderView sellerFloatHeaderView;

    @Bind(R.id.sellers_appbar)
    protected AppBarLayout sellerAppBarLayout;

    @Bind(R.id.seller_toolbar)
    protected Toolbar sellerToolbar;

    protected UserData mCurrentUser;
    protected ImageView sellerImgvProfile;

    private MyTextView sellerListingTv, sellerSoldTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_seller_profile);
        ButterKnife.bind(this);

        setSupportActionBar(sellerToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        new FileCache(this).clear();
        new MemoryCache().clear();
        imageLoader = new ImageLoaderGrid(OtherSellerProfileActivity.this);
        imageLoaderprofile = new ImageLoader(OtherSellerProfileActivity.this);

        sellerImgvProfile = (ImageView) findViewById(R.id.seller_profile_image);

        sellerRootLayout = (CoordinatorLayout) findViewById(R.id.cordinator_rootLayout);

        //sellerAppBarLayout.addOnOffsetChangedListener(this);

        sellerListingTv = (MyTextView) findViewById(R.id.seller_item_listing);
        sellerSoldTv = (MyTextView) findViewById(R.id.seller_item_sold);

        sellerListing = new SellerListingFragment();
        sellerSold = new SellerSoldFragment();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabHost = (MaterialTabHost) this.findViewById(R.id.seller_tabHost);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
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
        for (int i = 0; i < mAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(mAdapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }

    }

    private void initUi() {
        sellerAppBarLayout.addOnOffsetChangedListener(this);

        ParseQuery<UserData> userQuery = UserData.getUserQuery();
        Log.d("KramBook:UserProfA", getIntent().getStringExtra(EXTRA_USER_ID));
        userQuery.getInBackground(getIntent().getStringExtra(EXTRA_USER_ID), new GetCallback<UserData>() {
            @Override
            public void done(UserData object, ParseException e) {
                if (object != null) {
                    sellerToolbarHeaderView.bindTo(object.getFullName(), object.getDept());
                    sellerFloatHeaderView.bindTo(object.getFullName(), object.getDept());


                    Glide.with(OtherSellerProfileActivity.this)
                            .load(object.getPhotoUrl())
                            //.fitCenter()
                            //.centerCrop()
                            .into(sellerImgvProfile);
                }
            }
        });


    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            sellerToolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            sellerToolbarHeaderView.setVisibility(View.GONE);
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
                    return sellerListing;
                case 1:
                    return sellerSold;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Listing";
                case 1:
                    return "Sold";

            }
            return null;
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

    @Override
    protected void onResume() {
        super.onResume();
        initUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
