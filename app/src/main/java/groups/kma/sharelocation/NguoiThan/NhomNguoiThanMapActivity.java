package groups.kma.sharelocation.NguoiThan;

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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.MapAction.MapsActivity;
import groups.kma.sharelocation.R;

public class NhomNguoiThanMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private DatabaseReference rootRef;
    private DatabaseReference mGroupMember;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleMap mMap;
    private Spinner areaSpinner;
    private String uID;
    private SpinnerGroupAdapter sgAdapter;
    private RecyclerView mRecyclerview;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Marker currentLocationMarker;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private DatabaseReference GroupLocationKeyRef;
    private String currentDate, currentTime, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhom_nguoi_than_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uID = mUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupLocationKeyRef = FirebaseDatabase.getInstance().getReference();
        areaSpinner = findViewById(R.id.spinner);
        mRecyclerview = findViewById(R.id.recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        getUserInfo();
        rootRef.child("Users").child(uID).child("GroupLocationKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final List<SpinnerGroup> areas = new ArrayList<SpinnerGroup>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("NameGroup").getValue(String.class);
                    String areaKey = areaSnapshot.getKey();
                    SpinnerGroup x = new SpinnerGroup(areaName, areaKey);
                    areas.add(x);
                }
                sgAdapter = new SpinnerGroupAdapter(NhomNguoiThanMapActivity.this, android.R.layout.simple_spinner_dropdown_item, areas);
                areaSpinner = (Spinner) findViewById(R.id.spinner);
                ArrayAdapter<SpinnerGroup> areasAdapter = new ArrayAdapter<SpinnerGroup>
                        (NhomNguoiThanMapActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                areaSpinner.setAdapter(sgAdapter);
                areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        SpinnerGroup xx = sgAdapter.getItem(i);
                        if (null != currentLocationMarker) {
                            currentLocationMarker.remove();
                        }
                        mMap.clear();
                        // action
                        Toast.makeText(NhomNguoiThanMapActivity.this,"Nhóm "+ xx.getName(), Toast.LENGTH_SHORT).show();
                        GetMemBerGroup(xx.getKey());
                    }

                    // adapter
                    private void GetMemBerGroup(String key) {
                        mGroupMember = FirebaseDatabase.getInstance().getReference().child("GroupLocationCon").child(key).child("Members");
                        FirebaseRecyclerAdapter<MembersGroup, MembersGroupViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MembersGroup, MembersGroupViewHolder>(
                                MembersGroup.class, R.layout.group_location_row, MembersGroupViewHolder.class, mGroupMember
                        ) {
                            @Override
                            protected void populateViewHolder(final MembersGroupViewHolder viewHolder, MembersGroup model, int position) {
                                //viewHolder.setDate(model.getDate());
                                final String list_user_id = getRef(position).getKey();
                                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String keyid = dataSnapshot.getKey().toString();
                                        final String username = dataSnapshot.child("userName").getValue().toString();
                                        String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();
                                        viewHolder.setUsername(username);
                                        viewHolder.setThumb(thumbimage);
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                GroupLocationKeyRef.child("LocationUsers").child(keyid).child("Info").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            String name2 = dataSnapshot.child("name").getValue().toString();
                                                            String date2 = dataSnapshot.child("date").getValue().toString();
                                                            String latitude2 = dataSnapshot.child("latitude").getValue().toString();
                                                            String longtitude2 = dataSnapshot.child("longtitude").getValue().toString();
                                                            String time2 = dataSnapshot.child("time").getValue().toString();
                                                            //marker
                                                            LatLng here = new LatLng(Double.parseDouble(latitude2),Double.parseDouble(longtitude2));
                                                            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in VietNam"));
                                                            mMap.clear();
                                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
                                                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                                            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(here)
                                                                    .title(name2).visible(true)
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                                                    .snippet(name2+" đã ở đây vào lúc "+time2+" ngày "+date2));
                                                            currentLocationMarker.showInfoWindow();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        };
                        mRecyclerview.setAdapter(firebaseRecyclerAdapter);
                    }

                    //end adapter
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getUserInfo() {
        mUserDatabase.child(uID).addValueEventListener(new ValueEventListener() {
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
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(NhomNguoiThanMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(NhomNguoiThanMapActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        //Location locationx = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            Toast.makeText(this, "Location khong null", Toast.LENGTH_SHORT).show();
            if (null != currentLocationMarker) {
                currentLocationMarker.remove();
            }
            mMap.clear();
            onLocationChanged(location);
            myLocation(location);
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10,this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (null != currentLocationMarker) {
            currentLocationMarker.remove();
        }
        mMap.clear();
        myLocation(location);
        GuiViTri(location);
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


    private void GuiViTri(Location location) {
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();

        Calendar ccalForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM");
        currentDate = currentDateFormat.format(ccalForDate.getTime());
        Calendar ccalForDTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(ccalForDTime.getTime());
        String xMap = "LocationUsers/"+uID+"/Info";
        //DatabaseReference xMap = GroupLocationKeyRef.child("LocationUsers").child(uID);

        Map locationInfoMap = new HashMap();
        locationInfoMap.put("name", currentUserName);
        locationInfoMap.put("latitude", latitude);
        locationInfoMap.put("longtitude", longtitude);
        locationInfoMap.put("date", currentDate);
        locationInfoMap.put("time", currentTime);

        Map locationDetails = new HashMap();
        locationDetails.put(xMap,locationInfoMap);
        rootRef.updateChildren(locationDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!=null){
                    Log.d("Chat_Log",databaseError.getMessage().toString());
                }
            }
        });



    }

    private void myLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title("Vị trí hiện tại của bạn").visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Cập nhật vị trí"));
        currentLocationMarker.showInfoWindow();
    }

    private void myLocationMove(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in VietNam"));
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title("Vị trí hiện tại của bạn").visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Cập nhật vị trí"));
        currentLocationMarker.showInfoWindow();
    }

    public static class MembersGroupViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MembersGroupViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String name) {
            TextView usname = mView.findViewById(R.id.textCirle);
            usname.setText(name);
        }

        public void setPermission(String name) {

        }


        public void setThumb(String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.imgCircle);
            Picasso.get().load(thumbimage).placeholder(R.drawable.acc_box).into(thumb_image);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(NhomNguoiThanMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NhomNguoiThanMapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(NhomNguoiThanMapActivity.this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

}
