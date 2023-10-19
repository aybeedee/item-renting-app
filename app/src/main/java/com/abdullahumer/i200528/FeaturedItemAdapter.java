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

import com.squareup.picasso.Picasso;

import java.util.List;

public class FeaturedItemAdapter extends RecyclerView.Adapter<FeaturedItemAdapter.MyViewHolder> {

    List<Item> featuredList;
    Context context;

    public FeaturedItemAdapter(List<Item> featuredList, Context context) {
        this.featuredList = featuredList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View component = LayoutInflater.from(context).inflate(R.layout.featured_item_component, parent, false);
        return new MyViewHolder(component);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

//        Log.d("itemName-Adapter", featuredList.get(position).getItemName());

        holder.name.setText(featuredList.get(position).getItemName());
        holder.rate.setText("PKR "+featuredList.get(position).getRate().toString()+"/hr");
        holder.city.setText(featuredList.get(position).getCity());
        holder.date.setText(featuredList.get(position).getDay()+ " " + featuredList.get(position).getMonth());
        Picasso.get().load(featuredList.get(position).getItemImageUrl()).into(holder.img);

        holder.featuredItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDetails.class);
                intent.putExtra("itemId", featuredList.get(position).getItemId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return (featuredList.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, rate, city, date;
        ImageView img;
        CardView featuredItem;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.name);
            rate = itemView.findViewById(R.id.rate);
            city = itemView.findViewById(R.id.city);
            date = itemView.findViewById(R.id.date);
            img = itemView.findViewById(R.id.img);
            featuredItem = itemView.findViewById(R.id.featuredItem);
        }
    }
}
