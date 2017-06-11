package com.app.krambook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.entity.Validation;
import com.app.krambook.models.UserData;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import customfonts.MyEditText;
import customfonts.MyTextView;

public class ResertPasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private MyEditText changePassword, changeRePassword;
    private UserData mCurrentUser;
    private ProgressDialog mSavePasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resert_password);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSavePasswordDialog = new ProgressDialog(this);
        mSavePasswordDialog.setMessage(getString(R.string.saving_info));

        changePassword = (MyEditText) findViewById(R.id.change_pass);
        changeRePassword = (MyEditText) findViewById(R.id.change_reenter_pass);

        mCurrentUser = (UserData) UserData.getCurrentUser();
    }


    private boolean fieldsValid(String password, String passwordConfirmation) {
        Validation validation = Validation.getInstance();

        if (validation.isNetworkAvailable(this)) {
            if (validation.isNullOrEmpty(password, passwordConfirmation)) {
                validation.showToast(this, "All fields required");
                return false;
            } else if (!validation.matches(password, passwordConfirmation)) {
                validation.showToast(this, "Password does not match");
                return false;
            }
        } else {
            validation.showToast(this, "NO Network Connection!");
        }

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
            //new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPasswordClickHandler(View view) {
        final String passwordChangeField = changePassword.getText().toString().trim();
        final String passwordChangeConfirmField = changeRePassword.getText().toString().trim();
        if (fieldsValid(passwordChangeField, passwordChangeConfirmField)) {
            showProgressDialog();
            mCurrentUser.setPassword(passwordChangeField);
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    hideProgressDialog();
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Password Updated, You will be logged out", Toast.LENGTH_LONG).show();
                        ParseUser.getCurrentUser().logOut();
                        Intent resetPassIntent = new Intent(ResertPasswordActivity.this, UserDispatchActivity.class);
                        resetPassIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(resetPassIntent);
                        ResertPasswordActivity.this.finish();
                    } else {
                        switch (e.getCode()) {
                            default:
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    private void showProgressDialog() {
        mSavePasswordDialog.show();
    }

    private void hideProgressDialog() {
        mSavePasswordDialog.dismiss();
    }
}
