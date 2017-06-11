package com.app.krambook.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import customfonts.MyEditText;
import customfonts.MyTextView;
import id.zelory.compressor.Compressor;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private MyTextView usernameChange, userLocationChange, passwordChange, profilePhotoChange;
    private MyTextView userFullName,username,userLocation,userEmail,userDept;
    private ImageView userProfilePhoto;

    private UserData mCurrentUser;

    MyProfileActivity myProfileActivity;
    LinearLayout contentLayout;

    private FetchCallback fetchUserCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myProfileActivity = new MyProfileActivity();
        fetchUserCallback = new FetchCallback();

        mCurrentUser = (UserData) UserData.getCurrentUser();

        contentLayout = (LinearLayout)findViewById(R.id.linear);
        userFullName = (MyTextView) findViewById(R.id.user_full_name);
        username = (MyTextView) findViewById(R.id.user_name);
        userEmail = (MyTextView) findViewById(R.id.user_email);
        userDept = (MyTextView) findViewById(R.id.user_dept);
        userProfilePhoto = (ImageView)contentLayout.findViewById(R.id.user_profile_photo_change);
        passwordChange = (MyTextView)findViewById(R.id.user_password_label);
        usernameChange = (MyTextView) findViewById(R.id.user_full_name_label);

        mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);

        updateProfilePhotoThumb();

        findViewById(R.id.profile_photo_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageUploadOption();

            }
        });

    }

    public void userFullNameClickHandler(View view) {
        showUserFullNameDialog();


    }

    public void updateInfo() {
        if (fetchUserCallback != null)
            mCurrentUser = new UserData();
        mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
    }


    //update drawer photo with this, it works for all activities opened
    public void updateProfilePhotoThumb() {
        if (userProfilePhoto != null) {
            Glide.with(EditProfileActivity.this).load(mCurrentUser.getPhotoUrl()).centerCrop().into(userProfilePhoto);
        } else {
            Log.d("profileIcon", "can't find");
        }
    }

    protected void showUserFullNameDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(EditProfileActivity.this);
        View promptView = layoutInflater.inflate(R.layout.uername_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfileActivity.this);
        alertDialogBuilder.setView(promptView);

        final MyEditText editText = (MyEditText) promptView.findViewById(R.id.edit_full_name);
        // setup a dialog window
        editText.setText(mCurrentUser.getFullName());
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //resultText.setText("Hello, " + editText.getText());
                        //mCurrentUser = (User)parseUser;
                        //editText.setText(mCurrentUser.getUsername());
                        userFullName.setText(editText.getText());
                        mCurrentUser.setFullName(editText.getText().toString());
                        //myProfileActivity.updateInfo();
                        //updateInfo();


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alert.show();
    }

    public void userPasswordHandler(View view) {
        Intent editProfileIntent = new Intent(EditProfileActivity.this, ResertPasswordActivity.class);
        editProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(editProfileIntent);
    }

    //fetch and update user info from this callback
    private class FetchCallback implements GetCallback<ParseUser> {

        @Override
        public void done(ParseUser parseUser, ParseException e) {

            if (e != null) {
                contentLayout.setVisibility(View.GONE);
            } else if (parseUser != null) {
                userFullName.setText(mCurrentUser.getFullName());
                username.setText(mCurrentUser.getUsername());
                userEmail.setText(mCurrentUser.getEmail());
                userDept.setText(mCurrentUser.getDept());

                Glide.with(EditProfileActivity.this)
                        .load(mCurrentUser.getPhotoUrl())
                        .bitmapTransform(new CropCircleTransformation(Glide.get(EditProfileActivity.this).getBitmapPool()))
                        .placeholder(R.drawable.profile_photo_placeholder)
                        .into(userProfilePhoto);
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    private void selectImageUploadOption() {
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
                onPhotoChange(compressedImage);
            }


            //incase we press cancel on camera delete the photo
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditProfileActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });

    }

    //activity result automatically calls this
    private void onPhotoChange(File photoFile) {

        Matrix matrix = new Matrix(); //trying to rotate the image to be centered - deosn't work
        //matrix.reset();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


        final ParseFile uploadImage = new ParseFile(stream.toByteArray());

        final ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);
        dialog.setMessage(getString(R.string.progress_uploading));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        uploadImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    return;
                dialog.dismiss();
                mCurrentUser.setProfilePhoto(uploadImage);
                mCurrentUser.setProfilePhotoThumb(uploadImage);
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Snackbar snackbar = Snackbar.make(contentLayout, "Changes successful saved", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.setAction("UPDATE PROFILE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //fetusercallacks here
                                mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
                            }
                        });
                        snackbar.show();
                    }
                });
                //Log.d("PICKED FILE", photoFile.toString());

            }
        });
    }
}
