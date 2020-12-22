package com.example.chattingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattingapplication.Model.Chat;
import com.example.chattingapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private Context mContext;
    private String imageurl;
    private List<Chat> mChat;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext,List<Chat> mChat,String imageurl){
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        if(imageurl.equals("default")){
         //image ---
         holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
          Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
       return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);
            //code to set view holder
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.mprofile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
