package com.abdullahumer.i200528;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.abdullahumer.i200528.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

//    BottomNavigationView navb;
    ActivityMainBinding mainBinding;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//        navb = findViewById(R.id.bottomNavigationView);
//
//        navb.setOnItemSelectedListener(item -> {
//            switch (item.getItemId())
//            {
//                case R.id.home_bottomnav:
//                    replaceFrag(new HomeFragment());
//                    break;
//
//                case R.id.search_bottomnav:
//                    replaceFrag(new SearchFragment());
//                    break;
//
//                case R.id.add_bottomnav:
//                    Intent intent = new Intent(MainActivity.this, PostItem.class);
//                    startActivity(intent);
//                    break;
//
//                case R.id.chat_bottomnav:
//                    replaceFrag(new ChatFragment());
//                    break;
//
//                case R.id.profile_bottomnav:
//                    replaceFrag(new ProfileFragment());
//                    break;
//            }
//
//            return true;
//        });

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        replaceFrag(new HomeFragment());

        mainBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.home_bottomnav:
                    replaceFrag(new HomeFragment());
                    break;

                case R.id.search_bottomnav:
                    replaceFrag(new SearchFragment());
                    break;

                case R.id.add_bottomnav:
                    Intent intent = new Intent(MainActivity.this, PostItem.class);
                    startActivity(intent);
                    break;

                case R.id.chat_bottomnav:
                    replaceFrag(new ChatFragment());
                    break;

                case R.id.profile_bottomnav:
                    replaceFrag(new ProfileFragment());
                    break;
            }

            return true;
        });

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) {

            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        getFCMToken();
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

            @Override
            public void onComplete(@NonNull Task<String> task) {

                if (task.isSuccessful()) {

                    String token = task.getResult();
                    Log.d("Token", token);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(mAuth.getUid().toString()).child("fcmToken").setValue(token);
                }
            }
        });
    }

    private void replaceFrag(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}