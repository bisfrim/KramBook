package com.app.krambook.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.models.Homeitem;
import com.app.krambook.models.Photo;
import com.app.krambook.other.CircleTransform;
import com.app.krambook.utils.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class BookDetailActivity extends AppCompatActivity {
    //private HomeFragment.HomeItemListAdapter homeAdapter;
    private Toolbar mToolbar;
    private boolean likeflag;
    private boolean followfalg;
    private int likenumber;
    private boolean flag = true;
    private int commentnumber;
    private ImageView[] starViews;
    private LinearLayout starLayout;

    public ImageView productimageview;
    public ImageView userimageview;
    public ImageButton likebutimagebut;
    public ImageButton commentimagebut;
    public ImageButton shareimagebut;
    public ImageButton followmagebut;
    public ImageButton moreimagebut;
    public ImageButton detailsimagebut;
    public TextView userrealname_textview;
    public TextView commenttextview;
    public TextView retailtextview;
    public TextView expiredatetextview, productDescription, bookDetailPrice;
    public TextView taglinetextview, isbnTextView, bookTitleView, ratingCountText;
    public TextView liketextview;

    ParseObject photo;
    ParseUser touser;
    ProgressDialog mProgressDialog;
    ParseUser currentuser;
    Typeface font;
    ArrayList<String> likes;
    ArrayList<String> follows;
    String photouserID;
    String photoID;
    String userId;
    String myISBN, isbnBookString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setIndeterminate(false);

        Intent bookDetailsIntent = getIntent();
        bookDetailsIntent.getStringExtra("bookDetails");

        likes = new ArrayList<String>();
        follows = new ArrayList<String>();
        currentuser = ParseUser.getCurrentUser();

        expiredatetextview = (TextView) findViewById(R.id.expierdate_hometextView);

        moreimagebut = (ImageButton) findViewById(R.id.more_imageButton);
        taglinetextview = (TextView) findViewById(R.id.niketextView);
        isbnTextView = (TextView) findViewById(R.id.isbn_txtview);
        bookTitleView = (TextView) findViewById(R.id.book_title_view);
        liketextview = (TextView) findViewById(R.id.likenumber_hometextView);
        retailtextview = (TextView) findViewById(R.id.Retailer_textView);
        productimageview = (ImageView) findViewById(R.id.deal_imageView);
        userimageview = (ImageView) findViewById(R.id.userphoto_details_imageView);
        likebutimagebut = (ImageButton) findViewById(R.id.like_home_imageButton);
        commentimagebut = (ImageButton) findViewById(R.id.comment_home_imageButton);
        shareimagebut = (ImageButton) findViewById(R.id.share_home_imageButton);
        followmagebut = (ImageButton) findViewById(R.id.follow_home_imageButton);
        userrealname_textview = (TextView) findViewById(R.id.name_hometextView);
        commenttextview = (TextView) findViewById(R.id.commentnumber_hometextView);
        starLayout = (LinearLayout) findViewById(R.id.star_layout);
        ratingCountText = (TextView) findViewById(R.id.product_page_reviews_overall_rating_text);
        bookDetailPrice = (TextView) findViewById(R.id.book_detail_price);
        productDescription = (TextView) findViewById(R.id.product_description);
        productDescription.setMovementMethod(new ScrollingMovementMethod());


        starViews = new ImageView[5];
        for (int s = 0; s < starViews.length; s++) {
            starViews[s] = new ImageView(this);
        }

        photo = new Photo();
        Intent getdataintente = getIntent();
        photoID = getdataintente.getStringExtra("photoID");
        final ImageLoader imageLoader = new ImageLoader(BookDetailActivity.this);

        showProgressDialog();
        getBookDetails();


    }


    public int getDays(String begin) throws ParseException, java.text.ParseException {
        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        long start = dateFormat.parse(begin).getTime();
        long end = new Date().getTime(); // 2nd date want to compare
        long diff = (end - start) / (MILLIS_PER_DAY);
        return (int) diff;
    }


    private void getBookDetails() {
        final String myProfile = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
        query.include("user");
        query.getInBackground(photoID, new GetCallback<ParseObject>() {
            public void done(final ParseObject country, ParseException e) {
                if (e == null) {
                    photo = country;
                    isbnBookString = "https://www.googleapis.com/books/v1/volumes?q="+country.get("isbn");
                    final Homeitem map = new Homeitem();
                    int inappcount = country.getInt("inapp");
                    if (inappcount < 4) {

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        Date today = calendar.getTime();
                        Date c = (Date) country.get("expiry");


                        String formattedDate = df.format(c);

                        //Calculate days between using JodaTime API
                        DateTime date1 = DateTime.parse(formattedDate);
                        DateTime date2 = DateTime.now().minusDays(1);
                        int days = Days.daysBetween(date2, date1).getDays();

                        if (today.compareTo(c) > 0) {
                            expiredatetextview.setText("EXPIRED ");
                            ParseFile image = (ParseFile) country.get("image");
                            ParseUser homeuser = country.getParseUser("user");


                            photouserID = homeuser.getObjectId();
                            if (!((String) homeuser.get("name") == null)) {
                                userrealname_textview.setText((String) homeuser.get("name"));
                            } else {
                                userrealname_textview.setText("");
                            }

                            bookDetailPrice.setText("$" + country.get("price"));
                            taglinetextview.setText("#" + country.get("tagline"));
                            isbnTextView.setText((String) country.get("isbn"));
                            bookTitleView.setText((String) country.get("title"));

                            if (!(homeuser.get("photo") == null)) {
                                final ParseFile userimage = (ParseFile) homeuser.get("photo");
                                Glide.with(BookDetailActivity.this).load(userimage.getUrl())
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(BookDetailActivity.this))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(userimageview);


                            }


                            Glide.with(BookDetailActivity.this).load(image.getUrl()).into(productimageview);

                            retailtextview.setText((String) country.get("retail"));
                            if (!(country.<String>getList("userlike") == null)) {
                                likes = (ArrayList<String>) country.get("userlike");
                            }
                            if (!(country.<String>getList("userfollow") == null)) {
                                follows = (ArrayList<String>) country.get("userfollow");

                            }
                            commenttextview.setText(Integer.toString(country.getInt("commentnumber")));
                            liketextview.setText(Integer.toString(likes.size()));
                            likenumber = likes.size();
                            String likecompare = likes.toString();
                            if (likecompare.contains(currentuser.getObjectId())) {
                                likeflag = true;
                                likebutimagebut.setImageResource(R.drawable.heart_pressed);
                            } else {
                                likebutimagebut.setImageResource(R.drawable.heart_outline);
                                likeflag = false;
                            }
                            likes.clear();

                            String followcompare = follows.toString();
                            if (followcompare.contains(currentuser.getObjectId())) {
                                followmagebut.setImageResource(R.drawable.follow_pressed);
                                followfalg = true;
                            } else {
                                followmagebut.setImageResource(R.drawable.follow_outline);
                                followfalg = false;
                            }
                            follows.clear();

                        } else if (today.compareTo(c) < 0) {

                            expiredatetextview.setText("EXPIRES IN : " + days + " day(s)");
                            ParseFile image = (ParseFile) country.get("image");
                            ParseUser homeuser = country.getParseUser("user");


                            photouserID = homeuser.getObjectId();
                            if (!((String) homeuser.get("name") == null)) {
                                userrealname_textview.setText((String) homeuser.get("name"));
                            } else {
                                userrealname_textview.setText("");
                            }

                            taglinetextview.setText("#" + country.get("tagline"));
                            bookDetailPrice.setText("$" + country.get("price"));
                            isbnTextView.setText((String) country.get("isbn"));
                            bookTitleView.setText((String) country.get("title"));


                            if (!(homeuser.get("photo") == null)) {
                                final ParseFile userimage = (ParseFile) homeuser.get("photo");
                                Glide.with(BookDetailActivity.this).load(userimage.getUrl())
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(BookDetailActivity.this))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(userimageview);

                            }


                            Glide.with(BookDetailActivity.this).load(image.getUrl()).into(productimageview);

                            retailtextview.setText((String) country.get("retail"));
                            if (!(country.<String>getList("userlike") == null)) {
                                likes = (ArrayList<String>) country.get("userlike");
                            }
                            if (!(country.<String>getList("userfollow") == null)) {
                                follows = (ArrayList<String>) country.get("userfollow");

                            }
                            commenttextview.setText(Integer.toString(country.getInt("commentnumber")));
                            liketextview.setText(Integer.toString(likes.size()));
                            String likecompare = likes.toString();
                            if (likecompare.contains(currentuser.getObjectId())) {
                                likebutimagebut.setImageResource(R.drawable.heart_pressed);
                            } else {
                                likebutimagebut.setImageResource(R.drawable.heart_outline);
                            }
                            likes.clear();

                            String followcompare = follows.toString();
                            if (followcompare.contains(currentuser.getObjectId())) {
                                followmagebut.setImageResource(R.drawable.follow_pressed);
                            } else {
                                followmagebut.setImageResource(R.drawable.follow_outline);
                            }
                            follows.clear();


                        }

                        userimageview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String itemSelected = country.getObjectId();
                                if (itemSelected != null) {
                                    Intent sellerInfo = new Intent(BookDetailActivity.this, OtherSellerProfileActivity.class);
                                    sellerInfo.putExtra(OtherSellerProfileActivity.EXTRA_USER_ID, country.getParseUser("user").getObjectId());
                                    BookDetailActivity.this.startActivity(sellerInfo);

                                } else{
                                    Toast.makeText(BookDetailActivity.this, R.string.error_label, Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                        findViewById(R.id.seller_info).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent sellerInfo = new Intent(BookDetailActivity.this, OtherSellerProfileActivity.class);
                                sellerInfo.putExtra(OtherSellerProfileActivity.EXTRA_USER_ID, country.getParseUser("user").getObjectId());
                                BookDetailActivity.this.startActivity(sellerInfo);

                            }
                        });

                        new GetRatingInfo().execute(isbnBookString);
                    }

                    //  ok = true;
                    hideProgressDialog();
                }

            }
        });


        likebutimagebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (likeflag == true) {
                    likebutimagebut.setImageResource(R.drawable.heart_outline);

                    final String like = Integer.toString(likenumber - 1);
                    likenumber = likenumber - 1;
                    liketextview.setText(like);
                    likeflag = false;
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                    query.getInBackground(photoID, new GetCallback<ParseObject>() {
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
                                if (!(currentuser.getObjectId().equals(photouserID))) {


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
                    likebutimagebut.setImageResource(R.drawable.heart_pressed);
                    likeflag = true;
                    final String like = Integer.toString(likenumber + 1);
                    likenumber = likenumber + 1;
                    liketextview.setText(like);
//                        mProgressDialog.show();


                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                    query.getInBackground(photoID, new GetCallback<ParseObject>() {
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
                                if (!(currentuser.getObjectId().equals(photouserID))) {
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
        });


        followmagebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (followfalg) {
                    followmagebut.setImageResource(R.drawable.follow_outline);
//                        mProgressDialog.show();
//                        follows.clear();
                    followfalg = false;

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                    query.getInBackground(photoID, new GetCallback<ParseObject>() {
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
                                if (!(currentuser.getObjectId().equals(photouserID))) {
                                    ParseObject alert = new ParseObject("Alert");
                                    alert.put("fromuser", ParseUser.getCurrentUser());
                                    alert.put("contente", (String) ParseUser.getCurrentUser().get("name") + " unfollow you");
                                    Log.d("Unfollow", (String) ParseUser.getCurrentUser().get("name") + " unfollow you");
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
                    followmagebut.setImageResource(R.drawable.follow_pressed);
                    followfalg = true;

//                        mProgressDialog.show();
//                        follows.clear();

//                        adapter = new homeItemListAdapter(HomeActivity.this,
//                                data);
//                        listview.setAdapter(adapter);
//                        listview.setSelectionFromTop(position, 0);
//                        mProgressDialog.dismiss();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                    query.getInBackground(photoID, new GetCallback<ParseObject>() {
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
                                follow.put("user", ParseUser.getCurrentUser());
                                follow.put("photo", photo);
                                follow.saveInBackground();
                                ParseUser currentuser = ParseUser.getCurrentUser();
                                if (!(currentuser.getObjectId().equals(photouserID))) {
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

        });

        commentimagebut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent zoom = new Intent(BookDetailActivity.this, MyProfileActivity.class);
                zoom.putExtra("photoID", photoID);
                startActivity(zoom);
                // Toast.makeText(HomeActivity.this,worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();

                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }

        });


        shareimagebut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
//                    intent.putExtra(Intent.EXTRA_SUBJECT, ParseUser.getCurrentUser().getUsername());
//
//                    intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(), bitmap));
//                    intent.setType("image/*");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                    startActivity(Intent.createChooser(intent, null));


                productimageview.buildDrawingCache();
                Bitmap bm = productimageview.getDrawingCache();

                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Punch");
                imagesFolder.mkdirs();
                String fileName = "image" + ".jpg";
                File output = new File(imagesFolder, fileName);
                Uri uriSavedImage = Uri.fromFile(output);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                OutputStream fos = null;

                try {
                    fos = getContentResolver().openOutputStream(uriSavedImage);
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
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(Intent.createChooser(intent, null));


            }
        });


        moreimagebut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        BookDetailActivity.this);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        BookDetailActivity.this,
                        android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Report inappropriate");
                arrayAdapter.add("Report expired");

                builderSingle.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.dismiss();
                                hideProgressDialog();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                        BookDetailActivity.this);
                                builderInner.setMessage(strName);
                                builderInner.setPositiveButton("Submit",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {

                                                if (strName.equals("Report expired")) {


                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                                                    query.getInBackground(photoID, new GetCallback<ParseObject>() {
                                                        public void done(ParseObject photo_origin, ParseException e) {
                                                            if (e == null) {
//                                                                expiredatetextview.setText("EXPIRED");

                                                                photo = photo_origin;
                                                                Date today = new Date(System.currentTimeMillis());
                                                                photo.put("expiry", today);
//                                                                    photo.put("expflag","true");
                                                                photo.saveInBackground();
                                                                ParseUser touser = photo_origin.getParseUser("user");
                                                                ParseUser currentuser = ParseUser.getCurrentUser();
                                                                if (!(currentuser.getObjectId().equals(photouserID))) {
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
                                                    query.getInBackground(photoID, new GetCallback<ParseObject>() {
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
                                                                if (!(currentuser.getObjectId().equals(photouserID))) {
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
                                                //dialog.dismiss();
                                                hideProgressDialog();
                                            }
                                        });
                                builderInner.show();
                            }
                        });
                builderSingle.show();


            }
        });


    }


    private class GetRatingInfo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... bookURLs) {
            StringBuilder bookBuilder = new StringBuilder();

            for (String bookSearchURL : bookURLs) {
                HttpClient bookClient = HttpClientBuilder.create().build();
                HttpGet bookGet = new HttpGet(bookSearchURL);
                try {
                    HttpResponse bookResponse = bookClient.execute(bookGet);
                    StatusLine bookSearchStatus = bookResponse.getStatusLine();
                    if (bookSearchStatus.getStatusCode() == 200) {
                        //we have a result
                        HttpEntity bookEntity = bookResponse.getEntity();
                        InputStream bookContent = bookEntity.getContent();
                        InputStreamReader bookInput = new InputStreamReader(bookContent);
                        BufferedReader bookReader = new BufferedReader(bookInput);

                        String lineIn;
                        while ((lineIn = bookReader.readLine()) != null) {
                            bookBuilder.append(lineIn);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            return bookBuilder.toString();
        }

        protected void onPostExecute(String result) {
            final String AVERAGE_RATING = "averageRating";
            final String RATING_COUNT = "ratingsCount";
            final String DESC = "description";

            try {
                JSONObject resultObject = new JSONObject(result);


                if (resultObject.has("items")) {
                    JSONArray bookArray = resultObject.getJSONArray("items");

                    double decNumStars;
                    String ratingCount;
                    String description;

                    JSONObject bookObject = bookArray.getJSONObject(0);
                    JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");

                    if (volumeObject.has(AVERAGE_RATING)) {
                        decNumStars = Double.parseDouble(volumeObject.getString(AVERAGE_RATING));
                        int numStars = (int) decNumStars;
                        starLayout.setTag(numStars);
                        starLayout.removeAllViews();

                        for (int s = 0; s < numStars; s++) {
                            starViews[s].setImageResource(R.drawable.rating_star);
                            starLayout.addView(starViews[s]);
                        }

                    } else {
                        starLayout = null;
                    }

                    if (volumeObject.has(DESC)) {
                        description = volumeObject.getString(DESC);
                    } else {
                        description = "no description available";
                    }

                    if (volumeObject.has(RATING_COUNT)) {
                        ratingCount = volumeObject.getString(RATING_COUNT);
                    } else {
                        ratingCount = "no rating available";
                    }
                    ratingCountText.setText(ratingCount);
                    productDescription.setText(description);

                }


            } catch (JSONException e) {
                ratingCountText.setText("");
                e.printStackTrace();
            }

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
            //photo.saveInBackground();
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
