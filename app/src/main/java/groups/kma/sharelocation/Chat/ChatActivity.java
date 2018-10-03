package groups.kma.sharelocation.Chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Users;

public class ChatActivity extends Fragment {
    private View view;
    private FirebaseAuth mAuth;
    private TextView txt;
    private FirebaseDatabase firebaseDatabase;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat, container, false);
        mAuth = FirebaseAuth.getInstance();
        mViewPager = view.findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = view.findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        chathe();
        return view;
    }

    public void chathe(){
        //xac thuc user hien tai
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // doc database tren server
        DatabaseReference databaseReference =  firebaseDatabase.getReference(mAuth.getUid()).child("Users");
        if (user != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    String  name = users.getUserName();
                    String email = users.getEmail();
                    Toast.makeText(getContext(), ""+name+" "+email+" ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }
    //

}
