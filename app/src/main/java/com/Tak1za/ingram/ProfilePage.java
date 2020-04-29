package com.Tak1za.ingram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.Tak1za.ingram.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfilePage extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore firestoreDb;
    TextView postsCountTextView;
    TextView pokesCountTextView;
    TextView followersCountTextView;
    TextView sugarcubesCountTextView;
    TextView followingCountTextView;
    TextView bioTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                finish();
                return true;
            default:
                return false;
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
        postsCountTextView = findViewById(R.id.postsCountTextView);
        pokesCountTextView = findViewById(R.id.pokesCountTextView);
        followersCountTextView = findViewById(R.id.followersCountTextView);
        followingCountTextView = findViewById(R.id.followingCountTextView);
        sugarcubesCountTextView = findViewById(R.id.sugarcubesCountTextView);
        bioTextView = findViewById(R.id.bioTextView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mAuth.getCurrentUser().getDisplayName());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
    }

    @Override
    protected void onStart() {
        super.onStart();

        getUserProfile();
    }

    public void getUserProfile() {
        final DocumentReference docRef = firestoreDb.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("DebugLogs", "Fetched user: " + document.getData());
                        User user = document.toObject(User.class);

                        postsCountTextView.setText("Posts: 0");
                        pokesCountTextView.setText("Pokes: " + user.getPokes().size());
                        followersCountTextView.setText("Followers: " + user.getFollowers().size());
                        followingCountTextView.setText("Following: " + user.getFollowing().size());
                        sugarcubesCountTextView.setText("Sugar Cubes: 0");
                        bioTextView.setText(user.getBio() != null && !user.getBio().isEmpty() ? user.getBio() : "");
                    } else {
                        Log.d("DebugLogs", "No such document");
                        return;
                    }
                } else {
                    Log.d("DebugLogs", "Get user failed with ", task.getException());
                    return;
                }
            }
        });
    }
}
