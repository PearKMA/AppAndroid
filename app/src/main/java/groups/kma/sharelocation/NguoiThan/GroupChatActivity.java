package groups.kma.sharelocation.NguoiThan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import groups.kma.sharelocation.R;

public class GroupChatActivity extends AppCompatActivity {
    private ImageButton img_send;
    private EditText input_text;
    private ScrollView scrollView;
    private TextView display_text;
    private String groupname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        groupname = getIntent().getStringExtra("groupname");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat nhóm "+groupname);
        img_send = findViewById(R.id.send_message_group);
        input_text = findViewById(R.id.input_group_message);
        scrollView = findViewById(R.id.my_scroll_view);
        display_text = findViewById(R.id.groupchat_text_display);

        Toast.makeText(GroupChatActivity.this, "Group name: "+groupname, Toast.LENGTH_SHORT).show();

    }

    private void Init() {
    }
}
