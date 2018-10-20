package com.ezzat.spofi.Control;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class Utils {

    public static CustomProgressDialog sProgressDialog;
    public final static String TAG = "Utils";

    public static void hideProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            try {
                sProgressDialog.dismiss();
                sProgressDialog = null;
            } catch (Exception e) {
                sProgressDialog = null;
                Log.e(Constants.TAG, "Utils:: hideProgressDialog: ", e);
            }
        }
    }

    public static void showProgressDialog(Activity context, boolean isCancelable) {
        hideProgressDialog();
        if (context != null) {
            try {
                sProgressDialog = new CustomProgressDialog(context);
                sProgressDialog.setCanceledOnTouchOutside(true);
                sProgressDialog.setCancelable(isCancelable);
                sProgressDialog.show();
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
}