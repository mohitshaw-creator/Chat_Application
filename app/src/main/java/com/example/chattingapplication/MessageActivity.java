package com.example.chattingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattingapplication.Adapter.MessageAdapter;
import com.example.chattingapplication.Model.Chat;
import com.example.chattingapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    private static final String TAG = "LOG ERROR";
    CircleImageView profile_image;
    TextView username;
    ImageButton btn_send;
    ImageButton back_button;
    EditText text_send;
    Intent intent;
    // declaration
    private FirebaseFirestore db;
    FirebaseUser firebaseUser;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    String receiverid;
    String receiverProfile;
    String receiverStatus;
    String receiverEmail;
    String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Custom actionbar (toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        //Button to send message
        btn_send = findViewById(R.id.btn_send);
        //back button
        back_button = findViewById(R.id.back_button);
        //text to send as message
        text_send = findViewById(R.id.text_send);
        intent = getIntent();
        receiverid = intent.getStringExtra("rID");
        receiverProfile = intent.getStringExtra("pImage");
        receiverStatus = intent.getStringExtra("status");
        receiverEmail = intent.getStringExtra("Email");
        receiverName = intent.getStringExtra("userName");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class));
                MessageActivity.this.finish();
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MessageActivity.this, UserProfileActivity.class);
                intent.putExtra("rID",receiverid);
                intent.putExtra("userName",receiverName);
                intent.putExtra("status",receiverStatus);
                intent.putExtra("Email",receiverEmail);
                intent.putExtra("pImage",receiverProfile);
                startActivity(intent);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    //code to send message
                    sendMessage(firebaseUser.getUid(), receiverid, msg, getCurrentDate());
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message !", Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });

        //get the current user object as user
        db = FirebaseFirestore.getInstance(); // initialisation

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for(DocumentSnapshot document : task.getResult()) {
                                User user = new User(
                                        String.valueOf(document.getString("userID")),
                                        String.valueOf(document.getString("userName")),
                                        String.valueOf(document.getString("userPhone")),
                                        String.valueOf(document.getString("status")),
                                        String.valueOf(document.getString("email")),
                                        String.valueOf(document.getString("imageProfile")));

                                if (user.getId().equals(receiverid)) {
                                    username.setText(user.getUsername());
                                    if (user.getImageURL().equals("default")) {
                                        profile_image.setImageResource(R.mipmap.ic_launcher);
                                    } else {
                                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                                    }
                                    readMessage(firebaseUser.getUid(), receiverid, user.getImageURL());
                                }
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });




    }

    private void readMessage(final String myid, final String userid, final String imageurl) {

        mChat = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Messages")
                .document(firebaseUser.getUid())
                .collection(receiverid)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mChat.clear();
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        for (DocumentSnapshot documentSnapshot : value) {
                            Chat chat = new Chat(
                                    String.valueOf(documentSnapshot.getString("sender")),
                                    String.valueOf(documentSnapshot.getString("receiver")),
                                    String.valueOf(documentSnapshot.getString("message")),
                                    String.valueOf(documentSnapshot.getString("dateTime"))
                            );
                            if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                                mChat.add(chat);
                            }

                            messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                            recyclerView.setAdapter(messageAdapter);
                        }
                    }
                });


        //setting up Message adapter
        //messageAdapter = new MessageAdapter(MessageActivity.this,mChat,user.getImageURL());
        // recyclerView.setAdapter(messageAdapter);

    }

    private void sendMessage(String sender, String receiver, String message, String currentTime) {
        db = FirebaseFirestore.getInstance();
        // creating map variable to store data in key-value pair
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("dateTime",currentTime);
        db.collection("Messages")
                .document(sender)
                .collection(receiver)
                .add(hashMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failure
                Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //Toast.makeText(getApplicationContext(),"success", Toast.LENGTH_LONG).show();
            }
        });
        db.collection("Messages")
                .document(receiver)
                .collection(sender)
                .add(hashMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failure
                Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            }
        });

    }

    public String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SS a");
        String currentTime = df.format(currentDateTime.getTime());

        return today+", "+currentTime;
    }
    public void readLastMessage(){
        mChat = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Messages")
                .document(firebaseUser.getUid())
                .collection(receiverid)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                });
    }

}