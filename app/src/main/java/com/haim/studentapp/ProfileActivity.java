package com.haim.studentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class ProfileActivity extends AppCompatActivity {

    private Button btnLogOut, btnUpload;
    private Uri imagePath;
    private ImageView imageProfile;
    private String imageUrl;
    private String roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        imageProfile = findViewById(R.id.profile_img);


        roll = getIntent().getStringExtra("roll");

        btnUpload = findViewById(R.id.btnUploadImage);

        btnLogOut = findViewById(R.id.btnLogout);


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, IsStudentOrTeacherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        fetchImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            getImageInImagePath();
        }
    }

    private void getImageInImagePath(){
        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        imageProfile.setImageBitmap(bitmap);
    }
    private void uploadImage() {
        if (imagePath == null) {
            Toast.makeText(ProfileActivity.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            return; // Exit the method if no image has been selected
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // First, fetch the current profile picture URL
        FirebaseDatabase.getInstance().getReference(roll + "/" + userId + "/profilePicture")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            String currentProfilePictureUrl = task.getResult().getValue(String.class);

                            // Check if there's a profile picture to delete
                            if (currentProfilePictureUrl != null && !currentProfilePictureUrl.isEmpty()) {
                                // If there's an existing profile picture, delete it first
                                FirebaseStorage.getInstance().getReferenceFromUrl(currentProfilePictureUrl)
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> deleteTask) {
                                                if (deleteTask.isSuccessful()) {
                                                    // Old picture deleted, proceed with uploading new picture
                                                    uploadNewImage(userId, progressDialog);
                                                } else {
                                                    // Handle failure to delete old picture
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfileActivity.this, "Failed to delete previous image.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // No old picture exists, proceed with uploading new picture
                                uploadNewImage(userId, progressDialog);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed to retrieve profile picture.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadNewImage(String userId, ProgressDialog progressDialog) {
        // Set the file path to "user/{userId}/profilePicture/{uniqueId}.jpg"
        String filePath = "user/" + userId + "/profilePicture/" + UUID.randomUUID().toString();

        // Upload the new image to Firebase Storage
        FirebaseStorage.getInstance().getReference(filePath).putFile(imagePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String newImageUrl = task.getResult().toString();
                                        updateProfilePicture(newImageUrl);
                                    }
                                }
                            });
                            Toast.makeText(ProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                        progressDialog.setMessage(" Uploaded " + (int) progress + "%");
                    }
                });
    }

    private void updateProfilePicture(String url){
        FirebaseDatabase.getInstance().getReference(roll+"/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/profilePicture").setValue(url);

        if ("teacher".equals(roll)) {
            updateTeacherQuizzesPicture(url);
        }
    }

    private void updateTeacherQuizzesPicture(String newProfilePictureUrl) {
        String teacherID = FirebaseAuth.getInstance().getUid(); // Fetch the current teacher's UID
        FirebaseDatabase.getInstance().getReference("teacher/" + teacherID + "/quiz")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot quizSnapshot : task.getResult().getChildren()) {
                                // Get the quiz ID to update the profile picture in each quiz
                                String quizID = quizSnapshot.getKey();

                                // Update the profile picture field in each quiz created by the teacher
                                if (quizID != null) {
                                    FirebaseDatabase.getInstance().getReference("teacher/" + teacherID + "/quiz/" + quizID + "/quizPicture")
                                            .setValue(newProfilePictureUrl);
                                }
                            }
                            Toast.makeText(ProfileActivity.this, "Quizzes updated with new profile picture.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "No quizzes found to update.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchImage(){
        FirebaseDatabase.getInstance().getReference(roll+"/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/profilePicture")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // Fetch the image URL from Firebase
                            String imgUrl = task.getResult().getValue(String.class);
                            if (imgUrl != null && !imgUrl.isEmpty()) {
                                // If there's an image URL, load it with Glide
                                Glide.with(ProfileActivity.this)
                                        .load(imgUrl)
                                        .placeholder(R.drawable.account_img)  // Show placeholder during loading
                                        .error(R.drawable.account_img)        // Show default image if there's an error
                                        .into(imageProfile);
                            } else {
                                // No image found in the database, set default image
                                imageProfile.setImageResource(R.drawable.account_img);
                            }
                        } else {
                            // No image in the database, or some error occurred
                            imageProfile.setImageResource(R.drawable.account_img);
                        }
                    }
                });
    }





}