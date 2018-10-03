package groups.kma.sharelocation.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import groups.kma.sharelocation.R;

public class ProfileActivity extends AppCompatActivity {

    TextView txt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txt1 = findViewById(R.id.textView);
        String id_user = getIntent().getStringExtra("user_id");
        txt1.setText(id_user);
    }
}
