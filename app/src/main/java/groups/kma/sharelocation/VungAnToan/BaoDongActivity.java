package groups.kma.sharelocation.VungAnToan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Users;

public class BaoDongActivity extends Fragment {
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ListView lvPhoneAlert;
    EditText edtPhone;
    Button btnPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String UserId;
    public static String smsPhone;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bao_dong, container, false);
        edtPhone = view.<EditText>findViewById(R.id.edtSdt);
        btnPhone = view.<Button>findViewById(R.id.btnThemSDT);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        lvPhoneAlert = view.findViewById(R.id.lvPhoneAlert);
        lvPhoneAlert.setAdapter(adapter);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edtPhone.getText().toString();
                int kt=1;
                for(String ph: arrayList){
                    if (ph.equalsIgnoreCase(phone)){
                        kt=0;
                        Toast.makeText(getActivity(),"Số điện thoại đã tồn tại!",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                if(kt==1) {
                    arrayList.add(phone);
                    adapter.notifyDataSetChanged();
                    edtPhone.setText("");
                    edtPhone.requestFocus();
                    mDatabase.child("AlertSmS").child(UserId).child("phone").setValue(phone);
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserId=mAuth.getCurrentUser().getUid();


        getPhoneFromFireBase();
        return view;
        }

    private void getPhoneFromFireBase() {
        mDatabase.child("AlertSmS").child(UserId).child("phone").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                String phone= String.valueOf(dataSnapshot.getValue());
                if (phone==null)
                {
                    phone="Chưa thiết lập số điện thoại!";
                }else {
                    smsPhone = phone;
                }
                arrayList.add(phone);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
