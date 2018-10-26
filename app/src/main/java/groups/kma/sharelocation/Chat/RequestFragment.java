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
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView myRequestList;
    private View myMainView;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String online_user_id;
    private DatabaseReference UserReference;
    private DatabaseReference FriendDatabaseRef;
    private DatabaseReference FriendRequestDatabaseRef;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView = inflater.inflate(R.layout.fragment_request, container, false);
        myRequestList = myMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(online_user_id);
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendRequestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        myRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestList.setLayoutManager(linearLayoutManager);


        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Requests, RequestViewHolder>(Requests.class,
                        R.layout.friend_request_all_user_layout,
                        RequestFragment.RequestViewHolder.class,
                        FriendRequestReference
                ) {
                    @Override
                    protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position) {
                        final String list_user_id = getRef(position).getKey();
                        final DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                        get_type_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String request_type = dataSnapshot.getValue().toString();
                                    if (request_type.equals("received")) {
                                        UserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String username = dataSnapshot.child("userName").getValue().toString();
                                                final String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();
                                                final String user_status = dataSnapshot.child("status").getValue().toString();
                                                viewHolder.setUsername(username);
                                                viewHolder.setThumbImage(thumbimage);
                                                viewHolder.setUserStatus(user_status);
                                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Đồng ý kết bạn", "Hủy kết bạn"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Yêu cầu kết bạn");
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                if (i == 0) {
                                                                    Calendar calForDate = Calendar.getInstance();
                                                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                                    final String saveCurrentDate = currentDate.format(calForDate.getTime());
                                                                    FriendDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(
                                                                            new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    FriendDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {

                                                                                                    FriendRequestDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                    FriendRequestDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                                    Toast.makeText(getContext(), "Kết bạn thành công", Toast.LENGTH_SHORT).show();

                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                                // chọn hủy kb
                                                                if (i == 1) {
                                                                    FriendRequestDatabaseRef.child(online_user_id).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            FriendRequestDatabaseRef.child(list_user_id).child(online_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(getContext(), "Hủy kết bạn", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });

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
                                    } else if (request_type.equals("sent")) {
                                        UserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String username = dataSnapshot.child("userName").getValue().toString();
                                                final String thumbimage = dataSnapshot.child("photoUrl").getValue().toString();
                                                final String user_status = dataSnapshot.child("status").getValue().toString();
                                                viewHolder.setUsername(username);
                                                viewHolder.setThumbImage(thumbimage);
                                                viewHolder.setUserStatus(user_status);
                                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Hủy kết bạn"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Yêu cầu kết bạn");
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                // chọn hủy kb
                                                                if (i == 0) {
                                                                    FriendRequestDatabaseRef.child(online_user_id).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            FriendRequestDatabaseRef.child(list_user_id).child(online_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(getContext(), "Hủy kết bạn", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });

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
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                };
        myRequestList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username) {
            TextView x = mView.findViewById(R.id.request_profile_name);
            x.setText(username);
        }

        public void setThumbImage(String thumbimage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.request_profile_image);
            Picasso.get().load(thumbimage).placeholder(R.mipmap.ic_launcher).into(thumb_image);
        }

        public void setUserStatus(String user_status) {
            TextView x = mView.findViewById(R.id.request_profile_status);
            x.setText(user_status);
        }
    }

}
