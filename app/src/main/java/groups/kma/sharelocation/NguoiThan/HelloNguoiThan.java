package groups.kma.sharelocation.NguoiThan;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
        String groupname = getIntent().getStringExtra("groupname");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Nhóm " + groupname);
        final String key = getIntent().getStringExtra("groupinvitekey");
        final String groupid = getIntent().getStringExtra("groupid");
        mGroup = FirebaseDatabase.getInstance().getReference().child("GroupLocationCon").child(groupid).child("Members");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        txtKey.setText(key);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HelloNguoiThan.this));
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
            startActivity(new Intent(HelloNguoiThan.this, SettingsActivity.class));
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
            Picasso.get().load(thumbimage).placeholder(R.drawable.acc_box).into(thumb_image);
        }

    }


}
