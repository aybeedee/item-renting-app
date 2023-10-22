package com.abdullahumer.i200528;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView edit_nav, profilePhoto, coverPhoto;
    TextView logout, name, city, itemsPosted, itemsRented;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String userId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        edit_nav = view.findViewById(R.id.image_edit_nav);
        logout = view.findViewById(R.id.logout);
        profilePhoto = view.findViewById(R.id.profilePhoto);
        coverPhoto = view.findViewById(R.id.coverPhoto);
        name = view.findViewById(R.id.name);
        city = view.findViewById(R.id.city);
        itemsPosted = view.findViewById(R.id.itemsPosted);
        itemsRented = view.findViewById(R.id.itemsRented);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    User userObject = task.getResult().getValue(User.class);

                    Picasso.get().load(userObject.getCoverPhotoUrl()).into(coverPhoto);
                    Picasso.get().load(userObject.getProfilePhotoUrl()).into(profilePhoto);
                    name.setText(userObject.getFullName());
                    city.setText(userObject.getCity());

                    mDatabase.child("userPosts").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                itemsPosted.setText(String.valueOf(snapshot.getChildrenCount()) + " items posted");
                            }

                            else {

                                itemsPosted.setText("0 items posted");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(getContext(), "Could not fetch user's posted items", Toast.LENGTH_LONG).show();
                        }
                    });

                    mDatabase.child("userRents").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                itemsRented.setText(String.valueOf(snapshot.getChildrenCount()) + " items rented");
                            }

                            else {

                                itemsRented.setText("0 items rented");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(getContext(), "Could not fetch user's rented items", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                else {

                    Log.e("DBErr", "Could not fetch user data", task.getException());
                }
            }
        });

        edit_nav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();

                mAuth.signOut();

                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        return view;
    }
}