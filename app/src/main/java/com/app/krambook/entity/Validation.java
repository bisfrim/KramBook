package com.app.krambook.entity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private Pattern pattern;
    private Matcher matcher;
    private  static Validation validation;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String SEP = "(?:\\-|\\s)";
    private static final String GROUP = "(\\d{1,5})";
    private static final String PUBLISHER = "(\\d{1,7})";
    private static final String TITLE = "(\\d{1,6})";


    static final String ISBN10_REGEX     =
            "^(?:(\\d{9}[0-9X])|(?:" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9X])))$";


    static final String ISBN13_REGEX     =
            "^(978|979)(?:(\\d{10})|(?:" + SEP + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9])))$";

    String PHONE_PATTERN = "^([0-9]{3})[-]([0-9]{3})[-]([0-9]{4})$";


    Validation(){}

    public static Validation getInstance(){
        if(validation == null){
            validation = new Validation();
        }
        return validation;
    }

    public boolean validateEmail(final String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public boolean validateIsbn(final String isbn) {

        pattern = Pattern.compile(ISBN10_REGEX);
        matcher = pattern.matcher(isbn);
        boolean result = matcher.matches();

        if(result == false){
            pattern = Pattern.compile(ISBN13_REGEX);
            matcher = pattern.matcher(isbn);
            result = matcher.matches();
        }
        return result;
    }

    public boolean validatePhoneNumber(final String phoneNumber) {
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public boolean isValidMobile(String mobile) {
        Pattern p = Pattern.compile("^[789]\\d{9,9}$");
        if (mobile == null) {
            return false;
        } else {
            Matcher m = p.matcher(mobile);
            return m.matches();
        }
    }

    public static boolean validateMobileNumber(String... testObj) {
        for (int i = 0; i < testObj.length; i++) {
            if (testObj[i].length() != 10)
                return false;
        }
        return true;
    }


    // Checks if string is null or empty.
    public static boolean isNullOrEmpty(String string) {
        if (string == null) {
            return true;
        }

        if (string.trim().length() == 0) {
            return true;
        }

        return false;
    }

    // Checks if any strings in a series are null or empty
    public static boolean isNullOrEmpty(String... strings) {
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];

            if (isNullOrEmpty(string)) {
                return true;
            }
        }

        return false;
    }

    // Checks if two strings match one another
    public static boolean matches(String stringOne, String stringTwo) {
        return stringOne.equals(stringTwo);
    }


    public static void showToast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_LONG).show();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }


}