package com.example.chattingapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattingapplication.Model.User;
import com.example.chattingapplication.R;
import com.example.chattingapplication.UserProfileActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;


    public UserAdapter(Context mContext,List<User> mUser){
        this.mContext = mContext;
        this.mUser = mUser;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);

       return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUser.get(position);
        holder.username.setText(user.getUsername());

        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("rID",user.getId());
                intent.putExtra("userName",user.getUsername());
                intent.putExtra("status",user.getStatus());
                intent.putExtra("Email",user.getEmail());
                intent.putExtra("pImage",user.getImageURL());
                mContext.startActivity(intent);
            }
        });
        holder.user_status.setText(user.getStatus());
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public TextView user_status;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            user_status = itemView.findViewById(R.id.user_status);

        }
    }


}
