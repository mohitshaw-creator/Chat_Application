package com.example.chattingapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattingapplication.Adapter.ChatAdapter;
import com.example.chattingapplication.Model.User;
import com.example.chattingapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerview;
    private FirebaseFirestore db;
    private List<User> ChatUser;
    private boolean isChat;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerview = view.findViewById(R.id.recycler_view);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        ChatUser = new ArrayList<>();
        isChat = true;

        readChats();


        return view;
    }

    private void readChats(){
        db = FirebaseFirestore.getInstance(); // initialisation
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //fetch//

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



                                if( !user.getId().equals(firebaseUser.getUid())){
                                    ChatUser.add(user);
                                }
                            }

                            ChatAdapter chatAdapter = new ChatAdapter(getContext(), ChatUser, isChat);
                            recyclerview.setAdapter(chatAdapter);

                        } else {
                            Toast.makeText(getContext(),
                                    "" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}