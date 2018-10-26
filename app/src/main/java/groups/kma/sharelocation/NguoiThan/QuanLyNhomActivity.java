package groups.kma.sharelocation.NguoiThan;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.Chat.MessageActivity;
import groups.kma.sharelocation.Chat.ProfileActivity;
import groups.kma.sharelocation.R;

public class QuanLyNhomActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DatabaseReference mGroupDatabase;
    private DatabaseReference mGroupConDatabase;
    private FirebaseAuth mAuth;
    private String mUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_nhom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.danhsachnhom);
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUid).child("GroupLocationKey");
        mGroupConDatabase = FirebaseDatabase.getInstance().getReference().child("GroupLocationCon");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(QuanLyNhomActivity.this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Quản lý nhóm");


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<QuanLyNhomModel,QuanLyNhomViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<QuanLyNhomModel, QuanLyNhomViewHolder>(
                QuanLyNhomModel.class,
                R.layout.item_group_list_danhsach,
                QuanLyNhomViewHolder.class,
                mGroupDatabase
        ) {
            @Override
            protected void populateViewHolder(final QuanLyNhomViewHolder viewHolder, QuanLyNhomModel model, int position) {
                final String list_group_id = getRef(position).getKey();
                mGroupConDatabase.child(list_group_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String groupid = dataSnapshot.getKey().toString();
                        final String groupname = dataSnapshot.child("NameGroup").getValue().toString();
                        final MemberType memberType =dataSnapshot.child("Members").child(mUid).getValue(MemberType.class);
                        final String membertype = memberType.getType().toString();
                        String memberkey = "";
                        if(membertype.equals("admin")) {
                             memberkey = memberType.getInviteKey().toString();
                        }
                        final String statusgroup = dataSnapshot.child("StatusGroup").getValue().toString();
                        String thumbimage = dataSnapshot.child("PhotoGroup").getValue().toString();
                        if(thumbimage!=null) {
                            viewHolder.setThumb(thumbimage);
                        }
                        viewHolder.setUsername(groupname);
                        viewHolder.setStatusGroup(statusgroup);
                        final String finalMemberkey = memberkey;
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Trường hợp 1 : khi mà bạn là admin
                                if(membertype.equals("admin")) {
                                    CharSequence options[] = new CharSequence[]{
                                            "Quản lý nhóm " + groupname, "Gửi tin nhắn"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyNhomActivity.this);
                                    builder.setTitle("Chọn tính năng");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                if (membertype.equals("admin")) {
                                                    Intent quanlyIntent = new Intent(QuanLyNhomActivity.this, HelloNguoiThan.class);
                                                    quanlyIntent.putExtra("groupid", groupid);
                                                    quanlyIntent.putExtra("groupname",groupname);
                                                    quanlyIntent.putExtra("groupinvitekey", finalMemberkey);
                                                    Toast.makeText(QuanLyNhomActivity.this, ""+finalMemberkey, Toast.LENGTH_SHORT).show();
                                                    startActivity(quanlyIntent);
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyNhomActivity.this);
                                                    builder.setTitle("Từ chối truy nhập");
                                                    builder.setMessage("Bạn không có quyền quản lý nhóm này.\n Nếu có vui lòng liên hệ nhà phát triển để được hỗ trợ");
                                                    builder.setCancelable(true);
                                                    AlertDialog alertDialog = builder.create();
                                                    alertDialog.show();
                                                }
                                            }

                                            if (i == 1) {
                                                //chat nhóm
                                                Intent chatnhomIntent = new Intent(QuanLyNhomActivity.this,GroupChatActivity.class);
                                                chatnhomIntent.putExtra("groupname",groupname);
                                                chatnhomIntent.putExtra("groupid",groupid);
                                                startActivity(chatnhomIntent);
                                            }

                                        }
                                    });
                                    builder.show();
                                }else{
                                    // Trường hợp 2
                                    CharSequence options[] = new CharSequence[]{
                                            "Chat nhóm"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyNhomActivity.this);
                                    builder.setTitle("Chọn tính năng");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                               // nhắn tin nhóm
                                                Intent chatnhomIntent = new Intent(QuanLyNhomActivity.this,GroupChatActivity.class);
                                                chatnhomIntent.putExtra("groupname",groupname);
                                                chatnhomIntent.putExtra("groupid",groupid);
                                                startActivity(chatnhomIntent);
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

    public static class QuanLyNhomViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public QuanLyNhomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setUsername(String name){
            TextView usname = mView.findViewById(R.id.textView1);
            usname.setText(name);
        }
        public void setThumb(final String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.circlegroup);
            if (!thumbimage.equals("default")) {
                //Picasso.get().load(image).placeholder(R.drawable.acc_box).into(mImage);
                Picasso.get().load(thumbimage).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.acc_box).into(thumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(thumbimage).placeholder(R.drawable.acc_box).into(thumb_image);
                    }
                });

            }
            Picasso.get().load(thumbimage).placeholder(R.drawable.acc_box).into(thumb_image);
        }


        public void setStatusGroup(String statusgroup) {
            TextView usname = mView.findViewById(R.id.textView2);
            usname.setText(statusgroup);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
