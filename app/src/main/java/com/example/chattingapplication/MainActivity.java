package com.example.chattingapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chattingapplication.Fragments.ChatFragment;
import com.example.chattingapplication.Fragments.UsersFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialise firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //setting up custom Actionbar from bar_layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("E-Chat");


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // set up the Fragments in main_activity
        viewPagerAdapter.AddFragment(new ChatFragment(),"Chats");
        viewPagerAdapter.AddFragment(new UsersFragment(),"Users");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //MENU SET UP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                showDialogLogOut();
                break;

            case R.id.profile:
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        return true;
    }

    //Fragments setup
    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void AddFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void showDialogLogOut(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to Log out?");
        builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //Initialise sign in client
                googleSignInClient = GoogleSignIn.getClient(MainActivity.this
                        , GoogleSignInOptions.DEFAULT_SIGN_IN);
                //Sign out from google
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Check condition
                        if(task.isSuccessful()){
                            //When task is successful
                            //Sign out from firebase
                            firebaseAuth.signOut();
                            //Display toast
                            Toast.makeText(getApplicationContext()
                                    , "Logout Successfully", Toast.LENGTH_SHORT).show();
                            MainActivity.this.finish();
                            intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}