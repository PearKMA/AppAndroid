package groups.kma.sharelocation.MapAction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import groups.kma.sharelocation.R;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener,
        PlaceSelectionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static View view;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;


    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    //private ClusterManager<MyItem> mClusterManager;

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private Marker marker;
    private LocationManager locationManager;

    private String currentGroupLocation, currentUserId, currentUserName, currentDate, currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupLocationRef, GroupLocationKeyRef;
    String locationKey;

    Timer timer=null;
    TimerTask timerTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_online);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupLocationRef = FirebaseDatabase.getInstance().getReference().child("GroupsLocation").
                child("Group");
        getUserInfo();
        getAllLocation();
    }

    private void getAllLocation() {
        GroupLocationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    DisplayLocations(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    DisplayLocations(dataSnapshot);
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

    private void getUserInfo() {
        UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("userName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        buildGoogleApiClient();
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.googleMap.setMyLocationEnabled(true);

    }
    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(final Location location) {

            myLocation(location);
    }


    private void DisplayLocations(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        googleMap.clear();
        while (iterator.hasNext())
        {
            String locDate=(String) ((DataSnapshot)iterator.next()).getValue();
            Double locLatitude=(Double) ((DataSnapshot)iterator.next()).getValue();
            Double locLongtitude=(Double) ((DataSnapshot)iterator.next()).getValue();
            String locName=(String) ((DataSnapshot)iterator.next()).getValue();
            String locTime=(String) ((DataSnapshot)iterator.next()).getValue();

            if (locName==currentUserName){
                LatLng latLng = new LatLng(locLatitude, locLongtitude);
                Marker info = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("You're here!")
                        .snippet(locDate+" at "+locTime)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }else {
                ShowAllLocation(locDate,locLatitude,locLongtitude,locName,locTime);
            }
        }
    }

    private void ShowAllLocation(String locDate, Double locLatitude, Double locLongtitude,
                                 String locName, String locTime)
    {
        LatLng latLng = new LatLng(locLatitude, locLongtitude);
        Marker info = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(locName)
                .snippet(locDate+" at "+locTime));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
            locationManager = (LocationManager)MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
            guiViTri(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);

    }

    private void myLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    private void guiViTri(final Location location) {
        timerTask=new TimerTask() {
            @Override
            public void run() {
                double latitude = location.getLatitude();
                double longtitude = location.getLongitude();

                locationKey=GroupLocationRef.push().getKey();
                Calendar ccalForDate=Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MM/dd");
                currentDate=currentDateFormat.format(ccalForDate.getTime());
                Calendar ccalForDTime=Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                currentTime=currentTimeFormat.format(ccalForDTime.getTime());

                HashMap<String,Object> groupLocationKey=new HashMap<>();
                GroupLocationRef.updateChildren(groupLocationKey);

                GroupLocationKeyRef=GroupLocationRef.child(locationKey);

                HashMap<String,Object> locationInfoMap=new HashMap<>();
                locationInfoMap.put("name",currentUserName);
                locationInfoMap.put("latitude",latitude);
                locationInfoMap.put("longtitude",longtitude);
                locationInfoMap.put("date",currentDate);
                locationInfoMap.put("time",currentTime);

                GroupLocationKeyRef.updateChildren(locationInfoMap);
            }
        };
        timer=new Timer();
        timer.schedule(timerTask,0,3*1000*60);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {

    }
}
