package com.abdullahumer.i200528;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    List<Item> gridList;
    Context context;

    public GridAdapter(List<Item> featuredList, Context context) {

        this.gridList = featuredList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View component = LayoutInflater.from(context).inflate(R.layout.grid_item_component, parent, false);
        return new MyViewHolder(component);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String itemId = gridList.get(position).getItemId();

        holder.name.setText(gridList.get(position).getItemName());
        holder.rate.setText("PKR " + gridList.get(position).getRate().toString() + "/hr");
        holder.city.setText(gridList.get(position).getCity());
        holder.date.setText(gridList.get(position).getDay()+ " " + gridList.get(position).getMonth());
        Picasso.get().load(gridList.get(position).getItemImageUrl()).into(holder.img);

        holder.featuredItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ItemDetails.class);
                intent.putExtra("itemId", itemId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return (gridList.size());
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
