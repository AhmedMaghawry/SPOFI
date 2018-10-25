package com.ezzat.spofi.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.ezzat.spofi.Control.Constants;
import com.ezzat.spofi.Control.FirebaseCallback;
import com.ezzat.spofi.Control.FirebaseMethods;
import com.ezzat.spofi.Control.NotificationService;
import com.ezzat.spofi.Control.Utils;
import com.ezzat.spofi.Model.Location;
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
import com.victor.loading.rotate.RotateLoading;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private RotateLoading rotateLoading;
    private LinearLayout loading_l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        self = this;
        mCoordinatorLayout = findViewById(R.id.container);
        rotateLoading = findViewById(R.id.rotateloading);
        rotateLoading.start();
        loading_l = findViewById(R.id.load);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        fragmentManager = getSupportFragmentManager();
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        points = findViewById(R.id.points);
        prog = findViewById(R.id.prog);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (isInternetConnected()) {
            FirebaseMethods.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallback() {
                @Override
                public void onValueReturned(Object value) {
                    if (value == null) {
                        rotateLoading.stop();
                        loading_l.setVisibility(View.GONE);
                        return;
                    }
                    currentUser = (User) value;
                    re = new ReportFragment(currentUser);
                    hi = new HistoryFragment(currentUser);
                    setupViewPager(viewPager, currentUser);
                    setupViews();
                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);
                    initMenuFragment();
                    rotateLoading.stop();
                    loading_l.setVisibility(View.GONE);

                    FirebaseMethods.onReportsChange(new FirebaseCallback() {
                        @Override
                        public void onValueReturned(Object value) {
                            reportVer((Report) value);
                        }
                    }, null, null);

                    FirebaseMethods.onUserChange(currentUser.getId(), new FirebaseCallback() {
                        @Override
                        public void onValueReturned(Object value) {
                            currentUser = (User)value;
                            Toast.makeText(self, "User Changed", Toast.LENGTH_SHORT).show();
                            setupViews();
                        }
                    });
                }
            });
        } else {
            rotateLoading.stop();
            loading_l.setVisibility(View.GONE);
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

        MenuObject emrg = new MenuObject("Emergency");
        emrg.setBgResource(R.drawable.menu_bg);
        emrg.setResource(R.drawable.emerg);

        MenuObject about = new MenuObject("About");
        about.setBgResource(R.drawable.menu_bg);
        about.setResource(R.drawable.about);

        MenuObject like = new MenuObject("Log Out");
        like.setBgResource(R.drawable.menu_bg);
        like.setResource(R.drawable.logout);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(help);
        menuObjects.add(emrg);
        menuObjects.add(about);
        menuObjects.add(like);
        return menuObjects;
    }


    private MenuItem notify;
    private PopupMenu popupMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        notify = menu.getItem(0)
                .setIcon(R.drawable.notif_off);
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
                Context wrapper = new ContextThemeWrapper(this, R.style.pop);
                popupMenu = new PopupMenu(wrapper, self.findViewById(R.id.not));
                popupMenu.getMenu().add(popupMessage);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (!popupMessage.equals("No notifications now")){
                            popupMessage = "No notifications now";
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.notif_off);
                            Utils.launchActivity(HomeActivity.this, MapsActivity.class, null);
                        }
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
            Utils.launchActivity(HomeActivity.this, EmergActivity.class, null);
        } else if (i == 4) {
            Utils.launchActivity(HomeActivity.this, AboutActivity.class, null);
        } else if (i == 5) {
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
                Utils.showProgressCuteDialog(HomeActivity.this);
                StorageReference riversRef = mStorageRef.child("Reports");

                riversRef.putFile(data.getData())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Location loc = Utils.getLocation(self, currentUser.getLocation());
                                final Report report = new Report(currentUser.getName(), currentUser.getRate(), loc, Calendar.getInstance().getTime().toString(), String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)),downloadUrl.toString(), requestCode == 0 ? ReportType.Photo : ReportType.Video);
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
                                        Utils.showProgressDialogSuccess(self);
                                        currentUser.addReport(report.getReportId());
                                        hi.addReport(report);
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Utils.showProgressDialogFaild(self);
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
            videoView.setVideoURI(videoUri);
            videoView.requestFocus();
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
    }

    private void reportVer(Report report) {
        hi.verifyReport(report);
        popupMessage = "New Fire spot Detected";
        toolbar.getMenu().getItem(0).setIcon(R.drawable.notif_on);
        /*try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
