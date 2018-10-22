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

import java.util.Random;

import groups.kma.sharelocation.Chat.SettingsActivity;
import groups.kma.sharelocation.MainActivity;
import groups.kma.sharelocation.R;

public class LienKetActivity extends Fragment {
    private View view;
    private TextView malienket;
    private Button btnCreate,btnThamGia,btnTaoNhom;
    private ProgressDialog mProgressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lien_ket, container, false);
        btnTaoNhom = view.findViewById(R.id.btnTaoNhom);
        btnThamGia = view.findViewById(R.id.btnThamGiaNhom);
        //malienket = view.findViewById(R.id.malienket);
        //btnCreate = view.findViewById(R.id.btnCreate);
        //CreateRandom();








        btnThamGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(getContext(),ThamGiaActivity.class);
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

    public void TaoNhom(){
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
                        mProgressDialog = new ProgressDialog(dialog.getContext());
                        mProgressDialog.setTitle("Tạo nhóm");
                        mProgressDialog.setMessage("Đang tạo nhóm vui lòng đợi...");
                        mProgressDialog.show();
                        String edit = editstt.getText().toString();
                        /*
                        mStatusDatabase.child("status").setValue(edit).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mProgressDialog.dismiss();
                                }else{
                                    Toast.makeText(SettingsActivity.this, "Thay đổi trạng thái thất bại.", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            }
                        });
                        */
                        mProgressDialog.dismiss();
                        dialog.dismiss();
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
    public void CreateRandom(){
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
                StringBuilder sb1 = new StringBuilder();
                Random random1 = new Random();
                for (int i = 0; i < 6; i++)
                {
                    char c1 = chars1[random1.nextInt(chars1.length)];
                    sb1.append(c1);
                }
                String random_string = sb1.toString();
                //malienket.setText(random_string);
            }
            });

    }

}
