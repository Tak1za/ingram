package com.Tak1za.ingram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Tak1za.ingram.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    ImageView profileImageView;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText bioEditText;
    Button changeImageButton;
    Button updateProfileButton;
    FirebaseFirestore firestoreDb;
    FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImageView = findViewById(R.id.profileImageView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        bioEditText = findViewById(R.id.bioEditText);
        firestoreDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        ActionBar actionBar = getSupportActionBar();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        getUserData();
    }

    private void getUserData() {
        final DocumentReference docRef = firestoreDb.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Log.d("DebugLogs", "Fetched user: " + documentSnapshot.getData());
                        User user = documentSnapshot.toObject(User.class);

                        if(user.getProfileImageName() != null && !user.getProfileImageName().isEmpty()){
                            downloadImageInMemory(user.getProfileImageName());
                        }

                        firstNameEditText.setText(user.getFirstName());
                        lastNameEditText.setText(user.getLastName());
                        bioEditText.setText(user.getBio());
                        emailEditText.setText(user.getEmail());
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

    private void downloadImageInMemory(String profileImageName) {
        StorageReference profileImageRef = firebaseStorage.getReference().child(mAuth.getCurrentUser().getUid()).child("images").child(profileImageName);
        final long ONE_MEGABYTE = 1024 * 1024 * 10;
        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileImageView.setImageBitmap(bitmap);
            }
        });
    }

    public void updateProfile(View view){
        if(profileImageView.getDrawable() != null) {
            profileImageView.setDrawingCacheEnabled(true);
            profileImageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            final String imageName = UUID.randomUUID().toString() + ".jpg";

            UploadTask uploadTask = firebaseStorage.getReference().child(mAuth.getCurrentUser().getUid()).child("images").child(imageName).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DebugLogs", "Unable to upload");
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("DebugLogs", "Upload successful");
                    updateProfileDetailsToDatabase(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), bioEditText.getText().toString(), imageName);
                }
            });
        } else {
            String imageName = "";
            updateProfileDetailsToDatabase(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), bioEditText.getText().toString(), imageName);
        }
    }

    private void updateProfileDetailsToDatabase(String fName, String lName, String bio, String imageName) {
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("bio", bio);
        updateUser.put("firstName", fName);
        updateUser.put("lastName", lName);
        updateUser.put("updatedAt", new Timestamp(new Date()));
        if(!imageName.isEmpty()) {
            updateUser.put("profileImageName", imageName);
        }
        firestoreDb
                .collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .update(updateUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DebugLogs", "Updated profile");
                        Toast.makeText(EditProfileActivity.this, "Updated profile", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DebugLogs", "Failed while updating profile");
                        Toast.makeText(EditProfileActivity.this, "Update profile failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void changeImage(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profileImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
