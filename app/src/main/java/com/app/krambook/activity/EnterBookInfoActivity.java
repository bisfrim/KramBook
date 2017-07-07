package com.app.krambook.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.models.Photo;
import com.app.krambook.parse_session.KramBookInitialize;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import customfonts.MyEditText;
import customfonts.MyTextView;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class EnterBookInfoActivity extends AppCompatActivity {
    private final String LOG_TAG = EnterBookInfoActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private ParseFile image;
    private ProgressDialog progressDialog;
    private Photo photo;
    private Bitmap thumbImg;
    String bookSearchString;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NETWORK_STATUS_OK, NETWORK_STATUS_DOWN})
    public @interface NetworkStatus {
    }

    public static final int NETWORK_STATUS_OK = 0;
    public static final int NETWORK_STATUS_DOWN = 1;

    ImageView priviewimage;
    String fileuri;
    Bitmap fileBitmap;
    String catagory, condition;
    String location;
    String tagling;
    String retailerstr;
    String isbn, title, author, price;
    String gallerystr;
    Integer DATE_DIALOG_ID = 999;
    ImageButton categoryimagebut, conditionimageBtn;
    int mYear, mMonth, mDay;
    byte[] saveData;

    MyTextView bookExpireDate, category_textView, condition_textView;
    MyEditText tagline_edit_text, isbn_edit_txtview, title_edit_txtview;
    MyEditText author_edit_txtview, retailer_edit_txtview, price_edit_txtview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_book_info);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.uploading));

        photo = new Photo();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        priviewimage = (ImageView) findViewById(R.id.product_deal_imageView);
        bookExpireDate = (MyTextView) findViewById(R.id.expirer_deal_textView);
        categoryimagebut = (ImageButton) findViewById(R.id.category_imageButton);
        conditionimageBtn = (ImageButton) findViewById(R.id.condition_imageButton);
        tagline_edit_text = (MyEditText) findViewById(R.id.tagline_editText);
        category_textView = (MyTextView) findViewById(R.id.category_deal_textView);
        condition_textView = (MyTextView) findViewById(R.id.condition_deal_textView);
        isbn_edit_txtview = (MyEditText) findViewById(R.id.isbn_editText);
        title_edit_txtview = (MyEditText) findViewById(R.id.book_title_editText);
        author_edit_txtview = (MyEditText) findViewById(R.id.book_author_editText);
        retailer_edit_txtview = (MyEditText) findViewById(R.id.ratailer_deal_editText);
        price_edit_txtview = (MyEditText) findViewById(R.id.price_editText);

        Intent datagetintent = getIntent();
        fileuri = datagetintent.getStringExtra("serchresult");
        fileBitmap = (Bitmap) datagetintent.getParcelableExtra("serchresult");
        gallerystr = datagetintent.getStringExtra("gallery");
        catagory = datagetintent.getStringExtra("cotagoryresult");
        condition = datagetintent.getStringExtra("condition");
        location = datagetintent.getStringExtra("location");
        tagling = datagetintent.getStringExtra("tagline");
        isbn = datagetintent.getStringExtra("isbn");
        title = datagetintent.getStringExtra("title");
        author = datagetintent.getStringExtra("author");
        price = datagetintent.getStringExtra("price");

        bookSearchString = "https://www.googleapis.com/books/v1/volumes?" +
                "q=isbn:" + isbn + "&key=AIzaSyCeQI70KNMhJ9pzWW2ikn7MsQglgLg_crs";
        //new GetBookInfo().execute(bookSearchString);

        BitmapFactory.Options options = new BitmapFactory.Options();
        final Bitmap bitmap = BitmapFactory.decodeFile(fileuri,
                options);

        Matrix matrix = new Matrix();
//        if(falg.equals("1"))
        matrix.postRotate(90);
//        else if(falg.equals("0"))
//        {matrix.postRotate(90);}


        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        priviewimage.setImageBitmap(bitmap);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        saveData = bos.toByteArray();


        Calendar c = Calendar.getInstance();


        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        String formattedDate = df.format(c.getTime());
        Date date = new Date();

        int datestr = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int yearstr = c.get(Calendar.YEAR);


        String current_date = String.format("%02d/%02d/%d", datestr, month, yearstr);
        bookExpireDate.setText(current_date);


        bookExpireDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(EnterBookInfoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                bookExpireDate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();

            }
        });


        if (!(catagory == null)) {
            category_textView.setText(catagory);
        }
        if (!(condition == null)) {
            condition_textView.setText(condition);
        }

        if (!(tagling == null)) {
            tagline_edit_text.setText(tagling);
        }

        if (!(retailerstr == null)) {
            retailer_edit_txtview.setText(retailerstr);
        }
        if (!(isbn == null)) {
            isbn_edit_txtview.setText(isbn);
        }
        if (!(title == null)) {
            title_edit_txtview.setText(title);
        }
        if (!(author == null)) {
            author_edit_txtview.setText(author);
        }
        if (!(price == null)) {
            price_edit_txtview.setText(price);
        }

        categoryimagebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                intent = new Intent(EnterBookInfoActivity.this, CategoryActivity.class);
                intent.putExtra("serchresult", fileuri);
                //intent.putExtra("condition", condition_textView.getText().toString());
                intent.putExtra("tagline", tagline_edit_text.getText().toString());
                intent.putExtra("isbn", isbn_edit_txtview.getText().toString());
                intent.putExtra("title", title_edit_txtview.getText().toString());
                intent.putExtra("author", author_edit_txtview.getText().toString());
                intent.putExtra("publisher", retailer_edit_txtview.getText().toString());
                intent.putExtra("price", price_edit_txtview.getText().toString());

                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });

        conditionimageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentContition;
                intentContition = new Intent(EnterBookInfoActivity.this, ConditionActivity.class);
                intentContition.putExtra("serchresult", fileuri);
                intentContition.putExtra("cotagoryresult", catagory);
                intentContition.putExtra("tagline", tagline_edit_text.getText().toString());
                intentContition.putExtra("isbn", isbn_edit_txtview.getText().toString());
                intentContition.putExtra("title", title_edit_txtview.getText().toString());
                intentContition.putExtra("author", author_edit_txtview.getText().toString());
                intentContition.putExtra("publisher", retailer_edit_txtview.getText().toString());
                intentContition.putExtra("price", price_edit_txtview.getText().toString());
                startActivity(intentContition);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });

    }

    private boolean passValidation(Context context) {
        boolean resultOk = true;
        if (tagline_edit_text != null && tagline_edit_text.getText().toString().isEmpty()) {
            tagline_edit_text.setError("Please enter tagline");
            resultOk = false;
        } else if (isbn_edit_txtview != null && isbn_edit_txtview.getText().toString().isEmpty()) {
            isbn_edit_txtview.setError("Please enter isbn");
            resultOk = false;
        } else if (title_edit_txtview != null && title_edit_txtview.getText().toString().isEmpty()) {
            title_edit_txtview.setError("Please enter title");
            resultOk = false;
        } else if (author_edit_txtview != null && author_edit_txtview.getText().toString().isEmpty()) {
            author_edit_txtview.setError("Please enter the author");
            resultOk = false;
        } else if (retailer_edit_txtview != null && retailer_edit_txtview.getText().toString().isEmpty()) {
            retailer_edit_txtview.setError("Please enter retailer");
            resultOk = false;
        } else if (price_edit_txtview != null && price_edit_txtview.getText().toString().isEmpty()) {
            price_edit_txtview.setError("Please enter price");
            resultOk = false;
        } else if (condition_textView != null && condition_textView.getText().toString().isEmpty()) {
            condition_textView.setError("Condition cannot be empty");
            resultOk = false;
        } else if (bookExpireDate != null && bookExpireDate.getText().toString().isEmpty()) {
            bookExpireDate.setError("Please pick expire date");
            resultOk = false;
        }

        return resultOk;
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
            onBackPressed();
            return true;
        }

        if (id == R.id.save_book_info) {
            //Toast.makeText(this, "All notifications marked as read!", Toast.LENGTH_LONG).show();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date expiredate = null;
            try {
                expiredate = df.parse(bookExpireDate.getText().toString());
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            Date today = new Date(System.currentTimeMillis());
            if (today.compareTo(expiredate) > 0 || today.compareTo(expiredate) == 0) {

                Toast.makeText(EnterBookInfoActivity.this, "Expired date is over,Please select other!", Toast.LENGTH_LONG).show();
            } else if (tagline_edit_text.getText().length() == 0) {
                tagline_edit_text.setError("Please enter a tagline");
            } else if (isbn_edit_txtview.getText().length() == 0) {
                isbn_edit_txtview.setError("Please enter the isbn");
            } else if (title_edit_txtview.getText().length() == 0) {
                title_edit_txtview.setError("Please enter a title");
            } else if (author_edit_txtview.getText().length() == 0) {
                author_edit_txtview.setError("Please enter the author");
            } else if (retailer_edit_txtview.getText().length() == 0) {
                retailer_edit_txtview.setError("Please enter retailer");
            } else if (price_edit_txtview.getText().length() == 0) {
                price_edit_txtview.setError("Please enter price");
            } else if (category_textView.getText().length() == 0) {
                category_textView.setError("Please select category");
            } else if (condition_textView.getText().length() == 0) {
                condition_textView.setError("Please select condition");
            } else if (bookExpireDate.getText().length() == 0) {
                bookExpireDate.setError("Please pick expire date");
            } else {
                Calendar c = Calendar.getInstance();

                image = new ParseFile("photo.jpg", saveData);
                image.saveInBackground();
                showProgressDialog();

                //Associate the picture with the current user
                photo.setUser(ParseUser.getCurrentUser());
                //Add the image
                photo.setImage(image);
                if (photo.getObjectId() != null) {
                    photo.setColgId(KramBookInitialize.global.getSomeVariable());
                }
                //Add the thumbnail
                photo.setLike("12");
                photo.setCategory(catagory);
                photo.setCondition(condition);
                photo.setExpiry(expiredate);
                photo.setTagline(tagline_edit_text.getText().toString());
                photo.setIsbn(isbn_edit_txtview.getText().toString());
                photo.setTitle(title_edit_txtview.getText().toString());
                photo.setBookAuthor(author_edit_txtview.getText().toString());
                photo.setRetail(retailer_edit_txtview.getText().toString());
                photo.setPrice(price_edit_txtview.getText().toString());
                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);
                photo.setACL(acl);
                photo.put("expflag", "false");

                //save the picture and return to the MainActivity
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        hideProgressDialog();

                        if (e == null) {
                            ParseQuery<ParseObject> check_available = ParseQuery.getQuery("Follow");
                            //check_available.whereEqualTo("user", ParseUser.getCurrentUser());
                            check_available.whereEqualTo("photo", photo);
                            check_available.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject o : objects) {
                                            String username = o.getString(ParseUser.getCurrentUser().getObjectId());
                                            ParsePush parsePush = new ParsePush();
                                            ParseQuery pQuery = ParseInstallation.getQuery();
                                            pQuery.whereEqualTo("user", username).whereEqualTo("isReceive", true);
                                            parsePush.sendMessageInBackground("This item " + title_edit_txtview.getText().toString() + " in your wish list is available", pQuery);
                                        }
                                    } else {
                                        Toast.makeText(EnterBookInfoActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            Toast.makeText(EnterBookInfoActivity.this, "Your book is posted.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EnterBookInfoActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.right_out);

                        } else {
                            try {
                                //showProgressDialog();
                                //Toast.makeText(EnterBookInfoActivity.this, "Your book is posted.", Toast.LENGTH_LONG).show();
                                Toast.makeText(EnterBookInfoActivity.this, "Error occur during posting book.", Toast.LENGTH_LONG).show();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

            }
        }

        return super.onOptionsItemSelected(item);
    }


    private class GetBookInfo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setCancelable(false);
            showProgressDialog();
        }

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
            final String TITLE = "title";
            final String SUBTITLE = "subtitle";
            final String AUTHORS = "authors";
            final String DESC = "description";
            final String PUBLISHER = "publisher";
            final String CATEGORIES = "categories";
            final String IMG_URL_PATH = "imageLinks";
            final String IMG_URL = "thumbnail";

            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray bookArray = resultObject.getJSONArray("items");
                JSONObject bookObject = bookArray.getJSONObject(0);
                JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");

                String title = volumeObject.getString(TITLE);
                String subtitle = "";
                if (volumeObject.has(SUBTITLE)) {
                    subtitle = volumeObject.getString(SUBTITLE);
                }


               /* String author="";
                if(volumeObject.has(AUTHORS)){
                    author = volumeObject.getString(AUTHORS);
                }*/


                StringBuilder authorBuild = new StringBuilder("");
                try {
                    JSONArray authorArray = volumeObject.getJSONArray(AUTHORS);
                    for (int a = 0; a < authorArray.length(); a++) {
                        if (a > 0) authorBuild.append(", ");
                        authorBuild.append(authorArray.getString(a));
                    }
                    author_edit_txtview.setText(authorBuild.toString());
                } catch (JSONException jse) {
                    author_edit_txtview.setText("");
                    jse.printStackTrace();
                }


                String categories = "";
                if (volumeObject.has(CATEGORIES)) {
                    categories = volumeObject.getString(CATEGORIES);
                }

                String publisher = "";
                if (volumeObject.has(PUBLISHER)) {
                    publisher = volumeObject.getString(PUBLISHER);
                }

                String imgUrl = "";
                if (volumeObject.has(IMG_URL_PATH) && volumeObject.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                    imgUrl = volumeObject.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
                }


                //JSONObject imageInfo = volumeObject.getJSONObject(IMG_URL_PATH);
                new GetBookThumb().execute(imgUrl);


                title_edit_txtview.setText(title);
                tagline_edit_text.setText(subtitle);
                category_textView.setText(categories);
                retailer_edit_txtview.setText(publisher);


            } catch (Exception e) {
                priviewimage.setImageBitmap(null);
                e.printStackTrace();
            }
            hideProgressDialog();
        }
    }


    private class GetBookThumb extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... thumbURLs) {
            try {
                URL thumbURL = new URL(thumbURLs[0]);
                URLConnection thumbConn = thumbURL.openConnection();
                thumbConn.connect();

                InputStream thumbIn = thumbConn.getInputStream();
                BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);

                thumbImg = BitmapFactory.decodeStream(thumbBuff);
                thumbBuff.close();
                thumbIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result) {
            priviewimage.setImageBitmap(thumbImg);
        }

    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    static private void setNetworkStatus(Context c, @EnterBookInfoActivity.NetworkStatus int networkStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_network_status_key), networkStatus);
        spe.commit();
    }
}
