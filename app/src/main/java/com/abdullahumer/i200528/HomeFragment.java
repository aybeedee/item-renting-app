package com.abdullahumer.i200528;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView name, viewRequests;
    RecyclerView featuredRV;
    RecyclerView yourRV;
    RecyclerView recentRV;

    FeaturedItemAdapter featuredAdapter;
    FeaturedItemAdapter yourAdapter;
    GridAdapter recentAdapter;

    List<Item> featuredList;
    List<Item> yourList;
    List<Item> recentList;

    String userId;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        Log.i("apple", "Inside HomeFrag");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        name = view.findViewById(R.id.name);
        viewRequests = view.findViewById(R.id.viewRequests);
        featuredRV = view.findViewById(R.id.featuredRV);
        yourRV = view.findViewById(R.id.yourRV);
        recentRV = view.findViewById(R.id.recentRV);

        viewRequests.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), RentRequests.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {

                    Log.e("firebase", "Error getting data", task.getException());
                }

                else {
                    User userObject = task.getResult().getValue(User.class);
                    name.setText(userObject.getFullName());
                }
            }
        });

        featuredList = new ArrayList<>();
        featuredAdapter = new FeaturedItemAdapter(featuredList, getContext(), userId);
        featuredRV.setAdapter(featuredAdapter);
        RecyclerView.LayoutManager featuredLM = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        featuredRV.setLayoutManager(featuredLM);

        yourList = new ArrayList<>();
        yourAdapter = new FeaturedItemAdapter(yourList, getContext(), userId);
        yourRV.setAdapter(yourAdapter);
        RecyclerView.LayoutManager yourLM = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        yourRV.setLayoutManager(yourLM);

        recentList = new ArrayList<>();
        recentAdapter = new GridAdapter(recentList, getContext(), "home");
        recentRV.setAdapter(recentAdapter);
        RecyclerView.LayoutManager recentLM = new GridLayoutManager(getContext(), 2);
        recentRV.setLayoutManager(recentLM);

        // for featured items and your items
        mDatabase.child("items").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Item itemObject = snapshot.getValue(Item.class);

                if (!itemObject.getOwner().equals(userId)) {
                    Log.d("itemName-Featured", itemObject.getItemName());
                    featuredList.add(itemObject);
                    featuredAdapter.notifyDataSetChanged();
                }

                else {

                    Log.d("itemName-Your", itemObject.getItemName());
                    yourList.add(itemObject);
                    yourAdapter.notifyDataSetChanged();
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

        // for recent items
        mDatabase.child("userRecents").child(userId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ObjectReference itemRefObject = snapshot.getValue(ObjectReference.class);

                mDatabase.child("items").child(itemRefObject.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {

                            Item itemByRefObject = task.getResult().getValue(Item.class);

                            recentList.add(itemByRefObject);
                            recentAdapter.notifyDataSetChanged();
                        }

                        else {

                            Log.e("DBErr", "Could not fetch item", task.getException());
                        }
                    }
                });
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

        return view;
    }
}