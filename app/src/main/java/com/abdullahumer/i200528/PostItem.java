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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostItem extends AppCompatActivity {

    ImageView close_post_item;
    LinearLayout uploadImage, uploadVideo;
    EditText name, rate, description;
    Button post;

    String userId, imageUrl, videoUrl;

    String day, month, year;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

//        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        year = String.valueOf(calendar.get(Calendar.YEAR));
        imageUrl = "";
        videoUrl = "";

        close_post_item = findViewById(R.id.image_close_post_item);
        uploadImage = findViewById(R.id.uploadImage);
        uploadVideo = findViewById(R.id.uploadVideo);
        name = findViewById(R.id.name);
        rate = findViewById(R.id.rate);
        description = findViewById(R.id.description);
        post = findViewById(R.id.post);

        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Spinner citySpinner = findViewById(R.id.spinner_city_dropdown);
        ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cities_array, R.layout.spinner_dropdown_item);
        citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(citySpinnerAdapter);

        uploadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 102);
            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 103);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String itemId = mDatabase.child("items").push().getKey();

                Item item = new Item(itemId, userId, name.getText().toString(), Double.parseDouble(rate.getText().toString()), description.getText().toString(), citySpinner.getSelectedItem().toString(), day, month, year, imageUrl, videoUrl);

                mDatabase.child("items").child(itemId).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            mDatabase.child("userPosts").child(userId).child(itemId).child("id").setValue(itemId).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(PostItem.this, "Item Posted", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(PostItem.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    else {

                                        Toast.makeText(PostItem.this, "Post Failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        else {

                            Toast.makeText(PostItem.this, "Post Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        close_post_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PostItem.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK) {
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
                                    Toast.makeText(PostItem.this, imageUrl, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostItem.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        else if (requestCode == 103 && resultCode == RESULT_OK) {
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
                                    Toast.makeText(PostItem.this, videoUrl, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostItem.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
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