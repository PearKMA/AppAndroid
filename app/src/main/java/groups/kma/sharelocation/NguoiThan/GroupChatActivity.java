package groups.kma.sharelocation.NguoiThan;

import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import groups.kma.sharelocation.R;

public class GroupChatActivity extends AppCompatActivity {
    private ImageButton img_send;
    private final List<MessageGroup> messageGroups = new ArrayList<>();
    private EditText input_text;
    private RecyclerView userMessageList;
    private LinearLayoutManager linearLayoutManager;
    private ScrollView scrollView;
    private TextView display_text;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupCon,GroupKey,rootRef;
    private String groupid,groupname,currentUserId,currentUserName,currentDate,currentTime;
    private ChatGroupAdapter chatGroupAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1 ;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef = FirebaseDatabase.getInstance().getReference();
        groupname = getIntent().getStringExtra("groupname");
        groupid = getIntent().getStringExtra("groupid");
        GroupCon = FirebaseDatabase.getInstance().getReference().child("GroupLocationCon").child(groupid).child("Messages");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat nhóm "+groupname);
        img_send = findViewById(R.id.send_message_group);
        input_text = findViewById(R.id.input_group_message);
        chatGroupAdapter = new ChatGroupAdapter(messageGroups);
        mRefreshLayout =findViewById(R.id.message_swipe_layout);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList = findViewById(R.id.chatview);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(chatGroupAdapter);
        FetchMessages();

        GetUserInfo();
        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageToFB();
                input_text.setText("");
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos=0;

                FetchMoreMessages();

            }
        });

    }

    private void FetchMoreMessages() {
        DatabaseReference messageRef = rootRef.child("GroupLocationCon").child(groupid).child("Messages");
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageGroup messages = dataSnapshot.getValue(MessageGroup.class);
                String messageKey= dataSnapshot.getKey();
                if(!mPrevKey.equals(messageKey)){
                    messageGroups.add(itemPos++,messages);
                }else{
                    mPrevKey = messageKey;
                }
                if(itemPos==1){
                    mLastKey = messageKey;
                }


                chatGroupAdapter.notifyDataSetChanged();
                userMessageList.scrollToPosition(messageGroups.size()-1);
                mRefreshLayout.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(10,0);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMessages() {
        DatabaseReference messageRef = rootRef.child("GroupLocationCon").child(groupid).child("Messages");
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageGroup messages = dataSnapshot.getValue(MessageGroup.class);
                itemPos++;
                if(itemPos==1){
                    String messageKey= dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }
                messageGroups.add(messages);
                chatGroupAdapter.notifyDataSetChanged();
                userMessageList.scrollToPosition(messageGroups.size()-1);
                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void saveMessageToFB() {
        String message = input_text.getText().toString();
        String messageKey = GroupCon.push().getKey();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "Hãy nhập tin nhắn.", Toast.LENGTH_SHORT).show();
        }else{

            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            currentDate = simpleDateFormat.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm");
            currentTime = simpleTimeFormat.format(calendarTime.getTime());
            GroupKey = GroupCon.child(messageKey);
            HashMap<String,Object> messageInfo = new HashMap<>();
            messageInfo.put("name",currentUserName);
            messageInfo.put("message",message);
            messageInfo.put("date",currentDate);
            messageInfo.put("time",currentTime);
            messageInfo.put("fromId",currentUserId);
            GroupKey.updateChildren(messageInfo);

        }
    }

    private void GetUserInfo() {
        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("userName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
