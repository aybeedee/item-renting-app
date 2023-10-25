package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DirectMessage extends AppCompatActivity {

    ImageView back_chat, takePhoto, insertPhoto, insertVideo, video_call, voice_call;
    RecyclerView messagesRV;
    EditText enter_message_bar;
    TextView name, send;

    MessageAdapter messageAdapter;
    List<Message> messagesList;
    String chatId, customerId, ownerId, ownerFCMToken, text, imageUrl, videoUrl, userName;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        text = "";
        imageUrl = "";
        videoUrl = "";

        back_chat = findViewById(R.id.image_back_chat);
        takePhoto = findViewById(R.id.takePhoto);
        insertPhoto = findViewById(R.id.insertPhoto);
        insertVideo = findViewById(R.id.insertVideo);
        video_call = findViewById(R.id.image_video_call);
        voice_call = findViewById(R.id.image_voice_call);
        messagesRV = findViewById(R.id.messagesRV);
        enter_message_bar = findViewById(R.id.enter_message_bar);
        name = findViewById(R.id.name);
        send = findViewById(R.id.send);

        chatId = getIntent().getStringExtra("chatId");
        customerId = getIntent().getStringExtra("customerId");
        ownerId = getIntent().getStringExtra("ownerId");

        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList, DirectMessage.this, customerId);
        messagesRV.setAdapter(messageAdapter);
        RecyclerView.LayoutManager requestsLM = new LinearLayoutManager(DirectMessage.this);
        messagesRV.setLayoutManager(requestsLM);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(customerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    User userObject = task.getResult().getValue(User.class);
                    userName = userObject.getFullName();
                }

                else {

                    Toast.makeText(DirectMessage.this, "Could not fetch user", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDatabase.child("users").child(ownerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    User userObject = task.getResult().getValue(User.class);
                    ownerFCMToken = userObject.getFcmToken();
                    name.setText(userObject.getFullName());
                }

                else {

                    Toast.makeText(DirectMessage.this, "Could not fetch owner", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDatabase.child("messages").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message messageObject = snapshot.getValue(Message.class);

                messagesList.add(messageObject);
                messageAdapter.notifyDataSetChanged();
                messagesRV.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Message messageObject = snapshot.getValue(Message.class);

                for (int i = 0; i < messagesList.size(); i++) {

                    if (messagesList.get(i).getMessageId().equals(messageObject.getMessageId())) {

                        messagesList.remove(i);
                        messageAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                text = enter_message_bar.getText().toString();

                String messageId = mDatabase.child("messages").push().getKey();
                Long timestamp = System.currentTimeMillis();

                Message message = new Message(messageId, text, imageUrl, videoUrl, timestamp, customerId);

                mDatabase.child("messages").child(messageId).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            String lastMessage;

                            if (text.equals("")) {

                                if (imageUrl.equals("")) {

                                    lastMessage = userName.substring(0, userName.indexOf(' ')) + " sent a video";
                                }

                                else {

                                    lastMessage = userName.substring(0, userName.indexOf(' ')) + " sent a photo";
                                }
                            }

                            else {

                                String lastMessageTemp = (userName.substring(0, userName.indexOf(' ')) + ": " + text);
                                lastMessage = lastMessageTemp.substring(0, Math.min(lastMessageTemp.length(), 36));

                                sendNotification(text);
                            }

                            mDatabase.child("chats").child(chatId).child("lastMessage").setValue(lastMessage);

                            text = "";
                            imageUrl = "";
                            videoUrl = "";

                            enter_message_bar.setText("");

                            Toast.makeText(DirectMessage.this, "Message Sent", Toast.LENGTH_LONG).show();
                        }

                        else {

                            Toast.makeText(DirectMessage.this, "Message could not be sent", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        insertPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 105);
            }
        });

        insertVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 106);
            }
        });

        back_chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                Intent intent = new Intent(DirectMessage.this, MainActivity.class);
                startActivity(intent);

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DirectMessage.this, PhotoCamera.class);
                startActivity(intent);

            }
        });

        video_call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DirectMessage.this, VideoCall.class);
                startActivity(intent);

            }
        });

        voice_call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DirectMessage.this, VoiceCall.class);
                startActivity(intent);

            }
        });
    }

    void sendNotification(String message) {

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", userName);
            notificationObject.put("body", message);

            jsonObject.put("notification", notificationObject);
            jsonObject.put("to", ownerFCMToken);

            callAPI(jsonObject);
        }

        catch (Exception e) {


        }
    }

    void callAPI(JSONObject jsonObject) {

        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        okhttp3.Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAA1PgJxtk:APA91bGmckdwfkEFtT6as6mtfI02hznAu_kxbOOW2hcCgwh6-D7woRG5BRQDwrHO5pSbKvMJf4ByyOXm4ZIiom-YdYp5Bbc6ujAfWj6eDDx-ubMKoWRDSNtDRfgiSUKcc6LfrzML5gVB")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105 && resultCode == RESULT_OK) {
            Uri image = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(customerId + Calendar.getInstance().getTimeInMillis() + "-message-img.png");
            storageRef.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    Toast.makeText(DirectMessage.this, "Image Ready to Send", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DirectMessage.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        else if (requestCode == 106 && resultCode == RESULT_OK) {
            Uri video = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(customerId + Calendar.getInstance().getTimeInMillis() + "-item-video.mp4");
            storageRef.putFile(video)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    videoUrl = uri.toString();
                                    Toast.makeText(DirectMessage.this, "Video Ready to Send", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DirectMessage.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
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