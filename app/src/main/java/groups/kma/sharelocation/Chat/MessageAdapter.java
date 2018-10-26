package groups.kma.sharelocation.Chat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Messages;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseRefrence;

    public MessageAdapter(List<Messages> userMessageList){
        this.userMessageList = userMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_user,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        String message_sender_id = mAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(position);
        String fromUserId= messages.getFrom();
        String fromMessageType = messages.getType();
        UsersDatabaseRefrence = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        UsersDatabaseRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("userName").getValue().toString();
                String userImage = dataSnapshot.child("photoUrl").getValue().toString();
                Picasso.get().load(userImage).placeholder(R.drawable.icon_online).into(holder.userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(fromMessageType.equals("text")){
            holder.messageImg.setVisibility(View.INVISIBLE);
            if(fromUserId.equals(null)||fromUserId.equals("")){
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            }else if(fromUserId.equals(message_sender_id)){
                holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            }else{
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            }
            holder.messageText.setText(messages.getMessage());
        }else{
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageText.setPadding(0,0,0,0);
            Picasso.get().load(messages.getMessage()).placeholder(R.drawable.icon_online).into(holder.messageImg);
        }


    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText;
        public CircleImageView userProfileImage;
        public ImageView messageImg;

        public MessageViewHolder(View view){
            super(view);
            messageText = view.findViewById(R.id.message_text);
            userProfileImage = view.findViewById(R.id.message_profile_image);
            messageImg = view.findViewById(R.id.message_image_view);
        }
    }

}
