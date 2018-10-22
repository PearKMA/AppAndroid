package groups.kma.sharelocation.LienKetAction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import groups.kma.sharelocation.LoginAction.ActivityDangKy;
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.GroupLocations;
import groups.kma.sharelocation.model.MemberLocations;

public class ThamGiaActivity extends AppCompatActivity {
    private Button btnThamgia;
    private EditText invitecode;
    private String mamoidanhap;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tham_gia);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference();
        btnThamgia = findViewById(R.id.btnThamGia);
        invitecode = findViewById(R.id.NhapMaMoi);

        btnThamgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // nhap ma moi de lay key nhom
                mamoidanhap = invitecode.getText().toString().trim();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("InviteKey").child(mamoidanhap);
                mRef.child("InviteKey").child(mamoidanhap).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GroupLocations group_location = dataSnapshot.getValue(GroupLocations.class);
                        String group_location_id = group_location.getGroupId();
                        Toast.makeText(ThamGiaActivity.this, ""+group_location_id, Toast.LENGTH_SHORT).show();
                        //tham gia nhom
                        String type = "member";
                        MemberLocations mem = new MemberLocations(type);
                        mRef.child("GroupLocationCon").child(group_location_id).child("Members").child(uid).setValue(mem, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Toast.makeText(ThamGiaActivity.this, "Tham gia nhóm thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ThamGiaActivity.this, "Không có nhóm với mã mời này", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }
}
