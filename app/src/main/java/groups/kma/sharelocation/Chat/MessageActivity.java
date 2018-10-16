package groups.kma.sharelocation.Chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Messages;

public class MessageActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;
    private Toolbar ChatToolbar;

    private TextView userNameTitle;
    private TextView userLastSeen;
    private CircleImageView userChatProfileImage;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    private ImageButton SendMessageButton;
    private ImageButton SelectImageButton;
    private EditText InputMessageText;
    private String messageSenderId;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private static int Gallery_pick =1;
    private StorageReference MessageImageStorageRef;
    private ProgressDialog loadingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageReceiverId = getIntent().getExtras().get("user_id").toString();
        messageReceiverName = getIntent().getExtras().get("user_name").toString();

        rootRef = FirebaseDatabase.getInstance().getReference();
        MessageImageStorageRef = FirebaseStorage.getInstance().getReference().child("Messages_Pictures");
        ChatToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolbar);
        loadingBar = new ProgressDialog(this);

        mAuth= FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        userNameTitle = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        userChatProfileImage = findViewById(R.id.custom_profile_image);

        SendMessageButton = findViewById(R.id.send_message);
        SelectImageButton = findViewById(R.id.select_image);
        InputMessageText = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessageList = findViewById(R.id.chatview);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

        FetchMessages();


        userNameTitle.setText(messageReceiverName);
        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("photoUrl").getValue().toString();
                Picasso.get().load(userThumb).placeholder(R.drawable.acc_box).into(userChatProfileImage);
                if(online.equals("true")){
                    userLastSeen.setText("Online");
                }else{

                    LastSeenTime getTime = new LastSeenTime();
                    long last_seen = Long.parseLong(online);
                    String lastSeenDisplayTime = getTime.getTimeAgo(last_seen,getApplicationContext()).toString();
                    userLastSeen.setText(lastSeenDisplayTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_pick && resultCode == RESULT_OK) {
            loadingBar.setTitle("Gửi ảnh");
            loadingBar.setMessage("Vui lòng đợi trong giây lát...");
            loadingBar.show();
            Uri imageUri = data.getData();
            final String message_sender_ref = "Messages/"+messageSenderId+"/"+messageReceiverId;
            final String message_receiver_ref = "Messages/"+messageReceiverId+"/"+messageSenderId;
            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();
            final String message_push_id = user_message_key.getKey();
            StorageReference filePath = MessageImageStorageRef.child(message_push_id+".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        final String dowload_url = task.getResult().getDownloadUrl().toString();
                        Map messageTextBody = new HashMap();
                        messageTextBody.put("message",dowload_url);
                        messageTextBody.put("seen",false);
                        messageTextBody.put("type","image");
                        messageTextBody.put("time", ServerValue.TIMESTAMP);
                        messageTextBody.put("from",messageSenderId);

                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
                        messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);
                        rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null){
                                    Log.d("Chat_Log",databaseError.getMessage().toString());
                                }
                                InputMessageText.setText("");
                                loadingBar.dismiss();
                            }
                        });



                        Toast.makeText(MessageActivity.this, "Gửi ảnh thành công.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }else {
                        Toast.makeText(MessageActivity.this, "Gửi ảnh thất bại.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });



        }
    }

    private void FetchMessages() {
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
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

    private void SendMessage()
    {

        String messageText = InputMessageText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(MessageActivity.this, "Hãy nhập tin nhắn.", Toast.LENGTH_SHORT).show();
        }else{
            String message_sender_ref = "Messages/"+messageSenderId+"/"+messageReceiverId;
            String message_receiver_ref = "Messages/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

            String message_push_id = user_message_key.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
            messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);
            rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError!=null){
                            Log.d("Chat_Log",databaseError.getMessage().toString());
                        }
                        InputMessageText.setText("");
                }
            });


        }
    }
}
