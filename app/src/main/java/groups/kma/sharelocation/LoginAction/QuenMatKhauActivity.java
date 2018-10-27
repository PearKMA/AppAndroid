package groups.kma.sharelocation.LoginAction;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import groups.kma.sharelocation.R;

public class QuenMatKhauActivity extends AppCompatActivity {
    private EditText edtEmail2Change;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quen_mat_khau);
        edtEmail2Change = findViewById(R.id.edtEmail2Change);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void btSubmit(View view) {
        String email = edtEmail2Change.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(QuenMatKhauActivity.this, "Nhập email!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(QuenMatKhauActivity.this, "Email đặt lại mật khẩu đã được gửi đến địa chỉ email của bạn", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(QuenMatKhauActivity.this, ActivityDangNhap.class));
                    }else{
                        Toast.makeText(QuenMatKhauActivity.this, "Lỗi khi gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void btBack(View view) {
        startActivity(new Intent(this, ActivityDangNhap.class));
    }
}
