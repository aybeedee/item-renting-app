package com.abdullahumer.i200528;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView forgot_password, sign_up;
    EditText email, password;
    Button login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        forgot_password = findViewById(R.id.text_forgot_password);
        sign_up = findViewById(R.id.text_sign_up);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.button_login);

        forgot_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String emailInput = email.getText().toString();
                String passwordInput = password.getText().toString();

                if ((emailInput.trim().length() != 0) && (passwordInput.trim().length() != 0)) {

                    mAuth.signInWithEmailAndPassword(

                            email.getText().toString(), password.getText().toString()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Log In Successful", Toast.LENGTH_LONG).show();
                                Toast.makeText(Login.this, mAuth.getUid().toString(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            }

                            else {

                                Toast.makeText(Login.this, "Log In Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Login.this, "Log In Failed"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                else {

                    Log.d("loginClickElse", emailInput);
                    Toast.makeText(Login.this, "Input Fields Missing!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}