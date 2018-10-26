package groups.kma.sharelocation.VungAnToan;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import groups.kma.sharelocation.R;

public class VungAnToanActivity extends Fragment implements LocationListener{
    private LocationManager locationManager;
    private Location location;
    private TextView txtViTri;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String UserId;
    private ArrayList<String> dsFriendId;
    private Double myLat,myLong;
    private int distance=2000;
    int notificationId;
    private ArrayList<SharedLocation> list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vung_an_toan, container, false);
        txtViTri = view.<TextView>findViewById(R.id.txtVitri);
        getLocation();
        dsFriendId=new ArrayList<>();
        list=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserId=mAuth.getCurrentUser().getUid();
        getFriendId();

        return view;
    }

    private void hienDS() {
        Log.e("Ktra",""+list.size());
    }


    private void getDistance() {
        for (String key:dsFriendId) {
            mDatabase.child("LocationUsers").child(key).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot childSbapshot : dataSnapshot.getChildren()) {
                                        String date=String.valueOf(childSbapshot.child("date").getValue());
                                        String lat=String.valueOf(childSbapshot.child("latitude").getValue());
                                        String lon=String.valueOf(childSbapshot.child("longtitude").getValue());
                                        String name=String.valueOf(childSbapshot.child("name").getValue());
                                        String time=String.valueOf(childSbapshot.child("time").getValue());
                                        SharedLocation sl=new SharedLocation(date,
                                                Double.valueOf(lat),
                                                Double.valueOf(lon),
                                                name,time);
                                        taoDs(sl);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
        }
    }

    private void taoDs(SharedLocation sl) {
        list.add(sl);

    }

    private void getLocation() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        /*Sử dụng lớp Criteria để yêu cầu nhà cung cấp xử lý chính xác những số liệu có sẵn như:
        vĩ độ và kinh độ, tốc độ, độ cao, chi phí và yêu về cầu năng lương điện. */
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            myLat=location.getLatitude();
            myLong=location.getLongitude();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);
    }
    private void getFriendId(){
            mDatabase.child("Friends").child(UserId).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildren()!=null)
                            {
                                for(DataSnapshot friendKey : dataSnapshot.getChildren()) {
                                    dsFriendId.add(friendKey.getKey());
                                }

                                getDistance();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }
    @Override
    public void onLocationChanged(Location location) {
        myLat=location.getLatitude();
        myLong=location.getLongitude();
    }
    public static double distanceBetween2Points(double la1, double lo1,
                                                double la2, double lo2) {
        int R = 6378137;
        double dLat = (la2 - la1) * (Math.PI / 180);
        double dLon = (lo2 - lo1) * (Math.PI / 180);
        double la1ToRad = la1 * (Math.PI / 180);
        double la2ToRad = la2 * (Math.PI / 180);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
                * Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; //result = meter
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
}
