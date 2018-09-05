package groups.kma.sharelocation.LoginAction;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import groups.kma.sharelocation.R;

public class ActivityDangKy extends AppCompatActivity {
    private EditText edtUserName;
    private EditText edtEmailAddress;
    private EditText edtPassWord1;
    private EditText edtConfirmPW;
    private CheckBox chkBoxTerms;

    private DatabaseReference mRef;

    private FirebaseAuth mAuth;

    String userName, emailAD, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        edtUserName = findViewById(R.id.edtUserName);
        edtEmailAddress = findViewById(R.id.edtEmailAddress);
        edtPassWord1 = findViewById(R.id.edtPassWord1);
        edtConfirmPW = findViewById(R.id.edtConfirmPW);
        chkBoxTerms = findViewById(R.id.chkBoxTerms);

        mAuth = FirebaseAuth.getInstance();
    }
    public void btCreateAccount(View view) {
        SignUp();
    }

    public void SignUp() {
        userName = edtUserName.getText().toString().trim();

        emailAD = edtEmailAddress.getText().toString().trim();

        password = edtPassWord1.getText().toString().trim();
        String cfpassword = edtConfirmPW.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(), "Nhập tên người dùng của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(emailAD)) {
            Toast.makeText(getApplicationContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cfpassword)) {
            Toast.makeText(getApplicationContext(), "Vui lòng xác nhận mật khẩu của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Mật khẩu quá ngắn, nhập tối thiểu 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        //xác thực password nếu trùng với pass ở trên thì vứt hết data lên database

        if (password.equals(cfpassword)) {
            if (chkBoxTerms.isChecked()) {
                mAuth.createUserWithEmailAndPassword(emailAD, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sendUserData();
                                    // Sign in success, update UI with the signed-in user's information
                                    startActivity(new Intent(getApplicationContext(), ActivityDangNhap.class));
                                    finish();
                                    sendEmailVerification();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), "Đăng ký không thành công! ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                popupDialog();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Mật khẩu của bạn không được đồng bộ hóa, vui lòng nhập lại! ", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    //verify email and upload user's data to database
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityDangKy.this, "Kiểm tra email của bạn để xác minh", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //gửi thông tin user lên database
    private void sendUserData() {
        mRef = FirebaseDatabase.getInstance().getReference(mAuth.getUid());
        Users users = new Users(userName,emailAD,password);
        mRef.child("Users").setValue(users, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(getApplicationContext(), "Đăng ký thành công!\n ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityDangKy.this, "Lỗi!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void tvSignIn(View view) {
        startActivity(new Intent(this, ActivityDangNhap.class));
        finish();
    }

    private void popupDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ActivityDangKy.this);
        alert.setTitle("Xác nhận");
        alert.setMessage("Kiểm tra ở đây để cho biết rằng bạn đã đọc và đồng ý với các điều khoản và điều kiện");
        alert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
