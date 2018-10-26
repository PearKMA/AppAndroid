package groups.kma.sharelocation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import groups.kma.sharelocation.Chat.AllUsersActivity;
import groups.kma.sharelocation.Chat.ChatActivity;
import groups.kma.sharelocation.Chat.SettingsActivity;
import groups.kma.sharelocation.LienKetAction.LienKetActivity;
import groups.kma.sharelocation.LoginAction.ActivityDangNhap;
import groups.kma.sharelocation.MapAction.MapsActivity;
import groups.kma.sharelocation.NguoiThan.NhomNguoiThan;
import groups.kma.sharelocation.NguoiThan.NhomNguoiThanMapActivity;
import groups.kma.sharelocation.NguoiThan.QuanLyNhomActivity;
import groups.kma.sharelocation.VungAnToan.BaoDongActivity;
import groups.kma.sharelocation.VungAnToan.VungAnToanActivity;
import groups.kma.sharelocation.model.Users;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener//,OnMapReadyCallback,
        //PlaceSelectionListener, GoogleApiClient.ConnectionCallbacks,
        //GoogleApiClient.OnConnectionFailedListener, LocationListener
   {
    TextView navUsername, navEmail;
    ImageView avatar,imageviewcanhbao;
    private View headerView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference userReference;
    private SharedPreferences preferences;
    private Boolean saveLogin;
    //use for map
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private Marker marker;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        //get username
        navEmail = headerView.findViewById(R.id.tvEmail);
        imageviewcanhbao = headerView.findViewById(R.id.imageViewCanhBao);
        navUsername = headerView.findViewById(R.id.tvUsername);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(mCurrentUser != null){
            String online_user_id = mAuth.getCurrentUser().getUid();
            userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        }
        if (user != null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            String current_uid = mCurrentUser.getUid();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(current_uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    String name = users.getUserName();
                    String email = users.getEmail();
                    navUsername.setText(name);
                    navEmail.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        imageviewcanhbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Gửi cảnh báo!", Toast.LENGTH_SHORT).show();
                GuiCanhBao();
            }
        });

        setTitle("Liên kết người thân");
        LienKetActivity lienKetActivity = new LienKetActivity();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentX, lienKetActivity).commit();
        //addControls();
    }

       private void GuiCanhBao() {
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Gửi báo động");
           builder.setMessage("Bạn có muốn gửi báo động không không?");
           builder.setCancelable(false);
           builder.setPositiveButton("Hủy bỏ", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
               }
           });
           builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
                   sendSmSAlert();
               }
           });
           AlertDialog alertDialog = builder.create();
           alertDialog.show();
       }

       private void sendSmSAlert() {
           final SmsManager smsManager=SmsManager.getDefault();
           Intent intent=new Intent("ACTION_MSG_SEND");
           final PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);
           registerReceiver(new BroadcastReceiver() {
               @Override
               public void onReceive(Context context, Intent intent) {
                   int result=getResultCode();
                   String msg="Gửi thành công!";
                   if (result!=Activity.RESULT_OK)
                   {
                       msg="Có lỗi xảy ra, vui lòng thử lại!";
                   }
                   Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
               }

           },new IntentFilter("ACTION_MSG_SEND"));
           String mess1="User: "+MapsActivity.name+" đã gửi tín hiệu khẩn cấp tại vị trí "+
                   MapsActivity.locationUser;
           String mess=mess1+" lúc "+MapsActivity.atTime;
           smsManager.sendTextMessage(BaoDongActivity.smsPhone,null,mess,pendingIntent,
                   null);
       }


       @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser==null){
                //if null log out
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), ActivityDangNhap.class));
        }else if (mCurrentUser!=null){
            userReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCurrentUser!=null){
            userReference.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            dialogSetup();
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        if(id == R.id.action_alluser){
            startActivity(new Intent(MainActivity.this, AllUsersActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lienket) {
            setTitle("Liên kết người thân");
            LienKetActivity lienKetActivity = new LienKetActivity();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentX, lienKetActivity).commit();
        } else if (id == R.id.nav_nguoithan) {
            startActivity(new Intent(MainActivity.this, NhomNguoiThanMapActivity.class));
        } else if (id == R.id.nav_quanlynhom) {
            startActivity(new Intent(MainActivity.this,QuanLyNhomActivity.class));
        } else if (id == R.id.nav_guitin) {
            setTitle("Gửi tin");
            ChatActivity chatActivity = new ChatActivity();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentX, chatActivity).commit();
        } else if (id == R.id.nav_vungantoan) {
            setTitle("Vùng an toàn");
            VungAnToanActivity vungAnToanActivity = new VungAnToanActivity();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentX, vungAnToanActivity).commit();
        } else if (id == R.id.nav_caidat) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
         else if (id == R.id.nav_alert) {
            setTitle("Báo động");
            BaoDongActivity bdActivity = new BaoDongActivity();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentX, bdActivity).commit();
        }else if(id == R.id.nav_timbanbe){
            startActivity(new Intent(MainActivity.this, AllUsersActivity.class));
        }else if(id ==R.id.nav_logout){
            dialogSetup();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
       //log out
    private void dialogSetup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Log out");
        //alert.setIcon(R.drawable.icons8_logout);
        alert.setMessage("Do you want to log out?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(mCurrentUser != null){
                    userReference.child("online").setValue(ServerValue.TIMESTAMP);
                }

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), ActivityDangNhap.class));
                preferences = getSharedPreferences("File_name", MODE_PRIVATE);
                preferences.edit().putBoolean("saveLogin", false).apply();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alert.show();
    }

}
