package com.app.krambook.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.models.UserData;
import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SignUpDetails extends AppCompatActivity {

    final Context context = this;
    DatePicker calendar;
    java.sql.Date dob = null;
    Dialog dialog;
    DatePickerDialog dpg;
    UserData user;
    TextView dpif;
    int day,year,month;
    int sizeInBytes = 0;
    int MAX_BYTES = 12000;

    private boolean isdp = false;

    //Local fields
    private int currentAge;

    private Toolbar mToolbar;

    static final int DATE_DIALOG_ID = 999;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String dateFormat = "dd-MM-yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    private Date convertDate;
    private boolean doubleBackToExitPressedOnce = false;

    //Callback
    private final FetchCallback fetchUserCallback = new FetchCallback();

    private void updateInfo() {
        if (fetchUserCallback != null)
        user.fetchIfNeededInBackground(fetchUserCallback);
    }

    EditText date;
    String dept, gender;
    Spinner sdept, sgender;
    Bitmap imv1, imv2, imv3;

    //Layout
    CardView mCardviewLayout;
    //ImageView
    ImageView mProfilePhoto, userImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        user = (UserData) UserData.getCurrentUser();

        mCardviewLayout = (CardView) findViewById(R.id.card_view_layout);
        sdept = (Spinner) findViewById(R.id.spinner_dept);
        sgender = (Spinner) findViewById(R.id.spinner_gender);
        date = (EditText) findViewById(R.id.dob);
        dpif = (TextView) findViewById(R.id.sign_ifdp);
        mProfilePhoto = (ImageView) mCardviewLayout.findViewById(R.id.user_profile_photo);



        setDatePickerListener(date);

        findViewById(R.id.sign_btn_dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectProfileOptions();
            }
        });

        findViewById(R.id.sign_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                proceedSaveBtn();

            }
        });


    }

    private int calculatedAge(Date nowAge) {
        Calendar age = Calendar.getInstance();
        age.setTime(nowAge); //Date of birth

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date()); //Now age

        return currentDate.get(Calendar.YEAR) - age.get(Calendar.YEAR);
    }


    private void proceedSaveBtn(){
        final String birthday = date.getText().toString().trim();
        final ProgressDialog dlg = new ProgressDialog(SignUpDetails.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Updating Profile. A moment please...");
        dlg.show();

        imv1 = BitmapFactory.decodeResource(getResources(), R.drawable.male);
        imv2 = BitmapFactory.decodeResource(getResources(), R.drawable.female);
        imv3 = BitmapFactory.decodeResource(getResources(), R.drawable.unspecified);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();


        dept = String.valueOf(sdept.getSelectedItem());
        gender = String.valueOf(sgender.getSelectedItem());


        if(isdp==false) {
            if (gender.equals("Male")) {
                //user.setGenderIsMale(true);
                imv1.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                ParseFile file = new ParseFile("dp.png", data);
                user.put("Gender_photo", file);
                file.saveInBackground();
                //user.saveInBackground();
            } else if (gender.equals("Female")) {
                //user.setGenderIsMale(false);
                imv2.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                ParseFile file = new ParseFile("dp.png", data);
                user.put("Gender_photo", file);
                file.saveInBackground();
                // user.saveInBackground();
            } else {
                imv3.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                ParseFile file = new ParseFile("dp.png", data);
                user.put("Gender_photo", file);
                file.saveInBackground();
                //user.saveInBackground();
            }
        }



        if (date.getText().toString().equals("")){
            Toast.makeText(SignUpDetails.this, "Enter Date of Birth", Toast.LENGTH_LONG).show();
            dlg.hide();
        }else{

            user.put("Dept", dept);
            user.put("Gender", gender);

            //Convert DOB to birthday
            try {
                convertDate = sdf.parse(birthday); //parse the date of birth format as string (dd-mm-yyyy)
                currentAge = calculatedAge(convertDate); //calculate the parsed format from calculated method


            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            user.setAge(currentAge);
            user.put("isComplete", true);

            try {
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(isdp==false) {
                Toast.makeText(SignUpDetails.this, "Details Updated. Please sign-in", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(SignUpDetails.this, "Details Updated", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(SignUpDetails.this, Proceed.class);
            startActivity(intent);
        }

    }



    //fetch and update user info from this callback
    private class FetchCallback implements GetCallback<ParseUser> {

        @Override
        public void done(ParseUser parseUser, ParseException e) {
            if (e != null) {
                mCardviewLayout.setVisibility(View.GONE);
            } else if (parseUser != null) {
                user = (UserData) parseUser;
                //usernameField.setText(mCurrentUser.getNickname());
                //fit photo into our placeholder
                Glide.with(SignUpDetails.this)
                        .load(user.getPhotoUrl())
                        .bitmapTransform(new CropCircleTransformation(Glide.get(SignUpDetails.this).getBitmapPool()))
                        .placeholder(R.drawable.profile_photo_placeholder)
                        .into(mProfilePhoto);

            }


        }
    }


    private void selectProfileOptions() {
        EasyImage.openChooserWithDocuments(this, "Pick source", 0);
        //EasyImage.openChooserWithGallery(this, "Pick source", 0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }


            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the image

                File compressedImage = null;
                compressedImage = Compressor.getDefault(getApplicationContext()).compressToFile(imageFile);
                onPhotoReturned(compressedImage);
            }


            //incase we press cancel on camera delete the photo
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(SignUpDetails.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });

    }

    //activity result automatically calls this
    private void onPhotoReturned(File photoFile) {

        Matrix matrix = new Matrix(); //trying to rotate the image to be centered - deosn't work
        //matrix.reset();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


        final ParseFile uploadImage = new ParseFile(stream.toByteArray());

        final ProgressDialog dialog = new ProgressDialog(SignUpDetails.this);
        dialog.setMessage(getString(R.string.progress_uploading));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        uploadImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    return;
                dialog.dismiss();
                user.setProfilePhoto(uploadImage);
                user.setProfilePhotoThumb(uploadImage);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Snackbar snackbar = Snackbar.make(mCardviewLayout, "Changes successful saved", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.setAction("UPDATE PROFILE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //fetusercallacks here
                                user.fetchIfNeededInBackground(fetchUserCallback);
                            }
                        });
                        snackbar.show();
                    }
                });
                //Log.d("PICKED FILE", photoFile.toString());

            }
        });
    }


    private void setDatePickerListener(final EditText birthday) {
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showDatePicker((EditText) v);
            }
        });
    }

    private void showDatePicker(final EditText birthday) {
        DatePickerDialog dpd =
                new DatePickerDialog(SignUpDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date selectedDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                        birthday.setText(sdf.format(selectedDate));
                    }
                }, 2016, 0, 1);
        dpd.show();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
