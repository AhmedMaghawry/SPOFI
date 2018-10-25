package com.ezzat.spofi.View;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ezzat.spofi.Control.FirebaseCallback;
import com.ezzat.spofi.Control.FirebaseMethods;
import com.ezzat.spofi.Control.Utils;
import com.ezzat.spofi.Model.Report;
import com.ezzat.spofi.Model.ReportType;
import com.ezzat.spofi.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener{

        private GoogleMap mMap;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        Marker mCurrLocationMarker;
        LocationRequest mLocationRequest;
        double latitude, longitude;
        double end_latitude, end_longitude;
        private ImageButton help, emrg;
        private Marker addedMarker;
        private LinearLayout layoutBottomSheet;
        private BottomSheetBehavior sheetBehavior;
        private ImageView imageView;
        private TextView comment;

        ArrayList<Double> latitudes,longitudes;
        ArrayList<Report> reports;
        double avrgLat = 0, avrgLong = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            ButterKnife.bind(this);
            layoutBottomSheet = findViewById(R.id.bottom_sheet);
            sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
            sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            //btnBottomSheet.setText("Close Sheet");
                        }
                        break;
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            //btnBottomSheet.setText("Expand Sheet");
                        }
                        break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
            help = findViewById(R.id.help);
            emrg = findViewById(R.id.emrg);
            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.launchActivity(MapsActivity.this, HelpActivity.class, null);
                }
            });

            emrg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.launchActivity(MapsActivity.this, EmergActivity.class, null);
                }
            });
            //todo : get it dynamic
            latitudes = new ArrayList<>();
            longitudes = new ArrayList<>();
            reports = new ArrayList<>();
            /*final double distanceComp = 1609.34;
            FirebaseMethods.getAllReports(new FirebaseCallback() {
                @Override
                public void onValueReturned(Object value) {
                    List<Report> reportList = (List<Report>) value;
                    for (Report r : reportList) {
                        double dis = distance(Double.parseDouble(r.getLocation().getLat()), Double.parseDouble(r.getLocation().getLang()),latitude, longitude);
                        Log.i("Rizk", dis + " and comp to " + distanceComp);
                        if (dis <= distanceComp) {
                            latitudes.add(Double.parseDouble(r.getLocation().getLat()));
                            longitudes.add(Double.parseDouble(r.getLocation().getLang()));
                            reports.add(r);
                        }
                    }
                }
            });*/

            /*latitudes.add(31.2381608);
            longitudes.add(29.9712006);*/
            latitudes.add(31.239692);
            longitudes.add(29.9693056);
            latitudes.add(31.2408226);
            longitudes.add(29.9734445);
            latitudes.add(31.2426954);
            longitudes.add(29.9714656);
            /*latitudes.add(31.2372878);
            longitudes.add(29.9672778);*/
            Report report = new Report(" ownerName",1,new com.ezzat.spofi.Model.Location("Egypt", "Alex","1.22","1.22"), " date","hour", "contentUrl", ReportType.Video);
            report.setReportId("-LPDCl8toHAozLUOmr57");
            report.setComment("sadasdasda");

            reports.add(report);
            reports.add(report);
            reports.add(report);
            reports.add(report);
            //reports.add(report);
            //reports.add(report);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            //Check if Google Play Services Available or not
            if (!CheckGooglePlayServices()) {
                Log.d("onCreate", "Finishing test case since Google Play Services are not available");
                finish();
            }
            else {
                Log.d("onCreate","Google Play Services available.");
            }

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);
        }

    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    /*public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }*/

    public static double distance(double lat1, double lng1,
                                  double lat2, double lng2){
        double a = (lat1-lat2)*distPerLat(lat1);
        double b = (lng1-lng2)*distPerLng(lat1);
        return Math.sqrt(a*a+b*b);
    }

    private static double distPerLng(double lat){
        return 0.0003121092*Math.pow(lat, 4)
                +0.0101182384*Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }

        private void drawPlaces() {
            for(int i = 0; i<latitudes.size();i++)
                setMarker(latitudes.get(i),longitudes.get(i),reports.get(i));
        }


        private boolean CheckGooglePlayServices() {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int result = googleAPI.isGooglePlayServicesAvailable(this);
            if(result != ConnectionResult.SUCCESS) {
                if(googleAPI.isUserResolvableError(result)) {
                    googleAPI.getErrorDialog(this, result,
                            0).show();
                }
                return false;
            }
            return true;
        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            //Initialize Google Play Services
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }

            mMap.setOnMarkerDragListener(this);
            mMap.setOnMarkerClickListener(this);
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        }


        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }


        @Override
        public void onConnected(@Nullable Bundle bundle) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        public boolean checkLocationPermission(){
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Asking user if explanation is needed
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //Prompt the user once explanation has been shown
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d("onLocationChanged", "entered");

            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            latitude = location.getLatitude();
            longitude = location.getLongitude();


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.draggable(true);
            markerOptions.title("Current Position");
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.red_dot);
            markerOptions.icon(icon);
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


//        Toast.makeText(GetNearbyPlaces.this,"Your Current Location", Toast.LENGTH_LONG).show();
//        Toast.makeText(GetNearbyPlaces.this,Double.toString( latitude),Toast.LENGTH_SHORT).show();
            Log.e("Latituede ",Double.toHexString(latitude));
            Log.e("Langtude ",Double.toHexString(longitude));
            //stop location updates
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                Log.d("onLocationChanged", "Removing Location Updates");
            }

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(900.0).fillColor(R.color.red).strokeColor(R.color.colorPrimary).strokeWidth(3f);
        mMap.addCircle(circleOptions);
            drawPlaces();
        }


    @Override
        public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted. Do the
                        // contacts-related task you need to do.
                        if (ContextCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                            if (mGoogleApiClient == null) {
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }

                    } else {

                        // Permission denied, Disable the functionality that depends on this permission.
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                // other 'case' lines to check for other permissions this app might request.
                // You can add here other case statements according to your requirement.
            }
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.setDraggable(true);
                FirebaseMethods.getReport(marker.getTitle(), new FirebaseCallback() {
                       @Override
                       public void onValueReturned(Object value) {
                          Report re = (Report) value;
                           imageView = findViewById(R.id.report_img);
                           comment = findViewById(R.id.report_com);
                          comment.setText(re.getComment());
                          Glide.with(getApplicationContext()).load(re.getContentUrl())
                                   .thumbnail(0.5f)
                                   .into(imageView);
                          toggleBottomSheet();
                       }
                });
            return false;
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            end_latitude = marker.getPosition().latitude;
            end_longitude =  marker.getPosition().longitude;

            Log.d("end_lat",""+end_latitude);
            Log.d("end_lng",""+end_longitude);
        }



        private void setMarker(double lat ,double lng ,Report report ){
            MarkerOptions markerOptions = new MarkerOptions();
            Log.d("onPostExecute","Entered into showing locations");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(report.getReportId());
            markerOptions.icon(bitmapDescriptorFromVector(this,R.drawable.fire_marker));
            addedMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.fire_marker);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
