package com.ezzat.spofi.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ezzat.spofi.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pkharche on 13/04/18.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context mContext = null;
    protected Activity mActivity = null;
    protected CoordinatorLayout mCoordinatorLayout = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
    }

    protected void checkLocationServices() {
        locationServicesEnabled();
    }

    protected void showNetworkError() {
        showSnackBar("No Internet Connection");
    }

    protected void showSnackBar(String text) {
        showSnackBar(text, true);
    }

    protected void showSnackBar(String text, boolean isError) {
        if(mCoordinatorLayout != null) {
            Snackbar snackbar = Snackbar.make(
                    mCoordinatorLayout,
                    text,
                    Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();

            if(isError) {
                snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.col2));
            } else {
                snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.col2));
            }

            snackbar.show();

        } else {
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
        }
    }

    protected void launchActivity(Class activityClass) {
        Intent i = new Intent(mContext, activityClass);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInternetConnected();
    }
/*
    protected void logOutFirebaseUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                       SharedValues.resetAllValues(mContext);
                    }
                });
    }
*/
    protected boolean isInternetConnected() {
        if (mContext != null) {
            ConnectivityManager connectivityMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityMgr != null) {
                NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
/*
    protected void updateFirebase(String path, Object value) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(path);
        ref.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                onFirebaseUpdated();
            }
        });
    }

    protected void onFirebaseUpdated() {
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // Check if Location Settings are enabled to proceed
            locationServicesEnabled();

        } else {
            // Handle Enable Location Services request denied error
            showSnackBar("Please Enable The Location Service");
        }
    }

    protected void locationServicesEnabled() { //Override this method when location ON
    }

    protected String getFormattedTime(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat("hh:mm:a");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

}
