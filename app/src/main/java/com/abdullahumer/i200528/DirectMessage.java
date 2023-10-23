package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DirectMessage extends AppCompatActivity {

    ImageView back_chat, take_picture, video_call, voice_call;
    ScrollView chat_messages;
    RecyclerView messagesRV;
    EditText enter_message_bar;
    TextView name, send;

    MessageAdapter messageAdapter;
    List<Message> messagesList;
    String chatId, customerId, ownerId, text, imageUrl, videoUrl, userName;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        text = "";
        imageUrl = "";
        videoUrl = "";

        back_chat = findViewById(R.id.image_back_chat);
        take_picture = findViewById(R.id.image_take_picture);
        video_call = findViewById(R.id.image_video_call);
        voice_call = findViewById(R.id.image_voice_call);
        chat_messages = findViewById(R.id.scroll_chat_messages);
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

        back_chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DirectMessage.this, MainActivity.class);
                startActivity(intent);

            }
        });

        take_picture.setOnClickListener(new View.OnClickListener() {

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

        chat_messages.postDelayed(new Runnable() {

            @Override
            public void run() {

                chat_messages.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },0);
    }
}