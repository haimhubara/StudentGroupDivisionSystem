package com.haim.studentapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class TeacherActivity extends AppCompatActivity implements ResultsAdapter.MyClickInterface {

    ImageButton addAmericanQuestion;
    RecyclerView resultRecyclerView;
    ArrayList<QuizResult> results;
    ResultsAdapter resultsAdapter;
    private ValueEventListener profileListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        resultRecyclerView = findViewById(R.id.recycle);
        results = new ArrayList<>();

        // Initialize the adapter
        resultsAdapter = new ResultsAdapter(results, this, this);
        resultRecyclerView.setAdapter(resultsAdapter);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(resultRecyclerView);

        addAmericanQuestion = findViewById(R.id.img_add);


        // Handle button click for adding a new quiz
        addAmericanQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, CreateAmericanQuestionActivity.class);

                // Generate a unique quiz ID
                String quizId = FirebaseDatabase.getInstance().getReference("teacher").push().getKey();
                String currentTeacherID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                intent.putExtra("quizId", quizId);

                // Generate a unique result ID for the quiz result
                String quizResultId = FirebaseDatabase.getInstance().getReference("quizResults").push().getKey(); // Changed path here

                // Create the quiz result object with the result ID
                QuizResult quizResult = new QuizResult(currentTeacherID, quizId, quizResultId);
                intent.putExtra("quizResultId", quizResultId);

                if (quizResultId != null) {
                    // Save the quiz result to Firebase using the unique resultId as a key
                    FirebaseDatabase.getInstance().getReference("quizResults").child(quizResultId).setValue(quizResult); // Changed path here
                }

                // Start the activity after saving the quiz result
                startActivity(intent);
                finish();
            }
        });


// Fetch the results from Firebase
        fetchResultsFromFirebase();
    }

    // Add swipe-to-delete functionality
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false; // No need to handle drag & drop
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Get the position of the item that was swiped
            int position = viewHolder.getAdapterPosition();
            deleteQuiz(position); // Call the deletion method
        }
    };

    private void deleteQuiz(int position) {
        QuizResult quizResult = results.get(position);

        // Delete the quiz result from the separate "quizResults" node
        FirebaseDatabase.getInstance().getReference("quizResults") // Adjusted path
                .child(quizResult.getQuizResultId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Remove the quiz from the teacher's specific node
                        FirebaseDatabase.getInstance().getReference("teacher/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/quiz")
                                .child(quizResult.getQuizId())
                                .removeValue()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        // Quiz successfully deleted, remove it from the local list
                                        results.remove(position);
                                        resultsAdapter.notifyItemRemoved(position);
                                    } else {
                                        Toast.makeText(this, Objects.requireNonNull(task2.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_prodile_menu, menu);

        // Get the profile picture menu item (menu_item_profile)
        MenuItem profileItem = menu.findItem(R.id.menu_item_profile);

        // Load profile picture
        loadProfilePicture(profileItem);

        // Set up a listener for profile picture changes
        String currentUserID = FirebaseAuth.getInstance().getUid();
        profileListener = FirebaseDatabase.getInstance().getReference("teacher/" + currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Check if the activity is still in a valid state
                        if (!isFinishing() && !isDestroyed()) {
                            loadProfilePicture(profileItem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors here
                    }
                });

        return true;
    }

    // Method to load the profile picture
    private void loadProfilePicture(MenuItem profileItem) {
        String currentUserID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("teacher/" + currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String profilePictureUrl = snapshot.child("profilePicture").getValue(String.class);
                            if (profilePictureUrl != null) {
                                Glide.with(TeacherActivity.this)
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
            FirebaseDatabase.getInstance().getReference("teacher/" + FirebaseAuth.getInstance().getUid())
                    .removeEventListener(profileListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_profile) {
            Intent intent = new Intent(TeacherActivity.this, ProfileActivity.class);
            intent.putExtra("roll", "teacher");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int positionOfResult) {
        // logic for item clicked
        QuizResult quizResult = results.get(positionOfResult);
        Intent intent = new Intent(TeacherActivity.this,TeacherResultActivity.class);
        intent.putExtra("quizResultId",quizResult.getQuizResultId());
        intent.putExtra("quizId",quizResult.getQuizId());
        intent.putExtra("teacherId",quizResult.getTeacherId());
        startActivity(intent);
    }


    public void fetchResultsFromFirebase() {
        String currentTeacherID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Adjusting the reference to point to the separate quizResults node
        FirebaseDatabase.getInstance().getReference("quizResults")
                .orderByChild("teacherId") // Ensure there is a teacherId field in QuizResult
                .equalTo(currentTeacherID) // Filter results for the current teacher
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        results.clear();
                        if (snapshot.exists()) { // Check if there are any quiz results
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                QuizResult quizResult = dataSnapshot.getValue(QuizResult.class);

                                if (quizResult != null) { // Check for null to avoid crashes
                                    results.add(quizResult);
                                }
                            }
                        } else {
                            Toast.makeText(TeacherActivity.this, "No quizzes available for this teacher.", Toast.LENGTH_SHORT).show();
                        }
                        resultsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle any errors
                        Toast.makeText(TeacherActivity.this, "Error fetching quiz results: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
