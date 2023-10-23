package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    Button sign_up;
    TextView login;
    EditText name, email, contact, password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Spinner countrySpinner = findViewById(R.id.spinner_country_dropdown);
        ArrayAdapter<CharSequence> countrySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.countries_array, R.layout.spinner_dropdown_item);
        countrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countrySpinnerAdapter);

        Spinner citySpinner = findViewById(R.id.spinner_city_dropdown);
        ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cities_array, R.layout.spinner_dropdown_item);
        citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(citySpinnerAdapter);

        mAuth = FirebaseAuth.getInstance();

        Log.d("mAuth", mAuth.toString());

        sign_up = findViewById(R.id.button_sign_up);
        login = findViewById(R.id.text_login);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        password = findViewById(R.id.password);

        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.d("email", email.getText().toString());
                Log.d("password", password.getText().toString());

                mAuth.createUserWithEmailAndPassword(

                        email.getText().toString(), password.getText().toString()
                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_LONG).show();

                            String userId = mAuth.getUid().toString();

                            Toast.makeText(SignUp.this, userId, Toast.LENGTH_LONG).show();

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                            User user = new User(userId, name.getText().toString(), email.getText().toString(), contact.getText().toString(), countrySpinner.getSelectedItem().toString(), citySpinner.getSelectedItem().toString(),"", "");

                            mDatabase.child("users").child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Log.d("databaseUsers", "users");

                                    Intent intent = new Intent(SignUp.this, EditProfile.class);
                                    startActivity(intent);

                                    finish();
                                }
                            });
                        }
                    }
                ).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SignUp.this, "Sign Up Failed"+e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        if(mAuth.getUid() != null) {

            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}