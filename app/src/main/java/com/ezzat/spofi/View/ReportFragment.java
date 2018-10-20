package com.ezzat.spofi.View;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ezzat.spofi.Model.User;
import com.ezzat.spofi.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    private User currentUser;
    private Button sendReport;
    private ImageView cam, vid;
    private LinearLayout report_ll;
    private LinearLayout next_ll;

    public ReportFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ReportFragment(User user) {
        currentUser = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        sendReport = view.findViewById(R.id.report);
        report_ll = view.findViewById(R.id.report_ll);
        next_ll = view.findViewById(R.id.next_ll);
        cam = view.findViewById(R.id.cam);
        vid = view.findViewById(R.id.vid);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_left);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        report_ll.setVisibility(View.GONE);
                        next_ll.setVisibility(View.VISIBLE);
                        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                        next_ll.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                report_ll.startAnimation(animation);
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(takePictureIntent, 0);
            }
        });

        vid.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30);
                getActivity().startActivityForResult(takeVideoIntent, 1);
//                Intent intent=new Intent(MainActivity.this,profile.class);
//                startActivity(intent);

//                AlertDialog.Builder builder;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                } else {
//                    builder = new AlertDialog.Builder(MainActivity.this);
//                }
//                builder.setTitle("Submission Photo")
//                        .setMessage("Are you sure you want to submit this photo?")
//                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // continue with delete
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        })
//                        .setIcon(android.R.drawable.btn_star)
//                        .show();
            }
        });
        return view;
    }

}
