package com.example.chattingapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattingapplication.MessageActivity;
import com.example.chattingapplication.Model.Chat;
import com.example.chattingapplication.Model.User;
import com.example.chattingapplication.R;
import com.example.chattingapplication.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    boolean isChat;

    String theLastMessage;
    String receiver;

    public ChatAdapter(Context mContext, List<User> mUser, boolean isChat){
        this.mContext = mContext;
        this.mUser = mUser;
        this.isChat = isChat;

    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item1,parent,false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override


    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        final User user = mUser.get(position);
        receiver = user.getId();
        holder.username.setText(user.getUsername());

        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if(isChat){
            lastMessage(receiver, holder.user_last_message);
        } else {
            holder.user_last_message.setVisibility(View.GONE);
        }

        // Code to change the view into Profile Activity (on click the profile image)
        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("rID",user.getId());
                intent.putExtra("userName",user.getUsername());
                intent.putExtra("status",user.getStatus());
                intent.putExtra("Email",user.getEmail());
                intent.putExtra("pImage",user.getImageURL());
                mContext.startActivity(intent);
            }
        });

        // Code to change the view into Message Activity (on click the item view)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("rID",user.getId());
                intent.putExtra("userName",user.getUsername());
                intent.putExtra("status",user.getStatus());
                intent.putExtra("Email",user.getEmail());
                intent.putExtra("pImage",user.getImageURL());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView user_last_message;
        public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            user_last_message = itemView.findViewById(R.id.user_last_message);

        }
    }

    private void lastMessage(final String userid, final TextView user_last_message){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Query documentReference = fStore.collection("Messages").document(firebaseUser.getUid())
                .collection(receiver)
                .orderBy("dateTime", Query.Direction.ASCENDING);

        documentReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value) {
                    Chat chat = new Chat(
                            String.valueOf(documentSnapshot.getString("sender")),
                            String.valueOf(documentSnapshot.getString("receiver")),
                            String.valueOf(documentSnapshot.getString("message")),
                            String.valueOf(documentSnapshot.getString("dateTime"))
                    );
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        user_last_message.setText("No Message");
                        break;

                    default:
                        user_last_message.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }
        });
    }
}
