package groups.kma.sharelocation.Chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Users;

public class AllUsersActivity extends AppCompatActivity {
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Các người dùng hiện tại");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersList = findViewById(R.id.rcViewUser);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,R.layout.item_user_singer_layout,UsersViewHolder.class,mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                        viewHolder.setName(model.getUserName());
                        viewHolder.setStatus(model.getStatus());
                        viewHolder.setImage(model.getPhotoUrl());
                        final String user_id = getRef(position).getKey();
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id",user_id);
                                startActivity(profileIntent);
                            }
                        });
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name){
            //id trong item singer
            TextView username =(TextView) mView.findViewById(R.id.textView1);
            username.setText(name);
        }
        public void setStatus(String status){
            TextView mStatus =(TextView) mView.findViewById(R.id.custom_user_last_seen);
            mStatus.setText(status);
        }
        public void setImage(String image ){
            CircleImageView imageView = mView.findViewById(R.id.profileimg);
            Picasso.get().load(image).placeholder(R.drawable.acc_box).into(imageView);
        }
    }

}
