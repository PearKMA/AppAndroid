package groups.kma.sharelocation.VungAnToan;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.ListGiamSat;

public class VungAnToanActivity extends Fragment{

    private Location location;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String UserId;
    private ArrayList<String> dsFriendId;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ListView lvDistance;
    EditText edtDistance;
    FloatingActionButton btnSet;
    private ArrayList<ListGiamSat> listName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vung_an_toan, container, false);
        edtDistance = view.<EditText>findViewById(R.id.edtKhoangCach);
        btnSet= view.<FloatingActionButton>findViewById(R.id.fab);
        arrayList=new ArrayList<>();
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_multiple_choice,
                arrayList);
        lvDistance= view.<ListView>findViewById(R.id.lvDs);
        lvDistance.setAdapter(adapter);
        lvDistance.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVungAnToan(view);
            }
        });

        dsFriendId=new ArrayList<>();
        listName=new ArrayList<ListGiamSat>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserId=mAuth.getCurrentUser().getUid();
        getFriendId();
        return view;
    }

    private void setVungAnToan(View view) {
        mDatabase.child("AlertArea").setValue("");
        String distance= edtDistance.getText().toString();
        distance.trim();

        SparseBooleanArray checked = lvDistance.getCheckedItemPositions();
        int size = checked.size(); // number of name-value pairs in the array
        if (!distance.equals("")){
            if (!distance.equals("0")&&size!=0) {
                mDatabase.child("AlertArea").child(UserId).child("Distance").setValue(Double.valueOf(distance));
                for (int i = 0; i < size; i++) {
                    int key = checked.keyAt(i);
                    boolean value = checked.get(key);
                    if (value) {
                        for (ListGiamSat item : listName) {
                            if (lvDistance.getItemAtPosition(key).equals(item.getName())) {
                                mDatabase.child("AlertArea").child(UserId).child("Friends").child("" + i).
                                        setValue(item);
                            }
                        }
                        Toast.makeText(getContext(), "Đã thiết lập! Hủy bỏ bằng cách nhập 0!"
                                , Toast.LENGTH_SHORT).show();
                        edtDistance.setText("");
                    }
                }
            }if (distance.equals("0")){
                Toast.makeText(getContext(),"Hủy thiết lập!",Toast.LENGTH_SHORT).show();
                mDatabase.child("AlertArea").setValue("");
                edtDistance.setText("");
            }
            if (size!=0){
                Toast.makeText(getContext(), "Vui lòng điền khoảng cách và chọn đối tượng cần báo động!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
                Toast.makeText(getContext(), "Vui lòng điền khoảng cách và chọn đối tượng cần báo động!",
                        Toast.LENGTH_SHORT).show();
        }

    }

    private void getDS() {
        for (final String key:dsFriendId) {
            mDatabase.child("LocationUsers").child(key).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String name="";
                                    Double lat;
                                    Double lon;
                                    int i=0;
                                    for (DataSnapshot childSbapshot : dataSnapshot.getChildren()) {
                                        name = String.valueOf(childSbapshot.child("name").getValue());
                                        lat =(Double) childSbapshot.child("latitude").getValue();
                                        lon =(Double) childSbapshot.child("longtitude").getValue();
                                        for (ListGiamSat ds : listName) {
                                            if (name.equals(ds.getName())){
                                                i=1;
                                            }
                                        }
                                        if (i==0){
                                            listName.add(new ListGiamSat(name, key,lat,lon));
                                            arrayList.add(name);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
        }
    }


    private void getFriendId(){
            mDatabase.child("Friends").child(UserId).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildren()!=null)
                            {
                                dsFriendId.clear();
                                for(DataSnapshot friendKey : dataSnapshot.getChildren()) {
                                    dsFriendId.add(friendKey.getKey());
                                }
                                getDS();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }
}
