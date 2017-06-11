package com.app.krambook.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.entity.Validation;
import com.app.krambook.models.UserData;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import customfonts.MyEditText;


public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog mSignInDialog;
    private MyEditText mEmailField, mPasswordField;
    private Boolean parseLoginEmailAsUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInDialog = new ProgressDialog(this);
        mSignInDialog.setMessage(getString(R.string.progress_login));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // mToolbar.setClickable(true);
        //mToolbar.setTitleTextColor(Color.WHITE);
        // mToolbar.setTitle("Sign In");

        mEmailField = (MyEditText) findViewById(R.id.signIn_email);
        mPasswordField = (MyEditText) findViewById(R.id.signIn_password);

    }

    public void onSignupClickHandler(View view) {
        Intent sinupIntent = new Intent(this, RegisterActivitiy.class);
        startActivity(sinupIntent);
    }


    private boolean fieldsValid(String email, String password) {
        Validation validation = Validation.getInstance();

        if (validation.isNetworkAvailable(this)) {
            if (validation.isNullOrEmpty(email, password)) {
                validation.showToast(this, "Empty fields");
                return false;
            } else if (!validation.validateEmail(email)) {
                validation.showToast(this, "Invalid Email Address");
                return false;
            }
        } else {
            validation.showToast(this, "NO Network Connection!");
        }


        return true;
    }

    public boolean isParseLoginEmailAsUsername() {
        if (parseLoginEmailAsUsername != null) {
            return parseLoginEmailAsUsername;
        } else {
            return false;
        }
    }

    public void onclickSignInHandler(View view) {
        boolean validationError = false;
        Validation validation = Validation.getInstance();

        if (isParseLoginEmailAsUsername()) {
            mEmailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        final String username = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isValidKey = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                boolean isValidAction = actionId == EditorInfo.IME_ACTION_DONE;

                if (isValidKey || isValidAction) {
                    // do login request
                }
                return false;
            }
        });

        //check for input validation here
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            if (isParseLoginEmailAsUsername()) {
                validationError = true;
                validationErrorMessage.append(getString(R.string.error_blank_username));

            }
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));

            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            //Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            showAlertDialog(LoginActivity.this, "Opps!", validationErrorMessage.toString(), false);
            return;
        }

        if (validation.isNetworkAvailable(this)) {
            showProgressDialog();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                boolean isUserVerified, isEmailVerified, complete;
                Boolean isReceive = true;

                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    hideProgressDialog();
                    if (parseUser != null) {
                        Log.d("OnClick", "User");

                        UserData user = new UserData();
                        SharedPreferences receive = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                        Boolean userIsLogInBefore = receive.contains(username);

                        if (userIsLogInBefore) {
                            isReceive = receive.getBoolean(username, true);
                        } else {
                            SharedPreferences.Editor edit = receive.edit();
                            edit.putBoolean(username, isReceive);
                            edit.commit();
                        }

                        user.setInstallation(ParseInstallation.getCurrentInstallation());
                        ParseInstallation.getCurrentInstallation().put("user", parseUser);
                        ParseInstallation.getCurrentInstallation().put("isReceive", isReceive);
                        ParseInstallation.getCurrentInstallation().saveInBackground();
                        parseUser.saveInBackground();

                        isEmailVerified = user.getBoolean("emailVerified");
                        isUserVerified = user.getBoolean("Is_Verified");
                        complete = user.getBoolean("isComplete");

                        if (user.getBoolean("emailVerified") == true) {
                            user.setIsVerified(true);
                        }


                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Colg");
                        query.whereNotEqualTo("colgId", parseUser.get("colgId"));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Login " +
                                                    "Successfully!"
                                            , Toast.LENGTH_LONG).show();

                                    Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mainIntent.putExtra("username", username);
                                    startActivity(mainIntent);
                                }
                            }
                        });


                        //finish();

                        // login with email
                    } else if (username.contains("@")) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("email", username);
                        query.getFirstInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser object, ParseException e) {
                                if (object != null) {
                                    ParseUser.logInInBackground(object.getString("username"), password, new LogInCallback() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {
                                            //dialog.dismiss();
                                            if (parseUser != null) {
                                                Log.d("OnClick", "User");

                                                Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                                                ParseInstallation.getCurrentInstallation().put("user", parseUser);
                                                ParseInstallation.getCurrentInstallation().saveInBackground();
                                                ((UserData) parseUser).setInstallation(ParseInstallation.getCurrentInstallation());
                                                parseUser.saveInBackground();
                                                startActivity(mainIntent);
                                                finish();
                                            }

                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        switch (e.getCode()) {
                            case ParseException.OBJECT_NOT_FOUND:
                                showAlertDialog(LoginActivity.this, "Opps!", e.getMessage(), false);
                                break;
                            default:
                                Toast.makeText(LoginActivity.this
                                        , "Login failed! Try again", Toast.LENGTH_LONG).show();
                        }
                        Log.d("OnClick", e.toString());
                    }
                }
            });

        } else {
            validation.showToast(this, "NO network connection");
        }

    }

    private void showProgressDialog() {
        mSignInDialog.show();
    }

    private void hideProgressDialog() {
        mSignInDialog.dismiss();
    }

    //Alert dialog box: success / failed
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.ic_success : R.drawable.ic_failed);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
