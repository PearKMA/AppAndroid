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

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView myChatsList;
    private View myMainView;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    String online_user_id;

    public ChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_chat, container, false);
        myChatsList = (RecyclerView) myMainView.findViewById(R.id.chats_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        myChatsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myChatsList.setLayoutManager(linearLayoutManager);
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatFragment.ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(
                Chats.class,
                R.layout.item_user_singer_layout,
                ChatFragment.ChatsViewHolder.class,
                mFriendDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatFragment.ChatsViewHolder viewHolder, Chats model, int position) {

                final String list_user_id = getRef(position).getKey();
                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("userName").getValue().toString();
                        String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();
                        String user_status = dataSnapshot.child("status").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String online_status = (String) dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_status);
                        }
                        viewHolder.setUsername(username);
                        viewHolder.setThumb(thumbimage);
                        viewHolder.setUserStatus(user_status);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dataSnapshot.child("online").exists()) {
                                    Intent chatIntent = new Intent(getContext(), MessageActivity.class);
                                    chatIntent.putExtra("user_id", list_user_id);
                                    chatIntent.putExtra("user_name", username);
                                    startActivity(chatIntent);
                                } else {
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
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        myChatsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String name) {
            TextView usname = mView.findViewById(R.id.textView1);
            usname.setText(name);
        }

        public void setThumb(String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.profileimg);
            Picasso.get().load(thumbimage).placeholder(R.mipmap.ic_launcher).into(thumb_image);

        }

        public void setUserOnline(String online_status) {
            ImageView online_status_view = mView.findViewById(R.id.icon_online);
            if (online_status.equals("true")) {
                online_status_view.setVisibility(View.VISIBLE);
            } else {
                online_status_view.setVisibility(View.INVISIBLE);
            }

        }

        public void setUserStatus(String userStatus) {
            TextView user_status = mView.findViewById(R.id.custom_user_last_seen);
            user_status.setText(userStatus);
        }
    }

}
