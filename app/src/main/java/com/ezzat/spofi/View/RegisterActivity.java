package com.ezzat.spofi.View;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ezzat.spofi.Control.Constants;
import com.ezzat.spofi.Control.FirebaseMethods;
import com.ezzat.spofi.Control.PagerAdabter;
import com.ezzat.spofi.Control.Utils;
import com.ezzat.spofi.Model.Gender;
import com.ezzat.spofi.Model.Location;
import com.ezzat.spofi.Model.User;
import com.ezzat.spofi.R;
import com.ezzat.spofi.View.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends BaseActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserRegTask mAuthTask = null;

    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private PagerAdabter adapterViewPager;
    private ViewPager viewPager;
    private Button back,next,finish;
    private String name, email, password, phone;
    private Gender gender;
    private Location location;
    private UserRegTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        viewPager = findViewById(R.id.vpPager);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        finish = findViewById(R.id.finish);
        adapterViewPager = new PagerAdabter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        setupPager();
        //viewPager.setCurrentItem(0);
        //viewPager.beginFakeDrag();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = true;
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        FirstRegFragment fragment = (FirstRegFragment) adapterViewPager.getItem(viewPager.getCurrentItem());
                        val = fragment.attempReg();
                        name = fragment.getFullName();
                        email = fragment.getEmail();
                        password = fragment.getPassword();
                        break;
                    case 1:
                        SecondRegFragment fragment2 = (SecondRegFragment) adapterViewPager.getItem(viewPager.getCurrentItem());
                        val = fragment2.attempReg();
                        location = new Location(fragment2.getCountry(), fragment2.getCity(), fragment2.getLang(), fragment2.getLat());
                        phone = fragment2.getPhone();
                        break;
                    case 2:
                        ThirdRegFragment fragment3 = (ThirdRegFragment) adapterViewPager.getItem(viewPager.getCurrentItem());
                        val = fragment3.attempReg();
                        gender = fragment3.getGender();
                        break;
                }
                if (val)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnected())
                    attemptReg();
                else
                    showNetworkError();
            }
        });
    }

    private void setupPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        back.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        finish.setVisibility(View.GONE);
                        break;
                    case 1:
                        back.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        finish.setVisibility(View.GONE);
                        break;
                    case 2:
                        back.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                        finish.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptReg() {
        if (mAuthTask != null) {
            return;
        }
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UserRegTask(email, password, name, location, phone, gender);
        mAuthTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final String mPhone;
        private final Location location;
        private final Gender mGender;
        private FirebaseAuth mAuth;

        UserRegTask(String email, String password, String name, Location location, String phone, Gender gender) {
            mEmail = email;
            mPassword = password;
            mName = name;
            this.location = location;
            mPhone = phone;
            mGender = gender;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(Constants.TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                User userNew = new User(mName, mEmail, location, gender.toString(), mPhone, user.getUid());
                                FirebaseMethods.addUser(userNew);
                                Toast.makeText(RegisterActivity.this, "Authentication Good.",
                                        Toast.LENGTH_SHORT).show();
                                goToGoodLogin();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(Constants.TAG, "createUserWithEmail:failure", task.getException());
                                Utils.showDialog(RegisterActivity.this, "Registration Failed", "This Email already Exist");
                            }
                            showProgress(false);
                        }
                    });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                //finish();
                //Toast.makeText(getApplicationContext(), "Login Succ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void goToGoodLogin() {
        Utils.launchActivity(RegisterActivity.this, HomeActivity.class, null);
        finish();
    }
}

