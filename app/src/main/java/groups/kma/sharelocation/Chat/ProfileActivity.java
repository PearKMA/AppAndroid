package groups.kma.sharelocation.Chat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import groups.kma.sharelocation.R;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgProfile;
    TextView txtProfileName, txtProfileStatus;
    private Button sendRequest, decline;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialog;
    private String current_state;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //id của người mà bạn đang xem
        final String id_user = getIntent().getStringExtra("user_id");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotification = FirebaseDatabase.getInstance().getReference().child("notifications");

        imgProfile = findViewById(R.id.imgProfile);
        sendRequest = findViewById(R.id.ketban);
        txtProfileName = findViewById(R.id.dnProfile);
        txtProfileStatus = findViewById(R.id.stProfile);
        decline = findViewById(R.id.huyketban);

        // trang thai nguoi dung mac dinh -> not_friends
        current_state = "not_friends";
        decline.setVisibility(View.INVISIBLE);
        decline.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Tải dữ liệu");
        mProgressDialog.setMessage("Vui lòng đợi một chút.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("userName").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                txtProfileName.setText(display_name);
                txtProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(imgProfile);

                //Friends list and request
                // check trang thai nguoi dung
                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id_user)) {

                            String req_type = dataSnapshot.child(id_user).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {
                                current_state = "req_received";
                                sendRequest.setText("Đồng ý kết bạn");

                                decline.setVisibility(View.VISIBLE);
                                decline.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                current_state = "req_sent";
                                sendRequest.setText("Hủy yêu cầu kết bạn");
                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);
                            }
                            mProgressDialog.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(id_user)) {
                                        current_state = "friends";
                                        sendRequest.setText("Hủy kết bạn");

                                        decline.setVisibility(View.INVISIBLE);
                                        decline.setEnabled(false);

                                    }
                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // khi bam nut ket ban
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendRequest.setEnabled(false);

                // TH1 : không phải bạn bè có thể bấm nút yêu cầu kết bạn - bấm xong có thể hủy yêu cầu kết bạn

                if (current_state.equals("not_friends")) {
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(id_user).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendRequestDatabase.child(id_user).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put("from",mCurrentUser.getUid());
                                        notificationData.put("type","request");

                                        mNotification.child(id_user).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                current_state = "req_sent";
                                                sendRequest.setText("Hủy yêu cầu kết bạn");
                                                decline.setVisibility(View.INVISIBLE);
                                                decline.setEnabled(false);
                                            }
                                        });

                                        current_state = "req_sent";
                                        sendRequest.setText("Hủy yêu cầu kết bạn");

                                        decline.setVisibility(View.INVISIBLE);
                                        decline.setEnabled(false);
                                        Toast.makeText(ProfileActivity.this, "Gửi yếu cầu thành công.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Gửi yêu cầu thất bại.", Toast.LENGTH_SHORT).show();
                            }
                            sendRequest.setEnabled(true);
                        }
                    });
                }

                //TH2 : Hủy yêu cầu kết bạn
                if (current_state.equals("req_sent")) {
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(id_user).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(id_user).child(mCurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendRequest.setEnabled(true);
                                                    current_state = "not_friends";
                                                    sendRequest.setText("Kết bạn");

                                                    decline.setVisibility(View.INVISIBLE);
                                                    decline.setEnabled(false);

                                                }
                                            });
                                }
                            });
                }

                //TH3 : chấp nhận yêu cầu kết bạn
                if (current_state.equals("req_received")) {
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    final String saveCurrentDate = currentDate.format(calForDate.getTime());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(id_user).child("date").setValue(saveCurrentDate).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(id_user).child(mCurrentUser.getUid()).child("date").setValue(saveCurrentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(id_user).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendRequestDatabase.child(id_user).child(mCurrentUser.getUid()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    sendRequest.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    sendRequest.setText("Hủy kết bạn");
                                                                                    decline.setVisibility(View.INVISIBLE);
                                                                                    decline.setEnabled(false);

                                                                                }
                                                                            });
                                                                }
                                                            });

                                                }
                                            });
                                }
                            }
                    );
                } else {

                }
                    // hủy kết bạn
                if(current_state.equals("friends")){
                        mFriendDatabase.child(mCurrentUser.getUid()).child(id_user).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendDatabase.child(id_user).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                            current_state = "not_friends";
                                            sendRequest.setText("Kết bạn");
                                            decline.setVisibility(View.INVISIBLE);
                                            decline.setEnabled(false);
                                            sendRequest.setEnabled(true);
                                    }
                                });
                            }
                        });

                }


            }
        });


    }

}
