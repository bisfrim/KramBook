package com.app.krambook.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.adapter.SuggestionAdapter;
import com.app.krambook.entity.Validation;
import com.app.krambook.models.Colg;
import com.app.krambook.models.GlobalVariables;
import com.app.krambook.models.UserData;
import com.app.krambook.parse_session.KramBookInitialize;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.xml.sax.Parser;

import java.util.HashMap;
import java.util.Map;

import customfonts.MyAutoCompleteView;
import customfonts.MyEditText;

public class RegisterActivitiy extends AppCompatActivity {

    private Toolbar mToolbar;
    private Typeface fonts1;
    private CheckBox remember;

    private MyEditText full_name,username,email,password,re_enter_password,phoneNumber;
    private MyAutoCompleteView autoCompleteView;
    SharedPreferences mySharedPreferences ;
    HashMap<String, Boolean> userList;
    ProgressDialog mSignUpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activitiy);

        mSignUpDialog = new ProgressDialog(this);
        mSignUpDialog.setMessage(getString(R.string.progress_signup));

        mySharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        userList  = new HashMap<String, Boolean>();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      /*  mToolbar.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_material, null));
        }
        //mToolbar.setTitleTextColor(Color.WHITE);
        //mToolbar.setTitle("Register");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        //autoCompleteView = (MyAutoCompleteView) findViewById(R.id.myautoComplete); //search school autocompleteView
        //autoCompleteView.setAdapter(new SuggestionAdapter(this, autoCompleteView.getText().toString()));

        full_name = (MyEditText) findViewById(R.id.register_full_name);
        username = (MyEditText) findViewById(R.id.register_username);
        email = (MyEditText)findViewById(R.id.register_email);
        phoneNumber = (MyEditText)findViewById(R.id.register_phoneNumber);
        password = (MyEditText)findViewById(R.id.register_pass);
        re_enter_password = (MyEditText)findViewById(R.id.register_reenter_pass);
    }


    private boolean fieldsValid(String fullname, String username, String email, String phone, String password, String passwordConfirmation) {
        Validation validation = Validation.getInstance();

        if(validation.isNetworkAvailable(this)){
            if (validation.isNullOrEmpty(fullname, username, email, phone, password, passwordConfirmation)) {
                validation.showToast(this, "All fields required");
                return false;
            }else if(!validation.validateEmail(email)){
                validation.showToast(this, "Invalid Email Address");
                return false;
            } else if (!validation.matches(password, passwordConfirmation)) {
                validation.showToast(this, "Password does not match");
                return false;
            }else if(!validation.validateMobileNumber(phone)){
                validation.showToast(this, "Enter valid mobile number");
                return false;
            }
        }else{
            validation.showToast(this, "NO Network Connection!");
        }


        return true;
    }

    public void onRegisterClickHandler(View view) {
        //Validation validation = Validation.getInstance();
        boolean validationError = false;
        //final String autoCompleteField = autoCompleteView.getText().toString().trim();
        final String full_nameField = full_name.getText().toString().trim();
        final String usernameField = username.getText().toString().trim();
        final String emailField = email.getText().toString().trim();
        final String phoneField = phoneNumber.getText().toString().trim();
        final String passwordField = password.getText().toString().trim();
        final String passwordConfirmField = re_enter_password.getText().toString().trim();

        if(fieldsValid(full_nameField,usernameField,emailField,phoneField,passwordField,passwordConfirmField)){
            showProgressDialog();

            Glide.with(RegisterActivitiy.this)
                    .load(R.drawable.banner1)
                    .asBitmap()
                    .toBytes()
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            final ParseFile image = new ParseFile(resource);
                            image.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        UserData newUser = new UserData();
                                        Colg colgeDetails = new Colg();
                                        //LocationZipCode myLocationZip = new LocationZipCode();

                                        //newUser.setUserSelectedSchool(autoCompleteField);

                                        newUser.setFullName(full_nameField);
                                        newUser.setUsername(usernameField);
                                        newUser.setEmail(emailField);
                                        newUser.setUserPhoneNumber(phoneField);
                                        newUser.setPassword(passwordField);
                                        newUser.setUserNotification(true);
                                        //newUser.setUserLoc(userLocation);
                                        Log.d("ColgId", "Error" + KramBookInitialize.global.getSomeVariable());
                                        newUser.put("colgId", KramBookInitialize.global.getSomeVariable());

                                        //custom properties
                                        newUser.put("emailVerified", false);
                                        newUser.setIsVerified(false);

                                        //myLocationZip.getAddress(userLocation);
                                        newUser.setInstallation(ParseInstallation.getCurrentInstallation());
                                        newUser.setProfilePhotoThumb(image);
                                        newUser.setProfilePhoto(image);
                                        newUser.signUpInBackground(new SignUpCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                hideProgressDialog();
                                                if (e == null) {
                                                    userList.put(usernameField,true);
                                                    SharedPreferences.Editor edit = mySharedPreferences.edit();
                                                    for(Map.Entry entry: userList.entrySet()) {
                                                        edit.putBoolean((String)entry.getKey(), (Boolean)entry.getValue());
                                                        edit.commit();
                                                    }
                                                    //Log.d("SignUpException", e.toString());
                                                    //RegisterActivity.this.setResult(Constants.RES_CODE_SIGN_UP_SUCCESS, mLoginIntent);
                                                    //Intent i = new Intent(RegisterActivitiy.this, UserDispatchActivity.class);
                                                    //i.putExtra("username", usernameField);
                                                    //startActivity(i);
                                                    //finish();
                                                    Toast.makeText(RegisterActivitiy.this, "Email for account Confirmation has been sent to " + ParseUser.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(RegisterActivitiy.this, UserDispatchActivity.class);
                                                    i.putExtra("username", usernameField);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Log.d("SignUpException", e.toString());
                                                    switch (e.getCode()) {
                                                        case ParseException.USERNAME_TAKEN:
                                                            Toast.makeText(RegisterActivitiy.this
                                                                    , "Sorry, this username has already been taken", Toast.LENGTH_LONG).show();
                                                            break;
                                                        case ParseException.ACCOUNT_ALREADY_LINKED:
                                                            Toast.makeText(RegisterActivitiy.this, "Sorry this account is already registered", Toast.LENGTH_LONG).show();
                                                            break;
                                                        case ParseException.INVALID_EMAIL_ADDRESS:
                                                            Toast.makeText(RegisterActivitiy.this, "Sorry email is invalid, Please correct it and try again", Toast.LENGTH_LONG).show();
                                                            break;
                                                        case ParseException.OBJECT_NOT_FOUND:
                                                            Toast.makeText(RegisterActivitiy.this
                                                                    , "Sorry, those credentials were invalid", Toast.LENGTH_LONG).show();
                                                            break;
                                                        default:
                                                            Toast.makeText(RegisterActivitiy.this
                                                                    , e.getMessage(), Toast.LENGTH_LONG).show();
                                                            break;

                                                    }

                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

        }

    }

    private void showProgressDialog() {
        mSignUpDialog.show();
    }

    private void hideProgressDialog() {
        mSignUpDialog.dismiss();
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
}
