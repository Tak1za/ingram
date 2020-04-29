package com.Tak1za.ingram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Tak1za.ingram.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestoreDb;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    Button signupButton;
    String firstName;
    String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);
        firestoreDb = FirebaseFirestore.getInstance();
    }

    public void signupUser(View view) {
        createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(signupButton.getWindowToken(), 0);
    }

    public void updateUserProfile(final String firstName, final String lastName, final FirebaseUser currentUser) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();
        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DebugLogs", "Updated user profile");
                            Toast.makeText(SignupActivity.this, "Signing up...", Toast.LENGTH_SHORT).show();
                            addUserToDb(firstName, lastName, currentUser);
                            Intent intent = new Intent(SignupActivity.this, ProfilePage.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    public void addUserToDb(String firstName, String lastName, FirebaseUser currentUser) {
        List<DocumentReference> emptyList = new ArrayList<>();
        User user = new User(firstName, lastName, currentUser.getEmail(), new Timestamp(new Date()), new Timestamp(new Date()), emptyList, emptyList, emptyList);

        firestoreDb
                .collection("users")
                .document(currentUser.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DebugLogs", "Added user to the database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DebugLogs", "Failed while adding user to the database");
                        return;
                    }
                });
    }

    public void createAccount(final String email, final String password) {
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty() && firstName != null & !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("DebugLogs", "createUserWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                updateUserProfile(firstName, lastName, currentUser);
                            } else {
                                //SignUp failed for some reason
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Log.d("DebugLogs", "userAlreadyExists");
                                    Toast.makeText(SignupActivity.this, "User with this email address already exists, either go back to login or use a different email", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.d("DebugLogs", "createUserWithEmail:failed", task.getException());
                                    Toast.makeText(SignupActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }
    }
}
