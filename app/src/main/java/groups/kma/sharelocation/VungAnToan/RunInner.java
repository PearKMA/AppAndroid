package groups.kma.sharelocation.VungAnToan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import groups.kma.sharelocation.NguoiThan.NhomNguoiThanMapActivity;
import groups.kma.sharelocation.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RunInner extends BroadcastReceiver implements LocationListener {
    private Double myLat,myLong;
    private LocationManager locationManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String UserId,gettedLocation="";
    private ArrayList<String> dsFriendId;
    private String names;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager=
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo()!=null)
        {
            dsFriendId=new ArrayList<>();
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            UserId=mAuth.getCurrentUser().getUid();
            getLocation(context);
            getInfo(context);
        }

    }

    private void getInfo(final Context context) {
        mDatabase.child("AlertArea").child(UserId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Double distance=dataSnapshot.child("Distance").getValue(Double.class);
                            names="";
                            for(DataSnapshot friendKey : dataSnapshot.child("Friends").getChildren()) {
                                Double lat=friendKey.child("lat").getValue(Double.class);
                                Double lon=friendKey.child("lon").getValue(Double.class);
                                String user=friendKey.child("name").getValue(String.class);
                                double khoangCach = distanceBetween2Points(lat,lon, myLat, myLong);
                                if (khoangCach > distance) {
                                    names+=user+" ";
                                }
                            }
                            xuLyNotification(context,names);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private double distanceBetween2Points(double la1, double lo1,
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
    private void getLocation(Context context) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        /*Sử dụng lớp Criteria để yêu cầu nhà cung cấp xử lý chính xác những số liệu có sẵn như:
        vĩ độ và kinh độ, tốc độ, độ cao, chi phí và yêu về cầu năng lương điện. */
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            myLat=location.getLatitude();
            myLong=location.getLongitude();
            gettedLocation="OK";
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                0, this);
    }

    private void xuLyNotification(Context context,String userName) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, "Alert_Area")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Phát hiện vượt khoảng cách an toàn")
                .setContentText("User: "+userName+" .Nhấn để xem chi tiết!")
                .setAutoCancel(true);

        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder.setSound(uri);
        Intent resultIntent = new Intent(context, NhomNguoiThanMapActivity.class);
        PendingIntent resultPending = PendingIntent.getActivity(context,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPending);

        /*Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder.setSound(uri);*/
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(113, nBuilder.build());
    }

    @Override
    public void onLocationChanged(Location location) {
        myLat=location.getLatitude();
        myLong=location.getLongitude();
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
