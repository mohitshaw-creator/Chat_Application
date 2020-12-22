package com.example.chattingapplication;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;


public class UserProfileActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    ImageView userProfile, user_profile_message;
    TextView status,email;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Custom actionbar (toolbar);
        toolbar = findViewById(R.id.toolbar);
        userProfile= findViewById(R.id.user_profile);
        status = findViewById(R.id.status_user);
        email = findViewById(R.id.email_user);
        user_profile_message = findViewById(R.id.user_profile_message);




        intent = getIntent();
        final String receiverUserId = intent.getStringExtra("rID");
        final String receiverProfile = intent.getStringExtra("pImage");
        final String receiverStatus = intent.getStringExtra("status");
        final String receiverEmail = intent.getStringExtra("Email");
        final String receiverName = intent.getStringExtra("userName");


        if (receiverUserId!=null) {
            toolbar.setTitle(receiverName);
            if (receiverProfile.equals("default")) {
                userProfile.setImageResource(R.drawable.ic_baseline_person_outline_24);
            } else {
                Glide.with(this).load(receiverProfile).into(userProfile);
            }
        }
        status.setText(receiverStatus);
        email.setText(receiverEmail);

        user_profile_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(UserProfileActivity.this, MessageActivity.class);
                intent.putExtra("rID",receiverUserId);
                intent.putExtra("userName",receiverName);
                intent.putExtra("status",receiverStatus);
                intent.putExtra("Email",receiverEmail);
                intent.putExtra("pImage",receiverProfile);
                startActivity(intent);
                UserProfileActivity.this.finish();
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userProfile.invalidate();
                Drawable dr = userProfile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this, userProfile, "image");
                Intent intent = new Intent(UserProfileActivity.this, ViewImageActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

    }
}