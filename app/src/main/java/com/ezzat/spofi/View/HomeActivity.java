package com.ezzat.spofi.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.ezzat.spofi.Control.Constants;
import com.ezzat.spofi.Control.FirebaseCallback;
import com.ezzat.spofi.Control.FirebaseMethods;
import com.ezzat.spofi.Control.Utils;
import com.ezzat.spofi.Model.Report;
import com.ezzat.spofi.Model.ReportState;
import com.ezzat.spofi.Model.ReportType;
import com.ezzat.spofi.Model.User;
import com.ezzat.spofi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private HomeActivity self;
    private User currentUser;
    private CircleImageView profile;
    private TextView username, points;
    private NumberProgressBar prog;
    private AlertDialog dialog;
    private String popupMessage = "No notifications now";
    private ReportFragment re;
    private HistoryFragment hi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isInternetConnected()) {
            self = this;
            Utils.showProgressDialog(this, false);
            FirebaseMethods.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallback() {
                @Override
                public void onValueReturned(Object value) {
                    currentUser = (User) value;
                    setContentView(R.layout.activity_home);
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    fragmentManager = getSupportFragmentManager();
                    re = new ReportFragment(currentUser);
                    hi = new HistoryFragment(currentUser);
                    profile = findViewById(R.id.profile);
                    username = findViewById(R.id.username);
                    points = findViewById(R.id.points);
                    prog = findViewById(R.id.prog);

                    viewPager = (ViewPager) findViewById(R.id.viewpager);
                    Utils.hideProgressDialog();
                    setupViewPager(viewPager, currentUser);
                    setupViews();
                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);

                    initMenuFragment();
                }
            });

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Reports");
            Log.i(Constants.TAG, myRef.getKey());
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Report report = dataSnapshot.getValue(Report.class);
                    if (report.getState() == ReportState.Verified) {
                        sendNotification();
                        popupMessage = "New Fire spot Detected";
                        //Todo:Change the nof icon
                        toolbar.getMenu().getItem(0).setIcon(R.drawable.notf_red);
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        } else {
            showNetworkError();
        }
    }

    private void setupViews() {
        username.setText(currentUser.getName());
        points.setText(currentUser.getPoints()+"");
        prog.setProgress(currentUser.getRate());
        if (!currentUser.getPhotoUrl().equals("")) {
            Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl())
                    .thumbnail(0.5f)
                    .into(profile);
        }
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setBgResource(R.drawable.menu_bg);
        close.setResource(R.drawable.close);

        MenuObject send = new MenuObject("Profile");
        send.setBgResource(R.drawable.menu_bg);
        send.setResource(R.drawable.profile);

        MenuObject help = new MenuObject("Help");
        help.setBgResource(R.drawable.menu_bg);
        help.setResource(R.drawable.help);

        MenuObject about = new MenuObject("About");
        about.setBgResource(R.drawable.menu_bg);
        about.setResource(R.drawable.about);

        MenuObject like = new MenuObject("Log Out");
        like.setBgResource(R.drawable.menu_bg);
        like.setResource(R.drawable.logout);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(help);
        menuObjects.add(about);
        menuObjects.add(like);
        return menuObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
            case R.id.not:
                item.setIcon(R.drawable.notf_norm);
                PopupMenu popupMenu = new PopupMenu(this, self.findViewById(R.id.not));
                /*popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });*/
                popupMenu.getMenu().add(popupMessage);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Utils.launchActivity(HomeActivity.this, MapsActivity.class, null);
                        return true;
                    }
                });
                popupMenu.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(View view, int i) {
        if (i == 1) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", currentUser);
            Utils.launchActivity(this, ProfileActivity.class, bundle);
        } else if (i == 2) {
            Utils.launchActivity(HomeActivity.this, HelpActivity.class, null);
        } else if (i == 3) {
            Utils.launchActivity(HomeActivity.this, AboutActivity.class, null);
        } else if (i == 4) {
            FirebaseAuth.getInstance().signOut();
            launchActivity(LoginActivity.class);
            finish();
        }
    }

    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private void setupViewPager(ViewPager viewPager, User user) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(re, "Report");
        adapter.addFragment(hi, "History");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onMenuItemLongClick(View view, int i) {

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.submission_dialog, null);
        final EditText comment = (EditText) mView.findViewById(R.id.comment);
        final Button submitButton = (Button) mView.findViewById(R.id.submitButton);
        final Button cancelButton = (Button) mView.findViewById(R.id.cancel);
        final VideoView videoView = (VideoView) mView.findViewById(R.id.videoView);
        final ImageView imageView = (ImageView) mView.findViewById(R.id.imageView);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Reports");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo:get The current location instead default
                Utils.showProgressDialog(HomeActivity.this, false);
                StorageReference riversRef = mStorageRef.child("Reports");

                riversRef.putFile(data.getData())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                final Report report = new Report(currentUser.getName(), currentUser.getRate(), currentUser.getLocation(), Calendar.getInstance().getTime().toString(), String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)),downloadUrl.toString(), requestCode == 0 ? ReportType.Photo : ReportType.Video);
                                DatabaseReference pp = myRef.push();
                                report.setReportId(pp.getKey());
                                if (comment.getText() == null || comment.getText().toString().equals("")) {
                                    report.setComment("");
                                } else {
                                    report.setComment(comment.getText().toString());
                                }
                                pp.setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Utils.hideProgressDialog();
                                        currentUser.addReport(report.getReportId());
                                        Utils.showDialog(HomeActivity.this, "Upload State", "The upload is Done Successfully");
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Utils.hideProgressDialog();
                                Utils.showDialog(HomeActivity.this, "Upload State", "The upload Failed");
                            }
                        });
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if(requestCode == 0){
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            imageView.setImageBitmap(imageBitmap);
        }
        else if (requestCode == 1){
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            Uri videoUri = data.getData();
            //set the media controller buttons
            /*MediaController mediaControls = new MediaController(dialog.getContext()) {
                //for not hiding
                @Override
                public void hide() {}

                //for 'back' key action
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        Activity a = (Activity)dialog.getContext();
                        a.finish();
                    }
                    return true;
                }
            };
            if (mediaControls == null) {
                mediaControls = new MediaController(dialog.getContext());
            }
            mediaControls.setAnchorView(videoView);
            mediaControls.setMediaPlayer(videoView);
            videoView.setMediaController(mediaControls);*/
            /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                }
            });*/
            videoView.setVideoURI(videoUri);
            videoView.requestFocus();
            videoView.start();
            /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //if we have a position on savedInstanceState, the video playback should start from here
                    videoView.seekTo(position);
                    if (position == 0) {
                        videoView.start();
                    } else {
                        //if we come from a resumed activity, video playback will be paused
                        videoView.pause();
                    }
                }
            });*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
    }

    public void sendNotification() {

        // BEGIN_INCLUDE(build_action)
        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent = new Intent(this,MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // END_INCLUDE(build_action)

        // BEGIN_INCLUDE (build_notification)
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.ic_logosvg);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        /**
         *Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logosvg));

        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */
        builder.setContentTitle("Fire Alert");
        builder.setContentText("There is a Fire sport near you!");
        builder.setSubText("Tap to view Location about The Fire");

        // END_INCLUDE (build_notification)

        // BEGIN_INCLUDE(send_notification)
        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
        // END_INCLUDE(send_notification)
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
