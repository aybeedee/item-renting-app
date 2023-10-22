package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditItem extends AppCompatActivity {

    ImageView close_post_item;
    LinearLayout uploadImage, uploadVideo;
    EditText name, rate, description;
    TextView save;
    Button remove;

    String userId, imageUrl, videoUrl, itemId;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        imageUrl = "";
        videoUrl = "";

        close_post_item = findViewById(R.id.image_close_post_item);
        uploadImage = findViewById(R.id.uploadImage);
        uploadVideo = findViewById(R.id.uploadVideo);
        name = findViewById(R.id.name);
        rate = findViewById(R.id.rate);
        description = findViewById(R.id.description);
        save = findViewById(R.id.save);
        remove = findViewById(R.id.remove);

        itemId = getIntent().getStringExtra("itemId");

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Spinner citySpinner = findViewById(R.id.spinner_city_dropdown);
        ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cities_array, R.layout.spinner_dropdown_item);
        citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(citySpinnerAdapter);

        mDatabase.child("items").child(itemId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {

                    Log.e("DBErr", "Could not fetch item", task.getException());
                }
                else {

                    Item itemObject = task.getResult().getValue(Item.class);

                    name.setText(itemObject.getItemName());
                    rate.setText(itemObject.getRate().toString());
                    description.setText(itemObject.getDescription());
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // remove item from all recents
                mDatabase.child("userRecents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {

                                ItemReference itemRefObject = itemSnapshot.getValue(ItemReference.class);
                                String indexedItemId = itemRefObject.getId();

                                if (indexedItemId.equals(itemId)) {

                                    itemSnapshot.getRef().removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // remove item from all rents
                mDatabase.child("userRents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {

                                ItemReference itemRefObject = itemSnapshot.getValue(ItemReference.class);
                                String indexedItemId = itemRefObject.getId();

                                if (indexedItemId.equals(itemId)) {

                                    itemSnapshot.getRef().removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // remove item from all posts
                mDatabase.child("userPosts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {

                                ItemReference itemRefObject = itemSnapshot.getValue(ItemReference.class);
                                String indexedItemId = itemRefObject.getId();

                                if (indexedItemId.equals(itemId)) {

                                    itemSnapshot.getRef().removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // remove from items
                mDatabase.child("items").child(itemId).removeValue();

                Toast.makeText(EditItem.this, "Item Removed", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(EditItem.this, MainActivity.class);
                startActivity(intent);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 103);
            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 104);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mDatabase.child("items").child(itemId).child("itemName").setValue(name.getText().toString());
                mDatabase.child("items").child(itemId).child("rate").setValue(Double.parseDouble(rate.getText().toString()));
                mDatabase.child("items").child(itemId).child("description").setValue(description.getText().toString());
                mDatabase.child("items").child(itemId).child("itemImageUrl").setValue(imageUrl);
                mDatabase.child("items").child(itemId).child("itemVideoUrl").setValue(videoUrl);
                mDatabase.child("items").child(itemId).child("city").setValue(citySpinner.getSelectedItem().toString());

                Toast.makeText(EditItem.this, "Item Updated", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(EditItem.this, MainActivity.class);
                startActivity(intent);
            }
        });

        close_post_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditItem.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == RESULT_OK) {
            Uri image = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(mAuth.getUid() + Calendar.getInstance().getTimeInMillis() + "-item-img.png");
            storageRef.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    Toast.makeText(EditItem.this, imageUrl, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditItem.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        else if (requestCode == 104 && resultCode == RESULT_OK) {
            Uri video = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(mAuth.getUid() + Calendar.getInstance().getTimeInMillis() + "-item-video.mp4");
            storageRef.putFile(video)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    videoUrl = uri.toString();
                                    Toast.makeText(EditItem.this, videoUrl, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditItem.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}