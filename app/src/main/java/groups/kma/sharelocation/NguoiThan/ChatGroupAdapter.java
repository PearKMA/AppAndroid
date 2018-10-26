package groups.kma.sharelocation.NguoiThan;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.Chat.MessageAdapter;
import groups.kma.sharelocation.R;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ChatGroupViewHolder> {
    private List<MessageGroup> messageGroups;
    private FirebaseAuth mAuth;
    private DatabaseReference mRoot;

    public ChatGroupAdapter(List<MessageGroup> messageGroups) {
        this.messageGroups = messageGroups;
    }

    @NonNull
    @Override
    public ChatGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_group,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new ChatGroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatGroupViewHolder holder, int position) {
        String message_sender_id = mAuth.getCurrentUser().getUid();
        MessageGroup messageGroup = messageGroups.get(position);
        String fromId = messageGroup.getFromId();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(fromId);
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // truyền hình ảnh
                String userImage = dataSnapshot.child("photoUrl").getValue().toString();
                Picasso.get().load(userImage).placeholder(R.mipmap.ic_launcher).into(holder.userProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.name.setText(messageGroup.getName());
        holder.messageText.setText(messageGroup.getMessage());
        holder.datechat.setText(messageGroup.getTime()+" - "+messageGroup.getDate());


    }

    @Override
    public int getItemCount() {
        return messageGroups.size() ;
    }

    public class ChatGroupViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText,datechat;
        public CircleImageView userProfileImage;
        public TextView name;

        public ChatGroupViewHolder(View view){
            super(view);
            messageText = view.findViewById(R.id.message_text);
            name = view.findViewById(R.id.namechat);
            userProfileImage = view.findViewById(R.id.message_profile_image);
            datechat = view.findViewById(R.id.datechat);
        }
    }
}
