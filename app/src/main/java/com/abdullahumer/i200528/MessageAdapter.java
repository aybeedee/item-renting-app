package com.abdullahumer.i200528;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Message> messageList;
    Context context;
    String userId;

    public MessageAdapter(List<Message> messageList, Context context, String userId) {

        this.messageList = messageList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 0) {

            return new SenderTextVH(LayoutInflater.from(context).inflate(R.layout.message_sent_component, parent, false));
        }

        else if (viewType == 1) {

            return new SenderImgVH(LayoutInflater.from(context).inflate(R.layout.message_sent_img_component, parent, false));
        }

        else if (viewType == 2) {

            return new SenderVidVH(LayoutInflater.from(context).inflate(R.layout.message_sent_vid_component, parent, false));
        }

        else if (viewType == 3) {

            return new ReceiverTextVH(LayoutInflater.from(context).inflate(R.layout.message_received_component, parent, false));
        }

        else if (viewType == 4) {

            return new ReceiverImgVH(LayoutInflater.from(context).inflate(R.layout.message_received_img_component, parent, false));
        }

        return new ReceiverVidVH(LayoutInflater.from(context).inflate(R.layout.message_received_video_component, parent, false));
    }

    @Override
    public int getItemViewType(int position) {

        if (messageList.get(position).getSenderId().equals(userId)) {

            if (messageList.get(position).getText().equals("")) {

                if (messageList.get(position).getImageUrl().equals("")) {

                    return 2;
                }

                else {

                    return 1;
                }
            }

            else {

                return 0;
            }
        }

        else {

            if (messageList.get(position).getText().equals("")) {

                if (messageList.get(position).getImageUrl().equals("")) {

                    return 5;
                }

                else {

                    return 4;
                }
            }

            else {

                return 3;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder.getItemViewType() == 0) {

            SenderTextVH senderTextVH = (SenderTextVH) holder;

            String messageId = messageList.get(position).getMessageId();
            String text = messageList.get(position).getText();
            Long timestamp = messageList.get(position).getTimestamp();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(timestamp);
            String formattedTime = simpleDateFormat.format(date);

            senderTextVH.text.setText(text);
            senderTextVH.time.setText(formattedTime);

            senderTextVH.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    // remove from messages
                    mDatabase.child("messages").child(messageId).removeValue();

                    Toast.makeText(context, "Message Deleted", Toast.LENGTH_LONG).show();
                }
            });
        }

        else if (holder.getItemViewType() == 1) {

            SenderImgVH senderImgVH = (SenderImgVH) holder;

            String imageUrl = messageList.get(position).getImageUrl();
            Long timestamp = messageList.get(position).getTimestamp();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(timestamp);
            String formattedTime = simpleDateFormat.format(date);

            Picasso.get().load(imageUrl).into(senderImgVH.img);
            senderImgVH.time.setText(formattedTime);
        }

        else if (holder.getItemViewType() == 3) {

            ReceiverTextVH receiverTextVH = (ReceiverTextVH) holder;

            String senderId = messageList.get(position).getSenderId();
            String text = messageList.get(position).getText();
            Long timestamp = messageList.get(position).getTimestamp();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(timestamp);
            String formattedTime = simpleDateFormat.format(date);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(senderId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if (task.isSuccessful()) {

                        User userObject = task.getResult().getValue(User.class);
                        Picasso.get().load(userObject.getProfilePhotoUrl()).into(receiverTextVH.profilePhoto);
                    }

                    else {

                        Toast.makeText(context, "Could not fetch user", Toast.LENGTH_LONG).show();
                    }
                }
            });

            receiverTextVH.text.setText(text);
            receiverTextVH.time.setText(formattedTime);
        }

        else if (holder.getItemViewType() == 4) {

            ReceiverImgVH receiverImgVH = (ReceiverImgVH) holder;

            String senderId = messageList.get(position).getSenderId();
            String imageUrl = messageList.get(position).getImageUrl();
            Long timestamp = messageList.get(position).getTimestamp();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(timestamp);
            String formattedTime = simpleDateFormat.format(date);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(senderId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if (task.isSuccessful()) {

                        User userObject = task.getResult().getValue(User.class);
                        Picasso.get().load(userObject.getProfilePhotoUrl()).into(receiverImgVH.profilePhoto);
                    }

                    else {

                        Toast.makeText(context, "Could not fetch user", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Picasso.get().load(imageUrl).into(receiverImgVH.img);
            receiverImgVH.time.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {

        return (messageList.size());
    }

    // viewType 0
    public class SenderTextVH extends RecyclerView.ViewHolder {

        TextView time, text, delete;

        public SenderTextVH(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            text = itemView.findViewById(R.id.text);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    // viewType 1
    public class SenderImgVH extends RecyclerView.ViewHolder {

        TextView time;
        ImageView img;

        public SenderImgVH(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            img = itemView.findViewById(R.id.img);
        }
    }

    // viewType 2
    public class SenderVidVH extends RecyclerView.ViewHolder {

        TextView time;
        VideoView vid;

        public SenderVidVH(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            vid = itemView.findViewById(R.id.vid);
        }
    }

    // viewType 3
    public class ReceiverTextVH extends RecyclerView.ViewHolder {

        TextView time, text;
        ImageView profilePhoto;

        public ReceiverTextVH(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            text = itemView.findViewById(R.id.text);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
        }
    }

    // viewType 4
    public class ReceiverImgVH extends RecyclerView.ViewHolder {

        TextView time;
        ImageView profilePhoto, img;

        public ReceiverImgVH(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            img = itemView.findViewById(R.id.img);
        }
    }

    // viewType 5
    public class ReceiverVidVH extends RecyclerView.ViewHolder {

        TextView time;
        ImageView profilePhoto;
        VideoView vid;

        public ReceiverVidVH(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            vid = itemView.findViewById(R.id.vid);
        }
    }
}