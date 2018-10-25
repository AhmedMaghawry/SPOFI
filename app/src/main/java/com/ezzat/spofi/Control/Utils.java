package com.ezzat.spofi.Control;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ezzat.spofi.Model.Location;
import com.ezzat.spofi.R;
import com.ezzat.spofi.View.MapsActivity;
import com.ezzat.spofi.View.ProfileActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Utils {

    public static SweetAlertDialog pDialog;
    public final static String TAG = "Utils";

    public static void hideProgressCuteDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            try {
                pDialog.dismiss();
                pDialog = null;
            } catch (Exception e) {
                pDialog = null;
                Log.e(Constants.TAG, "Utils:: hideProgressDialog: ", e);
            }
        }
    }

    public static void showProgressCuteDialog(Activity context) {
        hideProgressCuteDialog();
        if (context != null) {
            try {
                pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading ...");
                pDialog.setCancelable(false);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.show();
            } catch (Exception e) {
                Log.e(Constants.TAG, "Utils:: showProgressDialog: ", e);
            }
        }
    }

    public static void showProgressDialogSuccess(Activity context) {
        hideProgressCuteDialog();
        if (context != null) {
            try {
                pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                pDialog.setTitleText("Success");
                pDialog.setContentText("The Image/Video is uploaded successfully");
                pDialog.setConfirmText("OK");
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                pDialog.show();
            } catch (Exception e) {
                Log.e(Constants.TAG, "Utils:: showProgressDialog: ", e);
            }
        }
    }

    public static void showProgressDialogFaild(Activity context) {
        hideProgressCuteDialog();
        if (context != null) {
            try {
                pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitleText("Failed");
                pDialog.setContentText("The Image/Video is failed to upload");
                pDialog.setCancelText("OK");
                pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                pDialog.show();
            } catch (Exception e) {
                Log.e(Constants.TAG, "Utils:: showProgressDialog: ", e);
            }
        }
    }

    public static boolean isInternetConnected(Context ctx) {
        if (ctx != null) {
            ConnectivityManager connectivityMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityMgr != null) {
                NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void launchActivity(Context context, Class classToGo, Bundle bundle) {
        Intent intent = new Intent(context, classToGo);
        intent.putExtra("send", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void showDialog(Context context,String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void sendNotification(Context context) {
        Intent intent = new Intent(context,MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_logosvg);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logosvg));
        builder.setContentTitle("Fire Alert");
        builder.setContentText("There is a Fire sport near you!");
        builder.setSubText("Tap to view Location about The Fire");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public static Location getLocation(Context context, Location defaultLoc) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            android.location.Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            android.location.Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            double latti = 0, longi = 0;
            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();
            } else if (location1 != null) {
                latti = location1.getLatitude();
                longi = location1.getLongitude();
            } else if (location2 != null) {
                latti = location2.getLatitude();
                longi = location2.getLongitude();
            } else {
                Toast.makeText(context, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
            }

            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(latti, longi, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Location loc;

            if (addresses.size() > 0) {
                loc = new Location(addresses.get(0).getCountryName(), addresses.get(0).getLocality(), (String.valueOf(addresses.get(0).getLongitude())), (String.valueOf(addresses.get(0).getLatitude())));
            }
            else {
                loc = defaultLoc;
            }

            return loc;
        }
        return defaultLoc;
    }
}