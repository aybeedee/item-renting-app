package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ItemDetails extends AppCompatActivity {

    ImageView back_item, itemImg, ownerImg;
    TextView report, itemName, rate, city, date, description, ownerName, ownerRentedCount;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String userId, itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        back_item = findViewById(R.id.image_back_item);
        report = findViewById(R.id.text_report);
        itemImg = findViewById(R.id.itemImg);
        ownerImg = findViewById(R.id.ownerImg);
        itemName = findViewById(R.id.itemName);
        rate = findViewById(R.id.rate);
        city = findViewById(R.id.city);
        date = findViewById(R.id.date);
        description = findViewById(R.id.description);
        ownerName = findViewById(R.id.ownerName);
        ownerRentedCount = findViewById(R.id.ownerRentedCount);

        back_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ItemDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ItemDetails.this, Report.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();

        itemId = getIntent().getStringExtra("itemId");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("items").child(itemId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    Item itemObject = task.getResult().getValue(Item.class);
                    Picasso.get().load(itemObject.getItemImageUrl()).into(itemImg);
                    rate.setText("PKR " + itemObject.getRate().toString());
                    itemName.setText(itemObject.getItemName());
                    city.setText(itemObject.getCity());
                    date.setText(itemObject.getDay() + " " + itemObject.getMonth() + ", " + itemObject.getYear());
                    description.setText(itemObject.getDescription());

                    String ownerId = itemObject.getOwner();

                    mDatabase.child("users").child(ownerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            if (task.isSuccessful()) {

                                User ownerObject = task.getResult().getValue(User.class);
                                ownerName.setText(ownerObject.getFullName());
                                Picasso.get().load(ownerObject.getProfilePhotoUrl()).into(ownerImg);

                                mDatabase.child("userRents").child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {

                                            ownerRentedCount.setText(String.valueOf(snapshot.getChildrenCount()) + " items rented");
                                        }
                                        else {

                                            ownerRentedCount.setText("0 items rented");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        Toast.makeText(ItemDetails.this, "Could not fetch owner's rented items", Toast.LENGTH_LONG).show();
                                    }
                                });

                                city.setText(itemObject.getCity());
                                date.setText(itemObject.getDay() + " " + itemObject.getMonth() + ", " + itemObject.getYear());
                                description.setText(itemObject.getDescription());
                            }

                            else {

                                Toast.makeText(ItemDetails.this, "Could not fetch owner", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                else {

                    Toast.makeText(ItemDetails.this, "Could not fetch item", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}