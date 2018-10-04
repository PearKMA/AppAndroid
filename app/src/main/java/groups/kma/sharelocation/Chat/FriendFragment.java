package groups.kma.sharelocation.Chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Friends;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {
    private RecyclerView mFriendLists;
    private DatabaseReference mFriendDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friend,container,false);

        mFriendLists = mMainView.findViewById(R.id.friendlist);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);

        mFriendLists.setHasFixedSize(true);
        mFriendLists.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,R.layout.item_user_singer_layout,FriendsViewHolder.class,mFriendDatabase
        ) {
            @Override
            protected void populateViewHolder(FriendsViewHolder viewHolder, Friends model, int position) {
                    viewHolder.setDate(model.getDate());
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
            TextView userName = mView.findViewById(R.id.textView2);
            userName.setText(date);
        }
    }

}
