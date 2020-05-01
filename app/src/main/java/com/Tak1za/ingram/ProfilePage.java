package com.Tak1za.ingram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.Tak1za.ingram.entity.UserProfile;
import com.Tak1za.ingram.models.Post;
import com.Tak1za.ingram.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ProfilePage extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore firestoreDb;
    FirebaseStorage firebaseStorage;
    TextView postsCountTextView;
    TextView pokesCountTextView;
    TextView followersCountTextView;
    TextView sugarcubesCountTextView;
    TextView followingCountTextView;
    TextView bioTextView;
    ImageView profileImageView;
    ActionBar actionBar;

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
                Intent goToLoginIntent = new Intent(this, LoginActivity.class);
                startActivity(goToLoginIntent);
                finish();
                return true;
            case R.id.addPost:
                Intent intent = new Intent(this, AddPostActivity.class);
                startActivity(intent);
                return true;
            case R.id.editProfile:
                Intent intentEditProfile = new Intent(this, EditProfileActivity.class);
                startActivity(intentEditProfile);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        postsCountTextView = findViewById(R.id.postsCountTextView);
        pokesCountTextView = findViewById(R.id.pokesCountTextView);
        followersCountTextView = findViewById(R.id.followersCountTextView);
        followingCountTextView = findViewById(R.id.followingCountTextView);
        sugarcubesCountTextView = findViewById(R.id.sugarcubesCountTextView);
        bioTextView = findViewById(R.id.bioTextView);
        profileImageView = findViewById(R.id.profileImageView);

        actionBar = getSupportActionBar();
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
                        mAuth.signOut();
                        Intent goToLoginIntent = new Intent(ProfilePage.this, LoginActivity.class);
                        startActivity(goToLoginIntent);
                        finish();
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

                            if(user.getProfileImageName() != null && !user.getProfileImageName().isEmpty()) {
                                StorageReference profileImageRef = firebaseStorage.getReference().child(mAuth.getCurrentUser().getUid()).child("images").child(user.getProfileImageName());
                                final long ONE_MEGABYTE = 1024 * 1024 * 10;
                                profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        profileImageView.setImageBitmap(bitmap);
                                    }
                                });
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
