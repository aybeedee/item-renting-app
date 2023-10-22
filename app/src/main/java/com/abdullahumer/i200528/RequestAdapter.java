package com.abdullahumer.i200528;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    List<Request> requestsList;
    Context context;

    public RequestAdapter(List<Request> requestsList, Context context) {

        this.requestsList = requestsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View component = LayoutInflater.from(context).inflate(R.layout.rent_request_component, parent, false);
        return new MyViewHolder(component);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String itemId = requestsList.get(position).getItemId();
        String customerId = requestsList.get(position).getCustomerId();
        Double hours = requestsList.get(position).getHours();

        holder.hours.setText(hours.toString() + " hrs");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("items").child(itemId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    Item itemObject = task.getResult().getValue(Item.class);
                    holder.itemName.setText(itemObject.getItemName());
                }

                else {

                    Toast.makeText(context, "Could not fetch item", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDatabase.child("users").child(customerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    User userObject = task.getResult().getValue(User.class);
                    holder.customerName.setText(userObject.getFullName());
                }

                else {

                    Toast.makeText(context, "Could not fetch customer", Toast.LENGTH_LONG).show();
                }
            }
        });

//        holder.featuredItem.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                if (!userId.equals(ownerId)) {
//
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//
//                    mDatabase.child("userRecents").child(userId).child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                            if (!snapshot.exists()) {
//
//                                mDatabase.child("userRecents").child(userId).child(itemId).child("id").setValue(itemId).addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if (task.isSuccessful()) {
//
//                                            Log.d("itemAdded", "item added to user recents");
//                                            Intent intent = new Intent(context, ItemDetails.class);
//                                            intent.putExtra("itemId", itemId);
//                                            context.startActivity(intent);
//                                        }
//
//                                        else {
//
//                                            Log.d("DBErr", "failed to post to user recents");
//                                        }
//                                    }
//                                });
//                            }
//
//                            else {
//
//                                Intent intent = new Intent(context, ItemDetails.class);
//                                intent.putExtra("itemId", itemId);
//                                context.startActivity(intent);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                            Log.d("DBErr", "failed to fetch user recents");
//                        }
//                    });
//                }
//
//                else {
//
//                    Intent intent = new Intent(context, EditItem.class);
//                    intent.putExtra("itemId", itemId);
//                    context.startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {

        return (requestsList.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, hours, customerName;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            hours = itemView.findViewById(R.id.hours);
            customerName = itemView.findViewById(R.id.customerName);
        }
    }
}