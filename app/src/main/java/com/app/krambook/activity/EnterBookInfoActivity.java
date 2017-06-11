package com.app.krambook.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import customfonts.MyEditText;
import customfonts.MyTextView;

public class EnterBookInfoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ParseFile image;
    private ProgressDialog progressDialog;
    private Photo photo;

    ImageView priviewimage;
    String fileuri;
    Bitmap fileBitmap;
    String catagory;
    String location;
    String tagling;
    String retailerstr;
    String isbn, title, author, price;
    String gallerystr;
    Integer DATE_DIALOG_ID = 999;
    ImageButton categoryimagebut;
    int mYear, mMonth, mDay;
    byte[] saveData;

    MyTextView bookExpireDate, category_textView;
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
        tagline_edit_text = (MyEditText) findViewById(R.id.tagline_editText);
        category_textView = (MyTextView) findViewById(R.id.category_deal_textView);
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
        location = datagetintent.getStringExtra("location");
        tagling = datagetintent.getStringExtra("tagline");
        isbn = datagetintent.getStringExtra("isbn");
        title = datagetintent.getStringExtra("title");
        author = datagetintent.getStringExtra("author");
        price = datagetintent.getStringExtra("price");


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
                intent.putExtra("tagline", tagline_edit_text.getText().toString());
                intent.putExtra("isbn", isbn_edit_txtview.getText().toString());
                intent.putExtra("title", title_edit_txtview.getText().toString());
                intent.putExtra("author", author_edit_txtview.getText().toString());
                intent.putExtra("retailer", retailer_edit_txtview.getText().toString());
                intent.putExtra("price", price_edit_txtview.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });

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
            Date today=new Date(System.currentTimeMillis());
            if (today.compareTo(expiredate)>0||today.compareTo(expiredate)==0){

                Toast.makeText(EnterBookInfoActivity.this,"Expired date is over,Please select other!",Toast.LENGTH_LONG).show();
            }
            else if(tagline_edit_text.getText().length() == 0){
                tagline_edit_text.setError("Please enter a tagline");
            }
            else if(isbn_edit_txtview.getText().length() == 0){
                isbn_edit_txtview.setError("Please enter the isbn");
            }
            else if(title_edit_txtview.getText().length() == 0){
                title_edit_txtview.setError("Please enter a title");
            }else if(author_edit_txtview.getText().length() == 0){
                author_edit_txtview.setError("Please enter the author");
            }else if(retailer_edit_txtview.getText().length() == 0){
                retailer_edit_txtview.setError("Please enter retailer");
            }else if(price_edit_txtview.getText().length() == 0){
                price_edit_txtview.setError("Please enter price");
            }else if(category_textView.getText().length() == 0){
                category_textView.setError("Please enter category");
            }else if(bookExpireDate.getText().length() == 0){
                bookExpireDate.setError("Please pick expire date");
            }else{
                Calendar c = Calendar.getInstance();

                image = new ParseFile("photo.jpg", saveData);
                image.saveInBackground();
                showProgressDialog();

                //Associate the picture with the current user
                photo.setUser(ParseUser.getCurrentUser());
                //Add the image
                photo.setImage(image);
                //Add the thumbnail
                photo.setLike("12");
                photo.setCategory(catagory);
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
                photo.put("expflag","false");

                //save the picture and return to the MainActivity
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        hideProgressDialog();

                        Intent intent = new Intent(EnterBookInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);

                        if (e == null) {

                        } else {
                            try {
                                showProgressDialog();
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

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }
}
