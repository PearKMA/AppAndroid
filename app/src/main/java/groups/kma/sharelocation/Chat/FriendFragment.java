package groups.kma.sharelocation.Chat;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Friends;

public class FriendFragment extends Fragment {
    private RecyclerView mFriendLists;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    String mCurrent_user_id;
    private View mMainView;

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friend,container,false);

        mFriendLists = (RecyclerView) mMainView.findViewById(R.id.friendlist);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mFriendLists.setHasFixedSize(true);
        mFriendLists.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.item_user_singer_layout,
                FriendsViewHolder.class,
                mFriendDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                    viewHolder.setDate(model.getDate());
                    final String list_user_id = getRef(position).getKey();
                    mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            final String username = dataSnapshot.child("userName").getValue().toString();
                            String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();

                                if(dataSnapshot.hasChild("online")){
                                    String online_status = (String) dataSnapshot.child("online").getValue().toString();
                                    viewHolder.setUserOnline(online_status);
                                }
                            viewHolder.setUsername(username);
                            viewHolder.setThumb(thumbimage);
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]{
                                        "Hồ sơ "+username,"Gửi tin nhắn"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Chọn tính năng");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                if(i==0){
                                                    Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                                    profileIntent.putExtra("user_id",list_user_id);
                                                    startActivity(profileIntent);
                                                }

                                            if(i==1){
                                                    if(dataSnapshot.child("online").exists()) {
                                                        Intent chatIntent = new Intent(getContext(), MessageActivity.class);
                                                        chatIntent.putExtra("user_id", list_user_id);
                                                        chatIntent.putExtra("user_name", username);
                                                        startActivity(chatIntent);
                                                    }else{
                                                        mUserDatabase.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Intent chatIntent = new Intent(getContext(), MessageActivity.class);
                                                                        chatIntent.putExtra("user_id", list_user_id);
                                                                        chatIntent.putExtra("user_name", username);
                                                                        startActivity(chatIntent);
                                                                    }
                                                                });
                                                    }
                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            }
        };
        mFriendLists.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDate(String date){
                TextView userName = mView.findViewById(R.id.custom_user_last_seen);
            userName.setText("Lần cuối online :\n" + date);
        }
        public void setUsername(String name){
            TextView usname = mView.findViewById(R.id.textView1);
            usname.setText(name);
        }

        public void setThumb(String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.profileimg);
            Picasso.get().load(thumbimage).placeholder(R.drawable.acc_box).into(thumb_image);
        }

        public void setUserOnline(String online_status) {
            ImageView online_status_view = mView.findViewById(R.id.icon_online);
            if(online_status.equals("true")){
                online_status_view.setVisibility(View.VISIBLE);
            }else{
                online_status_view.setVisibility(View.INVISIBLE);
            }

        }
    }

}
