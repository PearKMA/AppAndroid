package groups.kma.sharelocation.Chat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class OfflineMode extends Application {
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //picasso offline
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            String online_user = mAuth.getCurrentUser().getUid();
            usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user);

            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usersReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
}
