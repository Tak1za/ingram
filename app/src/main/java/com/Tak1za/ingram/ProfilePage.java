package com.Tak1za.ingram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.Tak1za.ingram.entity.UserProfile;
import com.Tak1za.ingram.models.Post;
import com.Tak1za.ingram.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;


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
            case R.id.addPost:
                Intent intent = new Intent(this, AddPostActivity.class);
                startActivity(intent);
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
                        getUserPosts(user);
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

    private void getUserPosts(final User user) {
        final List<Post> userPosts = new ArrayList<>();
        firestoreDb
                .collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    Post userPost = documentSnapshot.toObject(Post.class);
                                    userPosts.add(userPost);
                                }
                            }
                            postsCountTextView.setText("Posts: " + userPosts.size());
                            pokesCountTextView.setText("Pokes: " + user.getPokes().size());
                            followersCountTextView.setText("Followers: " + user.getFollowers().size());
                            followingCountTextView.setText("Following: " + user.getFollowing().size());
                            sugarcubesCountTextView.setText("Sugar Cubes: 0");
                            bioTextView.setText(user.getBio() != null && !user.getBio().isEmpty() ? user.getBio() : "");
                        } else {
                            Log.d("DebugLogs", "Failed to get posts");
                            try {
                                throw task.getException();
                            } catch (FirebaseFirestoreException e) {
                                Log.d("DebugLogs", e.getMessage());
                            } catch (Exception e) {
                                Log.d("DebugLogs", e.getMessage());
                            }
                        }
                    }
                });
    }
}
