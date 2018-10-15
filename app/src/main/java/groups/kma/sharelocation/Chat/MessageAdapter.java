package groups.kma.sharelocation.Chat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Messages;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;

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
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String message_sender_id = mAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(position);
        String fromUserId= messages.getFrom();
        if(fromUserId.equals(null)||fromUserId.equals("")){
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setGravity(Gravity.LEFT);
        }else if(fromUserId.equals(message_sender_id)){
            holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setGravity(Gravity.RIGHT);
        }else{
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setGravity(Gravity.LEFT);
        }

        holder.messageText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText;
        public CircleImageView userProfileImage;

        public MessageViewHolder(View view){
            super(view);
            messageText = view.findViewById(R.id.message_text);
            // userProfileImage = view.findViewById(R.id.message_profile_image);
        }
    }

}
