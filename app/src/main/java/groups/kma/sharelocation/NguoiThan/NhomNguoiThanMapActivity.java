package groups.kma.sharelocation.NguoiThan;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.R;

public class NhomNguoiThanMapActivity extends FragmentActivity implements OnMapReadyCallback {
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
        areaSpinner = findViewById(R.id.spinner);
        mRecyclerview = findViewById(R.id.recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        //mAdapter = new CircleAdapter();

        //
        rootRef.child("Users").child(uID).child("GroupLocationKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final List<SpinnerGroup> areas = new ArrayList<SpinnerGroup>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("NameGroup").getValue(String.class);
                    String areaKey = areaSnapshot.getKey();
                    SpinnerGroup x = new SpinnerGroup(areaName, areaKey);
//                    x.setKey(areaKey);
//                    x.setName(areaName);
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
                        // action
                        Toast.makeText(NhomNguoiThanMapActivity.this, xx.getName() + " " + xx.getKey(), Toast.LENGTH_SHORT).show();
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
                                        final String username = dataSnapshot.child("userName").getValue().toString();
                                        String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();
                                        viewHolder.setUsername(username);
                                        viewHolder.setThumb(thumbimage);
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                moveMove(30,105);

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

    public static class MembersGroupViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MembersGroupViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        // ty nua sua
        public void setUsername(String name) {
            TextView usname = mView.findViewById(R.id.textCirle);
            usname.setText(name);
        }

        public void setThumb(String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.imgCircle);
            Picasso.get().load(thumbimage).placeholder(R.drawable.acc_box).into(thumb_image);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void moveMove(long lat,long Lng){
        LatLng sydney = new LatLng(lat, Lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in VietNam"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(
                new LatLng(lat, Lng))
                .title("You are now Here").visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Updated Location"));

        currentLocationMarker.showInfoWindow();
    }
}
