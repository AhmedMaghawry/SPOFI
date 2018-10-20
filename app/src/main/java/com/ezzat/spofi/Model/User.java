package com.ezzat.spofi.Model;

import android.support.annotation.NonNull;

import com.ezzat.spofi.Control.FirebaseCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String name;
    private String id;
    private String email;
    private Location location;
    private String photoUrl;
    private String gender;
    private String phone;
    private List<String> reportsId;
    private int rate;
    private int points;
    private boolean notify;
    private boolean sms;

    public User(){
        reportsId = new ArrayList<>();
        rate = 0;
        points = 0;
        notify = false;
        sms = false;
        photoUrl = "";
    }

    public User(String name, String email, Location location, String gender, String phone, String id) {
        this.name = name;
        this.email = email;
        this.location = location;
        this.gender = gender;
        this.phone = phone;
        this.id = id;
        reportsId = new ArrayList<>();
        rate = 0;
        points = 0;
        notify = false;
        sms = false;
        photoUrl = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Gender getGender() {
        return Gender.valueOf(gender);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void addReport(String reportId) {
        reportsId.add(reportId);
        updateFirebase();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getReportsId() {
        return reportsId;
    }

    public void setReportsId(List<String> reportsId) {
        this.reportsId = reportsId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void add_to_firebase(final FirebaseCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id);
        myRef.setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onValueReturned(true);
            }
        });
    }

    private void remove_from_firebase(final FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id);
        myRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onValueReturned(true);
            }
        });
    }

    private void updateFirebase() {
        remove_from_firebase(new FirebaseCallback() {
            @Override
            public void onValueReturned(Object value) {
                add_to_firebase(new FirebaseCallback() {
                    @Override
                    public void onValueReturned(Object value) {
                        //notifyAll();
                    }
                });
            }
        });
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }
}
