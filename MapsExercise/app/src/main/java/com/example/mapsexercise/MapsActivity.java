package com.example.mapsexercise;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "MapsActivity";

    /** Map switch variables **/
    private GoogleMap mMap;
    public static int currentMapType = -1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int NORMAL_MAP = 0 ;
    private static final int HYBRID_MAP = 1 ;
    private static final int SATELLITE_MAP = 2 ;
    private static final int TERRAIN_MAP = 3 ;

    /** Location query variables **/
    private Boolean mLocationPermissionsGranted = false;
    private static boolean onStart = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static FusedLocationProviderClient mFusedLocationProviderClient;
    private static Timer locationLoggingTimer;

    /** Navigation Drawer **/
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Add menu options
        AddCompoundButton(navigationView, "Enable My Location", new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try{
                    if (isChecked) {
                        Log.v(TAG, "Enabling phone location fearture");
                        mMap.setMyLocationEnabled(true);
                    } else {
                        Log.v(TAG, "Disabling phone location fearture");
                        mMap.setMyLocationEnabled(false);
                    }
                }
                catch(SecurityException e){
                    Log.e(TAG, "Error" + e.getMessage());
                }
            }
        });
        AddCompoundButton(navigationView, "Enable Logging", new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.v(TAG, "Enabling Location logging");
                        locationLoggingTimer = new Timer();
                        TimerTask loggingTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(mLocationPermissionsGranted) {
                                    getDeviceLocation();
                                }
                            }
                        };
                        locationLoggingTimer.schedule(loggingTask, 1000L, 5000L);
                    } else {
                        Log.v(TAG, "Disabling Location logging");
                        locationLoggingTimer.cancel();
                        locationLoggingTimer.purge();
                    }
            }
        });

        onStart = true;
        if( mLocationPermissionsGranted = AppPermissions.getLocationPermission(this.getApplicationContext())){
            initMap();
        }
    }

    public void initMap(){
        if(mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            return;
        }
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0,170,0,0);
        switchMapType(currentMapType);
        attachListener();

        if(mLocationPermissionsGranted){
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    public void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task locationFinder = mFusedLocationProviderClient.getLastLocation();
                locationFinder.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.v(TAG, "found location!");
                            Location currentLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.v("GetDeviceLocation", "Longitude: " + latLng.longitude + ", Latiturde: " + latLng.latitude);
                            if(onStart){
                                Log.v("getDeviceLocation", "This is the initial setup");
                                moveCamera(latLng, DEFAULT_ZOOM);
                                onStart = false;
                            }
                        } else {
                            Log.v(TAG, "getDeviceLocation: location is null");
                            Toast.makeText(MapsActivity.this,"unable to find current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch(SecurityException e) {
            Log.e(TAG,"SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void switchMapType(int mapType){
        if(!checkReady()){
            return;
        }
        currentMapType = (mapType + 1) % 4;
        switch (currentMapType){
            case NORMAL_MAP:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this, R.string.normal_mode, Toast.LENGTH_SHORT).show();
                return;
            case HYBRID_MAP:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(this, R.string.hybrid_mode, Toast.LENGTH_SHORT).show();
                return;
            case SATELLITE_MAP:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this, R.string.satellite_mode, Toast.LENGTH_SHORT).show();
                return;
            case TERRAIN_MAP:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(this, R.string.terrain_mode, Toast.LENGTH_SHORT).show();
                return;
        }
    }

    /**Attaches our map view to to the MapGestureListener**/
    protected void attachListener() {
        View view = this.findViewById(R.id.map);

        view.setClickable(true);
        view.setFocusable(true);

        GestureDetector.SimpleOnGestureListener gestureListener = new MapGestureListener(this);
        final GestureDetector gd = new GestureDetector(this, gestureListener);
        final String LOG_TAG = MapsActivity.class.getSimpleName();

        mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) gestureListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.v(LOG_TAG,"Touch Event");
                gd.onTouchEvent(motionEvent);
                return false;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }


    public void AddCompoundButton(NavigationView navigationView, String name, CompoundButton.OnCheckedChangeListener listener){
        Switch mySwitch = new Switch(this);
        mySwitch.setOnCheckedChangeListener(listener);
        navigationView.getMenu().add(name).setActionView(mySwitch).setCheckable(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}