package groups.kma.sharelocation.LienKetAction;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import groups.kma.sharelocation.Chat.SettingsActivity;
import groups.kma.sharelocation.MainActivity;
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.MemberLocations;

public class LienKetActivity extends Fragment {
    private View view;
    private TextView malienket;
    private Button btnCreate, btnThamGia, btnTaoNhom;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mGroupKey;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String uID = "";
    private DatabaseReference rootRef;
    private String randomkey = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lien_ket, container, false);
        btnTaoNhom = view.findViewById(R.id.btnTaoNhom);
        btnThamGia = view.findViewById(R.id.btnThamGiaNhom);
        //malienket = view.findViewById(R.id.malienket);
        //btnCreate = view.findViewById(R.id.btnCreate);
        //CreateRandom();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        uID = mCurrentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnThamGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(getContext(), ThamGiaActivity.class);
                startActivity(inten);
            }
        });
        btnTaoNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaoNhom();
            }
        });
        return view;
    }

    public void TaoNhom() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Tạo nhóm");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_creat_group);
        Button acpt = dialog.findViewById(R.id.dongytaonhom);
        Button decl = dialog.findViewById(R.id.huybotaonhom);
        final EditText editstt = dialog.findViewById(R.id.edit_tennhom);

        acpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit = editstt.getText().toString();
                if (TextUtils.isEmpty(edit)) {
                    Toast.makeText(getContext(), "Vui lòng nhập tên nhóm.", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog = new ProgressDialog(dialog.getContext());
                    mProgressDialog.setTitle("Tạo nhóm");
                    mProgressDialog.setMessage("Đang tạo nhóm vui lòng đợi...");
                    mProgressDialog.show();
                    DatabaseReference group_location = rootRef.child("Users").child(uID).child("GroupLocationKey").push();
                    String group_location_id = group_location.getKey();
                    String group_child = "Users/"+uID+"/GroupLocationKey/"+group_location_id;
                    Map groupBody = new HashMap();
                    groupBody.put("NameGroup",edit);
                    Map groupDetail = new HashMap();
                    groupDetail.put(group_child,groupBody);
                    rootRef.updateChildren(groupDetail, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.d("Chat_Log",databaseError.getMessage().toString());
                            }
                            editstt.setText("");
                        }
                    });
                    String group_childcon = "GroupLocationCon/"+group_location_id;
                    String group_childconmember = "GroupLocationCon/"+group_location_id+"/Members";
                    Map groupConBody = new HashMap();
                    groupConBody.put("NameGroup",edit);
                    Map groupConDetail = new HashMap();
                    groupConDetail.put(group_childcon,groupConBody);
                    rootRef.updateChildren(groupConDetail, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                    //them nhánh member
                    String type = "admin";
                    MemberLocations mem = new MemberLocations(type);
                    rootRef.child("GroupLocationCon").child(group_location_id).child("Members").child(uID).setValue(mem, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                }
                            });

                    //create randomkey invite
                    char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
                    StringBuilder sb1 = new StringBuilder();
                    Random random1 = new Random();
                    for (int i = 0; i < 6; i++) {
                        char c1 = chars1[random1.nextInt(chars1.length)];
                        sb1.append(c1);
                    }
                    randomkey = sb1.toString();
                    //
                    String inviteKey = "InviteKey/"+randomkey;
                    Map inviteKeyCon = new HashMap();
                    inviteKeyCon.put("GroupId",group_location_id);
                    inviteKeyCon.put("NameGroup",edit);
                    Map inviteKeyConDetail = new HashMap();
                    inviteKeyConDetail.put(inviteKey,inviteKeyCon);
                    rootRef.updateChildren(inviteKeyConDetail, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });



                    mProgressDialog.dismiss();
                    dialog.dismiss();

                }
            }
        });

        decl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    // random key
    public void CreateRandom() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
                StringBuilder sb1 = new StringBuilder();
                Random random1 = new Random();
                for (int i = 0; i < 6; i++) {
                    char c1 = chars1[random1.nextInt(chars1.length)];
                    sb1.append(c1);
                }
                String random_string = sb1.toString();
                //malienket.setText(random_string);
            }
        });

    }

}
