package com.ezzat.spofi.Control;

import android.util.Log;

import com.ezzat.spofi.Model.Report;
import com.ezzat.spofi.Model.User;
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
}
