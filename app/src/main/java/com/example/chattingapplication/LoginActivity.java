package com.example.chattingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    //Initialise variable
    Button btnLogin;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Assign variable
        btnLogin = findViewById(R.id.login);

        //Initialise sign in options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("1002803696713-9p1nvr3q9ojfhhbhuib22bohecm5fjcr.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //Initialise sign in client
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this,
                googleSignInOptions);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialise sign in intent
                Intent intent = googleSignInClient.getSignInIntent();
                //start activity for result
                startActivityForResult(intent, 100);
            }
        });
        //Initialise firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //Initialise firestore
        firestore = FirebaseFirestore.getInstance();
        if(firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            LoginActivity.this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check condition
        if(requestCode == 100){
            //Initialise task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.
                    getSignedInAccountFromIntent(data);

            if(signInAccountTask.isSuccessful()){
                //when google sign in successful
                //Initialise string
                //String success = "Google Sign in Successful";
                //displayToast(success);
                try {
                    //initialise sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    if(googleSignInAccount != null){
                        //when sign in account is not null
                        //initialise auth credential
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(),
                                        null);
                        //check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            firebaseUser = task.getResult().getUser();
                                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                                            if(firebaseUser!= null && newuser) {
                                                String userID = firebaseUser.getUid();
                                                String Phone = firebaseUser.getPhoneNumber();
                                                String Name = firebaseUser.getDisplayName();
                                                String email = firebaseUser.getEmail();
                                                Users users = new Users(
                                                        userID,
                                                        Name,
                                                        Phone,
                                                        firebaseUser.getPhotoUrl().toString(),
                                                        email,
                                                        "");

                                                    firestore.collection("Users")
                                                            .document(userID)
                                                            .set(users)  // in case of using map , if not using then write multiple set() to save data
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    //failure
                                                                    Toast.makeText(LoginActivity.this,
                                                                            e.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //success
                                                            //Toast.makeText(LoginActivity.this, "Values Stored",Toast.LENGTH_LONG).show();
                                                            //Redirect to next activity
                                                            startActivity(new Intent(LoginActivity.this, WelcomeScreenActivity.class));
                                                            LoginActivity.this.finish();

                                                        }
                                                    });
                                                }else{
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                LoginActivity.this.finish();
                                                }
                                            displayToast("Login Successful!!");
                                        }else {
                                            displayToast("Auth Failed: "+task.getException()
                                            .getMessage());
                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String success) {
        Toast.makeText(getApplicationContext(),success,Toast.LENGTH_SHORT).show();
    }
}