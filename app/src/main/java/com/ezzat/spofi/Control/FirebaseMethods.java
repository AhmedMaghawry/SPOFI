package com.ezzat.spofi.Control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toolbar;

import com.ezzat.spofi.Model.Report;
import com.ezzat.spofi.Model.ReportState;
import com.ezzat.spofi.Model.User;
import com.ezzat.spofi.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseMethods {

    public static void getUser(String uid, final FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uid);
        Log.i(Constants.TAG, myRef.getKey());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChildren()) {
                    Log.i(Constants.TAG, "Herrre");
                    callback.onValueReturned(dataSnapshot.getValue(User.class));
                    //Log.i(Constants.TAG, currentUser.getEmail());
                } else {
                    callback.onValueReturned(null);
                    Log.i(Constants.TAG, "Errrrr");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i(Constants.TAG, error.getMessage());
            }
        });
    }

    public static void addUser(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(user.getId()).setValue(user);
    }

    public static void getReport(String rid, final FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reports").child(rid);
        Log.i(Constants.TAG, myRef.getKey());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChildren()) {
                    Log.i(Constants.TAG, "Herrre");
                    callback.onValueReturned(dataSnapshot.getValue(Report.class));
                    //Log.i(Constants.TAG, currentUser.getEmail());
                } else {
                    Log.i(Constants.TAG, "Errrrr");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i(Constants.TAG, error.getMessage());
            }
        });
    }

    public static void getReports(final List<String> rids, final FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reports");
        Log.i(Constants.TAG, myRef.getKey());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Report> reports = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.hasChildren() && rids.contains(d.getKey())) {
                        reports.add(d.getValue(Report.class));
                    }
                }
                callback.onValueReturned(reports);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i(Constants.TAG, error.getMessage());
            }
        });
    }

    public static void getAllReports(final FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reports");
        Log.i(Constants.TAG, myRef.getKey());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("dodoEzz", "new Data");
                List<Report> reports = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                        reports.add(d.getValue(Report.class));
                }
                callback.onValueReturned(reports);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i(Constants.TAG, error.getMessage());
            }
        });
    }

    public static void detectMarkersChange() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reports");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void onReportsChange(final FirebaseCallback reportVerified, final FirebaseCallback reportAdded, final FirebaseCallback reportChanged) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reports");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (reportAdded != null)
                    reportAdded.onValueReturned(dataSnapshot);
            }

            @SuppressLint("NewApi")
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Report report = dataSnapshot.getValue(Report.class);
                if (report.getState() == ReportState.Verified && reportInRange(report)) {
                    if (reportVerified != null)
                        reportVerified.onValueReturned(report);
                } else {
                    if (reportChanged != null)
                        reportChanged.onValueReturned(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static boolean reportInRange(Report report) {
        //TODO::Check if it is in range
        return true;
    }

    public static void onUserChange(final String userId, final FirebaseCallback userChanged) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @SuppressLint("NewApi")
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getId().equals(userId))
                    userChanged.onValueReturned(user);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
