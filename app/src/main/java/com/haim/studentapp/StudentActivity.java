package com.haim.studentapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity implements QuizAdapter.MyClickInterface {

    RecyclerView quizRecyclerView;
    ArrayList<Quiz> quizzes;
    QuizAdapter quizAdapter;
    TextView emptyView;
    private ValueEventListener profileListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        emptyView = findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);


        quizRecyclerView = findViewById(R.id.recycle_student);
        quizzes = new ArrayList<>();
        quizAdapter = new QuizAdapter(quizzes, this, this);
        quizRecyclerView.setAdapter(quizAdapter);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        fetchQuizzesFromFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_prodile_menu, menu);
        MenuItem profileItem = menu.findItem(R.id.menu_item_profile);

        // Load the profile picture
        loadProfilePicture(profileItem);

        // Set up a listener for profile picture changes
        String currentUserID = FirebaseAuth.getInstance().getUid();
        profileListener = FirebaseDatabase.getInstance().getReference("student/" + currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isFinishing() && !isDestroyed()) {
                            loadProfilePicture(profileItem); // Reload profile picture if the activity is still valid
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors here
                    }
                });

        return true;
    }

    private void loadProfilePicture(MenuItem profileItem) {
        String currentUserID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("student/" + currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String profilePictureUrl = snapshot.child("profilePicture").getValue(String.class);
                            if (profilePictureUrl != null) {
                                Glide.with(StudentActivity.this)
                                        .asBitmap()
                                        .load(profilePictureUrl)
                                        .transform(new CircleCrop())
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                if (!isFinishing() && !isDestroyed()) {
                                                    profileItem.setIcon(new BitmapDrawable(getResources(), resource));
                                                }
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors here
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener to prevent memory leaks
        if (profileListener != null) {
            FirebaseDatabase.getInstance().getReference("student/" + FirebaseAuth.getInstance().getUid())
                    .removeEventListener(profileListener);
        }
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_profile) {
            Intent intent = new Intent(StudentActivity.this, ProfileActivity.class);
            intent.putExtra("roll", "student");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int positionOfQuiz) {
        // Get the selected quiz
        Quiz selectedQuiz = quizzes.get(positionOfQuiz);

        // Create an Intent to start the QuizActivity
        Intent intent = new Intent(StudentActivity.this, QuizActivity.class);


        // Pass the quiz details to QuizActivity
        intent.putExtra("quizId", selectedQuiz.getQuizId());  // Ensure quizId is valid
        intent.putExtra("numberOfQuestions", selectedQuiz.getNumberOfQuestion());
        intent.putExtra("teacherEmail", selectedQuiz.getTeacherEmail());
        intent.putExtra("teacherID", selectedQuiz.getTeacherID());
        intent.putExtra("teacherName",selectedQuiz.getTeacherName());
        // Start the QuizActivity
        startActivity(intent);
        finish();
    }

    private void fetchQuizzesFromFirebase() {
        DatabaseReference teachersReference = FirebaseDatabase.getInstance().getReference("teacher");
        String currentUID = FirebaseAuth.getInstance().getUid(); // Fetch UID once

        teachersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizzes.clear();  // Clear the list before adding new data
                if (snapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : snapshot.getChildren()) {
                        DataSnapshot quizSnapshot = teacherSnapshot.child("quiz");
                        for (DataSnapshot quizDataSnapshot : quizSnapshot.getChildren()) {
                            Quiz quiz = quizDataSnapshot.getValue(Quiz.class);
                            if (quiz != null) {
                                boolean hasCompleted = false;
                                for (String uid : quiz.getCompletedBy().keySet()) {
                                    if (uid.equals(currentUID)) {
                                        hasCompleted = true;
                                        break;  // No need to check further once found
                                    }
                                }
                                if (!hasCompleted) {
                                    quizzes.add(quiz);
                                }
                            }
                        }
                    }
                    quizAdapter.notifyDataSetChanged();

                    // Show or hide the empty view based on quiz availability
                    if (quizzes.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);  // Show "No quizzes available"
                        quizRecyclerView.setVisibility(View.GONE);  // Hide RecyclerView
                    } else {
                        emptyView.setVisibility(View.GONE);  // Hide "No quizzes available"
                        quizRecyclerView.setVisibility(View.VISIBLE);  // Show RecyclerView
                    }
                } else {
                    Toast.makeText(StudentActivity.this, "No quizzes found.", Toast.LENGTH_SHORT).show();
                    emptyView.setVisibility(View.VISIBLE);  // Show "No quizzes available"
                    quizRecyclerView.setVisibility(View.GONE);  // Hide RecyclerView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching quizzes: " + error.getMessage());
               // Toast.makeText(StudentActivity.this, "Failed to fetch quizzes.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
