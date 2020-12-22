package com.example.chattingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;

public class ViewImageActivity extends AppCompatActivity {
    ZoomageView Image;
    ImageButton back_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Image = findViewById(R.id.imageView);
        back_button = findViewById(R.id.btn_back_image);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewImageActivity.this,ProfileActivity.class));
                ViewImageActivity.this.finish();
            }
        });
        Image.setImageBitmap(Common.IMAGE_BITMAP);
    }
}