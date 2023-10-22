package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RentItem extends AppCompatActivity {

    ImageView itemImg, back;
    TextView rate, itemName, city, date, hours, totalRent;
    Button send, calculate;

    String itemId, customerId, ownerId;
    Double rateValue, hoursValue;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item);

        itemImg = findViewById(R.id.itemImg);
        rate = findViewById(R.id.rate);
        itemName = findViewById(R.id.itemName);
        city = findViewById(R.id.city);
        date = findViewById(R.id.date);
        hours = findViewById(R.id.hours);
        totalRent = findViewById(R.id.totalRent);
        send = findViewById(R.id.send);
        calculate = findViewById(R.id.calculate);
        back = findViewById(R.id.back);

        itemId = getIntent().getStringExtra("itemId");
        customerId = getIntent().getStringExtra("customerId");
        ownerId = getIntent().getStringExtra("ownerId");
        rateValue = Double.parseDouble(getIntent().getStringExtra("rate"));

        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(itemImg);
        rate.setText("PKR " + getIntent().getStringExtra("rate"));
        itemName.setText(getIntent().getStringExtra("itemName"));
        city.setText(getIntent().getStringExtra("city"));
        date.setText(getIntent().getStringExtra("date"));
        hours.setText(getIntent().getStringExtra("hours"));
        totalRent.setText("PKR 0.0");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RentItem.this, MainActivity.class);
                startActivity(intent);
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hoursValue = Double.parseDouble(hours.getText().toString());
                totalRent.setText("PKR " + (hoursValue * rateValue));
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String requestID = mDatabase.child("requests").push().getKey();

                Request request = new Request(requestID, itemId, customerId, hoursValue);

                mDatabase.child("requests").child(requestID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            mDatabase.child("userRequests").child(ownerId).child(requestID).child("id").setValue(requestID).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(RentItem.this, "Rent Request Sent", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(RentItem.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    else {

                                        Toast.makeText(RentItem.this, "Rent Request Failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        else {

                            Toast.makeText(RentItem.this, "Rent Request Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}