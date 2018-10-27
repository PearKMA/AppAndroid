package groups.kma.sharelocation.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Users;

public class ChangePassActivity extends AppCompatActivity {
    private EditText edtCurrentPassword, edtNewPassword, edtConfirmNewPW;
    String AES = "AES";
    String giaima;
    // tạo 1 biến oldPassword để gán giá trị của password cũ vào
    private String oldPassword = "";
    private String currentPassword, newPassword, confirmNewPassword;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        edtCurrentPassword = findViewById(R.id.edtCurrentPW);
        edtNewPassword = findViewById(R.id.edtNewPW);
        edtConfirmNewPW = findViewById(R.id.edtConfirmNewPW);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUser.getUid();
       mRef = FirebaseDatabase.getInstance().getReference();
    }
    public void btCancel(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
    public void btChange(View view) {
        currentPassword = edtCurrentPassword.getText().toString().trim();
        newPassword = edtNewPassword.getText().toString().trim();
        confirmNewPassword = edtConfirmNewPW.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = mDatabase.getReference().child("Users").child(uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users password = dataSnapshot.getValue(Users.class);
                //gán giá trị của PW cũ vào biến old password
                oldPassword = password.getPassword();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(getApplicationContext(), "Nhập mật khẩu hiện tại của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getApplicationContext(), "Nhập mật khẩu mới của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(getApplicationContext(), "Xác nhận mật khẩu mới của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            giaima = decrypt(oldPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //kiểm tra xem password trên database có trùng với password nhập vào hay không
        if (giaima.equals(currentPassword)) {
            //kiểm tra passwôrd cũ và mới có trùng nhau hay không
            if (currentPassword.equals(newPassword)) {
                Toast.makeText(this, "Mật khẩu cũ và mới trùng nhau ,vui lòng nhập mật khẩu khác", Toast.LENGTH_SHORT).show();
            } else {
                //kiểm tra xác thực password có giống newPW hay không

                if (newPassword.equals(confirmNewPassword)) {
                    mUser.updatePassword(edtNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePassActivity.this, "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                updatePassword();
                                edtCurrentPassword.setText("");
                                edtNewPassword.setText("");
                                edtConfirmNewPW.setText("");
                                finish();
                            } else {
                                Toast.makeText(ChangePassActivity.this, "Không thể thay đổi mật khẩu của bạn", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Mật khẩu mới của bạn không được đồng bộ hóa", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Mật khẩu cũ của bạn không đúng", Toast.LENGTH_SHORT).show();
        }
    }
    // giải mã
    private String decrypt(String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(password, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptValue = new String(decValue);
        return decryptValue;
    }
    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec;

    }
    private String encrypt(String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }


    //update PW mới đổi lên database để khi lấy về gán vào old PW
    // cần mã hóa new pass
    private void updatePassword() {
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        // mã hóa 
        String passwordEcn = "";
        try {
            //pass word đã mã hóa
            passwordEcn = encrypt(newPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRef.child("password").setValue(passwordEcn, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                } else {
                }
            }
        });
    }

}
