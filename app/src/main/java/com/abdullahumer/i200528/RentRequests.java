package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RentRequests extends AppCompatActivity {

    ImageView back;
    RecyclerView requestsRV;

    RequestAdapter requestAdapter;
    List<Request> requestsList;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_requests);

        back = findViewById(R.id.back);
        requestsRV = findViewById(R.id.requestsRV);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();

        requestsList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestsList, RentRequests.this, userId);
        requestsRV.setAdapter(requestAdapter);
        RecyclerView.LayoutManager requestsLM = new LinearLayoutManager(RentRequests.this);
        requestsRV.setLayoutManager(requestsLM);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("userRequests").child(userId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ObjectReference requestRefObject = snapshot.getValue(ObjectReference.class);

                mDatabase.child("requests").child(requestRefObject.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {

                            Request requestByRefObject = task.getResult().getValue(Request.class);

                            requestsList.add(requestByRefObject);
                            requestAdapter.notifyDataSetChanged();
                        }

                        else {

                            Log.e("DBErr", "Could not fetch request", task.getException());
                        }
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RentRequests.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}