package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResults extends AppCompatActivity {

    ImageView back_search;
    TextView searchQuery;
    RecyclerView searchRV;

    GridAdapter searchAdapter;

    String searchInput;
    List<Item> searchList;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Spinner filterSpinner = findViewById(R.id.spinner_filter_dropdown);
        ArrayAdapter<CharSequence> filterSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filters_array, R.layout.filter_spinner_dropdown_item);
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterSpinnerAdapter);

        back_search = findViewById(R.id.image_back_search);
        searchQuery = findViewById(R.id.searchQuery);
        searchRV = findViewById(R.id.searchRV);

        searchInput = getIntent().getStringExtra("searchInput");
        searchQuery.setText("\"" + searchInput + "\"");

        searchList = new ArrayList<>();
        searchAdapter = new GridAdapter(searchList, SearchResults.this, "search");
        searchRV.setAdapter(searchAdapter);
        RecyclerView.LayoutManager searchLM = new GridLayoutManager(SearchResults.this, 2);
        searchRV.setLayoutManager(searchLM);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // for featured items and your items
        mDatabase.child("items").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Item itemObject = snapshot.getValue(Item.class);

                if (itemObject.getItemName().toLowerCase().contains(searchInput.toLowerCase())) {
                    searchList.add(itemObject);
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SearchResults.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}