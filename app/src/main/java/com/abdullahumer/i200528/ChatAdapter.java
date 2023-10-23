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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    List<Chat> chatList;
    Context context;
    String userId;
    String ownerName;

    public ChatAdapter(List<Chat> chatList, Context context, String userId) {

        this.chatList = chatList;
        this.context = context;
        this.userId = userId;
        this.ownerName = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View component = LayoutInflater.from(context).inflate(R.layout.chat_item_component, parent, false);
        return new MyViewHolder(component);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String chatId = chatList.get(position).getChatId();
        String lastMessage = chatList.get(position).getLastMessage();
        String firstMemberId = chatList.get(position).getFirstMemberId();
        String secondMemberId = chatList.get(position).getSecondMemberId();

        String ownerId;

        if (userId.equals(firstMemberId)) {

            ownerId = secondMemberId;
        }

        else {

            ownerId = firstMemberId;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(ownerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    User userObject = task.getResult().getValue(User.class);
                    ownerName = userObject.getFullName();
                    holder.name.setText(ownerName);
                    holder.lastMessage.setText(lastMessage);
                    Picasso.get().load(userObject.getProfilePhotoUrl()).into(holder.picture);
                }

                else {

                    Toast.makeText(context, "Could not fetch customer", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.chatItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DirectMessage.class);
                intent.putExtra("chatId", chatId);
                intent.putExtra("customerId", userId);
                intent.putExtra("ownerId", ownerId);
                intent.putExtra("ownerName", ownerName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return (chatList.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lastMessage, name;
        ImageView picture;
        LinearLayout chatItem;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            lastMessage = itemView.findViewById(R.id.lastMessage);
            name = itemView.findViewById(R.id.name);
            picture = itemView.findViewById(R.id.picture);
            chatItem = itemView.findViewById(R.id.chatItem);
        }
    }
}
