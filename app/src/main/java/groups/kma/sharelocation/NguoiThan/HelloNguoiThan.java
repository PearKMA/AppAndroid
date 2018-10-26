package groups.kma.sharelocation.NguoiThan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.Chat.MessageActivity;
import groups.kma.sharelocation.Chat.ProfileActivity;
import groups.kma.sharelocation.Chat.SettingsActivity;
import groups.kma.sharelocation.MainActivity;
import groups.kma.sharelocation.R;

public class HelloNguoiThan extends AppCompatActivity {
    private TextView txtKey;
    private Button shareKey, changeKey;
    private RecyclerView mRecyclerView;
    private DatabaseReference mGroup, mUserDatabase;
    private FirebaseAuth mAuth;
    private String mUid;
    private DatabaseReference rootRef;
    private String randomkeyNew = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_nguoi_than);
        txtKey = findViewById(R.id.txtkey);
        shareKey = findViewById(R.id.chiase);
        changeKey = findViewById(R.id.doima);
        mRecyclerView = findViewById(R.id.listDs);
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        final String groupname = getIntent().getStringExtra("groupname");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Nhóm " + groupname);
        toolbar.setTitleTextColor(Color.WHITE);

        final String key = getIntent().getStringExtra("groupinvitekey");
        final String groupid = getIntent().getStringExtra("groupid");
        mGroup = FirebaseDatabase.getInstance().getReference().child("GroupLocationCon").child(groupid).child("Members");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        txtKey.setText(key);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HelloNguoiThan.this));
        shareKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Đây là mã mời vào nhóm của tôi trên ứng dụng SmartLocationPro - Tìm vị trí người thân và bạn bè , hãy nhập vào và tham gia với tôi : "+key;
                String shareSub = "Lời mời tham gia nhóm";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
        changeKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // tạo key mới
                char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
                StringBuilder sb1 = new StringBuilder();
                Random random1 = new Random();
                for (int i = 0; i < 6; i++) {
                    char c1 = chars1[random1.nextInt(chars1.length)];
                    sb1.append(c1);
                }
                randomkeyNew = sb1.toString();
                // gửi key vừa thay đổi lên firebase database
                rootRef.child("GroupLocationCon").child(groupid).child("Members").child(mUid).child("inviteKey").setValue(randomkeyNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                // xóa key cũ đi tạo key mới với dữ liệu cũ
                // 1. xóa key cũ
                rootRef.child("InviteKey").child(key).removeValue();
                // 2. tạo key mới với dữ liệu cũ
                String inviteKey = "InviteKey/" + randomkeyNew;
                Map inviteKeyCon = new HashMap();
                inviteKeyCon.put("GroupId", groupid);
                inviteKeyCon.put("NameGroup", groupname);
                Map inviteKeyConDetail = new HashMap();
                inviteKeyConDetail.put(inviteKey, inviteKeyCon);
                rootRef.updateChildren(inviteKeyConDetail, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Log.d("ThanhCong","tao key moi voi du lieu cu");
                    }
                });
                //end
                // cap nhat key o users
                rootRef.child("Users").child(mUid).child("GroupLocationKey").child(groupid).child("InviteKey").setValue(randomkeyNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("ThanhCong","doi thanh cong grouplocationcon");
                    }
                });

                Toast.makeText(HelloNguoiThan.this, "Đổi thành công.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings_group) {
            String groupid = getIntent().getStringExtra("groupid");
            Intent caidatnhom = new Intent(HelloNguoiThan.this, CaiDatNhom.class);
            caidatnhom.putExtra("groupid",groupid);
            startActivity(caidatnhom);
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<InfoMemberGroup, InfoMemberGroupViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<InfoMemberGroup, InfoMemberGroupViewHolder>(
                InfoMemberGroup.class,
                R.layout.item_user_singer_layout,
                InfoMemberGroupViewHolder.class,
                mGroup
        ) {
            @Override
            protected void populateViewHolder(final InfoMemberGroupViewHolder viewHolder, InfoMemberGroup model, int position) {
                final String list_user_id = getRef(position).getKey();
                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String uidmember = dataSnapshot.getKey();
                        final String username = dataSnapshot.child("userName").getValue().toString();
                        String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();
                        String stt = dataSnapshot.child("status").getValue().toString();
                        viewHolder.setStatus(stt);
                        viewHolder.setUsername(username);
                        viewHolder.setThumb(thumbimage);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mUid.equals(uidmember)) {
                                    Toast.makeText(HelloNguoiThan.this, "Bạn là admin nhóm này", Toast.LENGTH_SHORT).show();
                                } else {
                                    CharSequence options[] = new CharSequence[]{
                                            "Hồ sơ " + username, "Gửi tin nhắn", "Xóa khỏi nhóm"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HelloNguoiThan.this);
                                    builder.setTitle("Chọn tính năng");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                Intent profileIntent = new Intent(HelloNguoiThan.this, ProfileActivity.class);
                                                profileIntent.putExtra("user_id", list_user_id);
                                                startActivity(profileIntent);
                                            }

                                            if (i == 1) {
                                                if (dataSnapshot.child("online").exists()) {
                                                    Intent chatIntent = new Intent(HelloNguoiThan.this, MessageActivity.class);
                                                    chatIntent.putExtra("user_id", list_user_id);
                                                    chatIntent.putExtra("user_name", username);
                                                    startActivity(chatIntent);
                                                } else {
                                                    mUserDatabase.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent chatIntent = new Intent(HelloNguoiThan.this, MessageActivity.class);
                                                                    chatIntent.putExtra("user_id", list_user_id);
                                                                    chatIntent.putExtra("user_name", username);
                                                                    startActivity(chatIntent);
                                                                }
                                                            });
                                                }
                                            }
                                            if (i == 2) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(HelloNguoiThan.this);
                                                builder.setTitle("Xóa khỏi nhóm");
                                                builder.setMessage("Bạn có muốn xóa " + username + " khỏi nhóm không ?");
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
                                                        Toast.makeText(HelloNguoiThan.this, "" + uidmember, Toast.LENGTH_SHORT).show();
                                                        final String groupid = getIntent().getStringExtra("groupid");
                                                        rootRef.child("Users").child(uidmember).child("GroupLocationKey").child(groupid).removeValue(new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                Toast.makeText(HelloNguoiThan.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        rootRef.child("GroupLocationCon").child(groupid).child("Members").child(uidmember).removeValue(new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                            }
                                                        });
                                                    }
                                                });
                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();
                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class InfoMemberGroupViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public InfoMemberGroupViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setStatus(String stt) {
            TextView userName = mView.findViewById(R.id.custom_user_last_seen);
            userName.setText(stt);
        }

        public void setUsername(String name) {
            TextView usname = mView.findViewById(R.id.textView1);
            usname.setText(name);
        }

        public void setThumb(String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.profileimg);
            Picasso.get().load(thumbimage).placeholder(R.mipmap.ic_launcher).into(thumb_image);
        }

    }


}
