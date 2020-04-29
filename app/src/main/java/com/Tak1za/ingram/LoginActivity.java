package com.Tak1za.ingram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText passwordEditText;
    TextView signupLinkTextView;
    Button loginButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupLinkTextView = findViewById(R.id.signupLinkTextView);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in and redirect to profile page if logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);
        }
    }

    public void goToSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view) {
        loginUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(loginButton.getWindowToken(), 0);
    }

    public void loginUser(String email, String password) {
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Sign in success, redirect to profile page
                                Log.d("DebugLogs", "signInWithEmailAndPassword:success");
                                Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, ProfilePage.class);
                                startActivity(intent);
                            } else {
                                //Sign in failed, display message to user
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Log.d("DebugLogs", "signInWithEmailAndPassword:failed", task.getException());
                                    Toast.makeText(LoginActivity.this, "No user found with these credentials, please try again or signup if you have not registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
        }
    }
}
