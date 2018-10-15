package groups.kma.sharelocation.LoginAction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import groups.kma.sharelocation.MainActivity;
import groups.kma.sharelocation.R;

public class ActivityDangNhap extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassWord;
    private CheckBox cbRemember;
    private ImageButton ibtShowPW;

    private DatabaseReference usersReference;

    private FirebaseAuth mAuth;

    private int clickCount = 0;

    String email, password;

    private ProgressDialog progressDialog;

    private SharedPreferences preferences;
    private Boolean saveLogin;
    private static final String FILE_NAME = "File_name";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassWord = findViewById(R.id.edtPassWord);
        cbRemember = findViewById(R.id.cbRemember);
        ibtShowPW = findViewById(R.id.ibtShowPW);

        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);


        preferences = getSharedPreferences("File_name", MODE_PRIVATE);


        saveLogin = preferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            edtEmail.setText(preferences.getString("username", ""));
            edtPassWord.setText(preferences.getString("password", ""));
            cbRemember.setChecked(true);
            signIn();
        }


    }


    public void ibshowpw(View view) {
        clickCount += 1;
        if (clickCount % 2 != 0) {
            // show password
            edtPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            edtPassWord.setSelection(edtPassWord.getText().length());
        } else {
            // hide password
            edtPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edtPassWord.setSelection(edtPassWord.getText().length());
        }
    }

    public void tvForgot(View view) {
    }

    public void btSignIn(View view) {
        showSpinnerProgressDialog();
        signIn();
    }

    public void btSignUp(View view) {
        startActivity(new Intent(this, ActivityDangKy.class));
    }

    public void signIn() {
        email = edtEmail.getText().toString().trim();
        password = edtPassWord.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Nhập password!", Toast.LENGTH_SHORT).show();
            return;
        }

        //đăng nhập bằng email và password vừa tạo
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String online_user_id = mAuth.getCurrentUser().getUid();
                            String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                            usersReference.child(online_user_id).child("device_token").setValue(DeviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkEmailVerification();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(ActivityDangNhap.this, "Mật khẩu hoặc người dùng không tồn tại !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        // tính năng verified email trước khi đăng nhập - tạm thời hủy bỏ
        //if (user.isEmailVerified()) {
            //nếu check box được tích thì lưu đăng nhập đến khi người dùng logout
            if (cbRemember.isChecked()) {
                preferences.edit().putBoolean("saveLogin", true).apply();
                preferences.edit().putString("username", email).apply();
                preferences.edit().putString("password", password).apply();
            } else {
                preferences.edit().putBoolean("saveLogin", false).apply();
                preferences.edit().putString("username", "").apply();
                preferences.edit().putString("password", "").apply();
            }

            // Sign in success, update UI with the signed-in user's information
            Toast.makeText(ActivityDangNhap.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            // hàm if else của tính năng verified
//        } else {
//            Toast.makeText(getApplicationContext(), "Hãy xác nhận email trong hòm thư trước", Toast.LENGTH_SHORT).show();
//            mAuth.signOut();
//            progressDialog.dismiss();
//        }
    }

    public void showSpinnerProgressDialog() {
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Logging in, please wait");
        progressDialog.show();
    }


}
