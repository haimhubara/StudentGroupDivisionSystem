package com.haim.studentapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupActivity extends AppCompatActivity implements StudentAdapter.MyClickInterface {

    String quizResultId;
    String quizId;
    String teacherId;
    int lowerBarrier = 0;
    int upperBarrier = 100; // default values
    boolean hasLowerBarrier = false;
    boolean hasUpperBarrier = false;
    String studentId;

    RecyclerView recyclerView;
    ArrayList<Student> studentList;
    StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        recyclerView = findViewById(R.id.recycle_groups);
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, this, this);
        recyclerView.setAdapter(studentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Retrieve the barriers and other data from Intent
        quizResultId = getIntent().getStringExtra("quizResultId");
        quizId = getIntent().getStringExtra("quizId");
        teacherId = getIntent().getStringExtra("teacherId");
        Log.d("GroupActivity", "quizResultId: " + quizResultId + ", quizId: " + quizId + ", teacherId: " + teacherId);

        if (getIntent().hasExtra("lowerBarrier")) {
            lowerBarrier = Integer.parseInt(getIntent().getStringExtra("lowerBarrier"));
            hasLowerBarrier = true;
        }

        if (getIntent().hasExtra("upperBarrier")) {
            upperBarrier = Integer.parseInt(getIntent().getStringExtra("upperBarrier"));
            hasUpperBarrier = true;
        }

        // Fetch quiz result data and filter students based on the barriers
        fetchAndDisplayGroup();
    }

    private void fetchAndDisplayGroup() {
        Log.d("GroupActivity", "Fetching group with lowerBarrier: " + lowerBarrier + ", upperBarrier: " + upperBarrier);
        FirebaseDatabase.getInstance().getReference("teacher/" + teacherId + "/quiz/" + quizId + "/completedBy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            HashMap<String, Object> completedByMap = (HashMap<String, Object>) dataSnapshot.getValue(); // Use Object to store numbers
                            Log.d("GroupActivity", "Fetched CompletedBy data: " + completedByMap.toString());

                            if (completedByMap != null) {
                                for (Map.Entry<String, Object> entry : completedByMap.entrySet()) {
                                    studentId = entry.getKey();

                                    // Handle casting carefully
                                    double grade;
                                    if (entry.getValue() instanceof Long) {
                                        grade = ((Long) entry.getValue()).doubleValue(); // Convert Long to Double
                                    } else if (entry.getValue() instanceof Double) {
                                        grade = (Double) entry.getValue();
                                    } else {
                                        Log.e("GroupActivity", "Unexpected data type for grade");
                                        continue; // Skip if it's an unexpected data type
                                    }

                                    Log.d("GroupActivity", "Student UID: " + studentId + ", Grade: " + grade);

                                    // Only fetch students whose grades are within the barriers
                                    if (hasLowerBarrier && hasUpperBarrier) {
                                        if (grade >= lowerBarrier && grade <= upperBarrier) {
                                            fetchStudentDetails(studentId, grade);
                                        }
                                    } else if (hasLowerBarrier && grade >= lowerBarrier) {
                                        fetchStudentDetails(studentId, grade);
                                    } else if (hasUpperBarrier && grade <= upperBarrier) {
                                        fetchStudentDetails(studentId, grade);
                                    }
                                }
                            }
                        } else {
                            Log.d("GroupActivity", "No completedBy data found.");
                            Toast.makeText(GroupActivity.this, "No data found for the quiz results.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("GroupActivity", "Error fetching completedBy data: " + databaseError.getMessage());
                        Toast.makeText(GroupActivity.this, "Error accessing quiz data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void fetchStudentDetails(String studentUid, double grade) {
        Log.d("GroupActivity", "Fetching details for student UID: " + studentUid);
        FirebaseDatabase.getInstance().getReference("student/" + studentUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String password = dataSnapshot.child("password").getValue(String.class);
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String profilePicture = dataSnapshot.child("profilePicture").getValue(String.class);

                            Log.d("GroupActivity", "Student fetched: " + name);

                            // Add the student to the list and update RecyclerView
                            Student student = new Student(email, password, name, profilePicture);
                            studentList.add(student);
                            studentAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("GroupActivity", "No student data found for UID: " + studentUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("GroupActivity", "Error fetching student data: " + databaseError.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        // Handle student click if necessary
        Log.d("GroupActivity", "Student clicked at position: " + position);
    }
}
