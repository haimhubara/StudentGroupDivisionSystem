package com.haim.studentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {

    TextView txtNumberOfQuestion, txtQuestion;
    Button btnOptionOne, btnOptionTwo, btnOptionThree, btnOptionFour, btnOptionFive;
    HashMap<String, AmericanQuestion> updatedQuestionStats = new HashMap<>();
    String quizId;
    String teacherID;
    int numberOfQuestions;
    String teacherEmail;
    ArrayList<AmericanQuestion> questionList = new ArrayList<>();
    int currentQuestionIndex = 0;
    String answer;
    double grade;
    int correctAnswers = 0;
    String studentName;
    String teacherName;
    QuizResult newQuizResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtNumberOfQuestion = findViewById(R.id.txt_num_of_question);
        txtQuestion = findViewById(R.id.txt_question);
        btnOptionOne = findViewById(R.id.btn_option_1);
        btnOptionTwo = findViewById(R.id.btn_option_2);
        btnOptionThree = findViewById(R.id.btn_option_3);
        btnOptionFour = findViewById(R.id.btn_option_4);
        btnOptionFive = findViewById(R.id.btn_option_5);

        // Get the quiz details from the intent
        if (getIntent() != null) {
            quizId = getIntent().getStringExtra("quizId");
            numberOfQuestions = getIntent().getIntExtra("numberOfQuestions", 0);
            teacherEmail = getIntent().getStringExtra("teacherEmail");
            teacherID = getIntent().getStringExtra("teacherID");
            teacherName = getIntent().getStringExtra("teacherName");


        }

        //fetch student name
        fetchStudentName();


        // Fetch quiz questions from Firebase
        fetchQuizQuestions();
    }

    // Fetch the current student's name from Firebase
    private void fetchStudentName() {
        String studentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference studentRef = FirebaseDatabase.getInstance()
                .getReference("student")
                .child(studentUid);

        studentRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    studentName = dataSnapshot.getValue(String.class);
                    // Optionally, you can display the student name or use it in your logic
                } else {
                    Toast.makeText(QuizActivity.this, "Student name not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "Error fetching student name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch quiz questions and options from Firebase
    private void fetchQuizQuestions() {
        // Reference the correct path in Firebase where the quiz is stored
        DatabaseReference quizRef = FirebaseDatabase.getInstance()
                .getReference("teacher")
                .child(teacherID) // teacherID you fetched from the Intent
                .child("quiz")
                .child(quizId);   // quizId you fetched from the Intent

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Quiz quiz = dataSnapshot.getValue(Quiz.class);
                    if (quiz != null) {
                        questionList = quiz.getAmericanQuestionData();
                        loadQuestion(currentQuestionIndex);  // Load the first question
                    }
                } else {
                    Toast.makeText(QuizActivity.this, "Quiz not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "Error fetching quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Load the current question
    private void loadQuestion(int index) {
        if (index < questionList.size()) {
            AmericanQuestion currentQuestion = questionList.get(index);

            // Set question number and text
            txtNumberOfQuestion.setText("Question " + (index + 1) + " of " + numberOfQuestions);
            txtQuestion.setText(currentQuestion.getQuestion());

            // Set options for the question
            btnOptionOne.setText(currentQuestion.getOptionOne());
            btnOptionTwo.setText(currentQuestion.getOptionTwo());
            btnOptionThree.setText(currentQuestion.getOptionThree());
            btnOptionFour.setText(currentQuestion.getOptionFour());
            btnOptionFive.setText(currentQuestion.getOptionFive());
            answer = currentQuestion.getAnswer();
            // Set listeners for buttons
            setButtonListeners();
        }
    }

    // Set listeners for the option buttons
    private void setButtonListeners() {
        btnOptionOne.setOnClickListener(v -> checkAnswer(btnOptionOne.getText().toString()));
        btnOptionTwo.setOnClickListener(v -> checkAnswer(btnOptionTwo.getText().toString()));
        btnOptionThree.setOnClickListener(v -> checkAnswer(btnOptionThree.getText().toString()));
        btnOptionFour.setOnClickListener(v -> checkAnswer(btnOptionFour.getText().toString()));
        btnOptionFive.setOnClickListener(v -> checkAnswer(btnOptionFive.getText().toString()));
    }

    private void checkAnswer(String selectedAnswer) {
        AmericanQuestion currentQuestion = questionList.get(currentQuestionIndex);

        // Compare the selected answer with the correct answer
        if (selectedAnswer.equals(answer)) {
            correctAnswers++;
            Toast.makeText(QuizActivity.this, "Correct Answer!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(QuizActivity.this, "Wrong Answer!", Toast.LENGTH_SHORT).show();
        }

        // Update question statistics locally
        updateQuestionStatisticsLocally(currentQuestion, selectedAnswer);

        // Move to the next question
        nextQuestion();
    }

    private void updateQuestionStatisticsLocally(AmericanQuestion question, String selectedOption) {
        // Increment the selected option's count locally
        if (selectedOption.equals(question.getOptionOne())) {
            question.setOptionOneStats(question.getOptionOneStats() + 1);
        } else if (selectedOption.equals(question.getOptionTwo())) {
            question.setOptionTwoStats(question.getOptionTwoStats() + 1);
        } else if (selectedOption.equals(question.getOptionThree())) {
            question.setOptionThreeStats(question.getOptionThreeStats() + 1);
        } else if (selectedOption.equals(question.getOptionFour())) {
            question.setOptionFourStats(question.getOptionFourStats() + 1);
        } else if (selectedOption.equals(question.getOptionFive())) {
            question.setOptionFiveStats(question.getOptionFiveStats() + 1);
        }

        // Update the correct answer count locally
        if (selectedOption.equals(question.getAnswer())) {
            question.setCorrectAnswerCount(question.getCorrectAnswerCount() + 1);
        }

        // Save the updated question in the local map
        updatedQuestionStats.put(question.getQuestionID(), question);
    }


    // Move to the next question or end the quiz
    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            loadQuestion(currentQuestionIndex);
        } else {
            markQuizAsCompleted(quizId);  // Pass quizId instead of uid
            grade();
            updateQuizStatisticsInFirebase();
            updateCurrentStudentGrade();
            //newQuizResults = new QuizResult(teacherName,studentName,grade,teacherEmail);
            Intent intent = new Intent(QuizActivity.this,StudentActivity.class);
            startActivity(intent);
            finish();
            // End of quiz logic here
            Toast.makeText(QuizActivity.this, "Quiz Completed!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateQuizStatisticsInFirebase() {
        DatabaseReference quizRef = FirebaseDatabase.getInstance()
                .getReference("teacher")
                .child(teacherID)
                .child("quiz")
                .child(quizId)
                .child("americanQuestionData");

        for (String questionID : updatedQuestionStats.keySet()) {
            AmericanQuestion updatedQuestion = updatedQuestionStats.get(questionID);

            // Save the updated statistics back to Firebase
            quizRef.child(questionID).setValue(updatedQuestion).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                   // Toast.makeText(QuizActivity.this, "Statistics updated for question: " + questionID, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuizActivity.this, "Error updating statistics for question: " + questionID, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit without completing the quiz? The questions you answered will not be saved.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Exit the activity without saving
                    Intent intent = new Intent(QuizActivity.this, StudentActivity.class);
                    intent.putExtra("roll", "student");
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void grade(){
        grade = ((double) 100 /numberOfQuestions)*correctAnswers;

    }

    private void markQuizAsCompleted(String quizId) {
        // Get the current student's UID
        String studentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Reference to the "completedBy" field in Firebase for the specific quiz
        DatabaseReference quizRef = FirebaseDatabase.getInstance()
                .getReference("teacher")
                .child(teacherID) // Use teacherID from the activity
                .child("quiz")
                .child(quizId)
                .child("completedBy");

        // Fetch the current list of completed students, append the current student's UID
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String , Double> completedByList;
                if (dataSnapshot.exists()) {
                    completedByList = (HashMap<String, Double>) dataSnapshot.getValue();
                } else {
                    // If no list exists yet, create a new one
                    completedByList = new HashMap<>();
                }

                // Add the current student's UID to the list
                if (!completedByList.containsKey(studentUid)) {
                    completedByList.put(studentUid, 0.0);
                    // Save the updated list back to Firebase
                    quizRef.setValue(completedByList).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuizActivity.this, "Quiz marked as completed.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QuizActivity.this, "Error marking quiz as completed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(QuizActivity.this, "You have already completed this quiz.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "Error accessing quiz data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCurrentStudentGrade() {
        // Get the current student's UID
        String studentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Reference to the "completedBy" field in Firebase for the specific quiz
        DatabaseReference quizRef = FirebaseDatabase.getInstance()
                .getReference("teacher")
                .child(teacherID) // Use teacherID from the activity
                .child("quiz")
                .child(quizId)
                .child("completedBy");

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Double> completedByList;
                if (snapshot.exists()) {
                    completedByList = (HashMap<String, Double>) snapshot.getValue();
                } else {
                    // If no list exists yet, create a new one
                    completedByList = new HashMap<>();
                }

                // Update the current student's grade
                completedByList.put(studentUid, grade);

                // Save the updated list back to Firebase
                quizRef.setValue(completedByList).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuizActivity.this, "Grade updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuizActivity.this, "Error updating grade.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizActivity.this, "Error accessing quiz data.", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
