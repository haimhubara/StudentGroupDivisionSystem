package com.haim.studentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateAmericanQuestionActivity extends AppCompatActivity {

    private static int count = 1;
    private String quizId;
    private String quizResultId;
    String questionId;
    private String teacherName, teacherEmail, teacherID, teacherPicture;

    private EditText edtNumberOfQuestion, edtQuestionOne, edtQuestionTwo, edtQuestionThree, edtQuestionFour;
    private EditText edtQuestionFive, edtAnswer, edtQuestion;
    private TextView tvNumberOfQuestion;
     Button btnSubmit, btnPrev;

    private ArrayList<AmericanQuestion> americanQuestionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_american_question);

        edtNumberOfQuestion = findViewById(R.id.edt_num_of_question_qua);
        edtQuestion = findViewById(R.id.edt_question_qua);
        tvNumberOfQuestion = findViewById(R.id.tv_num_of_question);

        edtQuestionOne = findViewById(R.id.edt_option_1);
        edtQuestionTwo = findViewById(R.id.edt_option_2);
        edtQuestionThree = findViewById(R.id.edt_option_3);
        edtQuestionFour = findViewById(R.id.edt_option_4);
        edtQuestionFive = findViewById(R.id.edt_option_5);
        edtAnswer = findViewById(R.id.edt_answer);

        btnSubmit = findViewById(R.id.btn_submit);
        btnPrev = findViewById(R.id.btn_prev_american);

        americanQuestionsList = new ArrayList<>();
        count = 1;
        quizId = getIntent().getStringExtra("quizId");
        quizResultId = getIntent().getStringExtra("quizResultId");

        // Fetch teacher details from Firebase
        fetchTeacherDetails();

        btnSubmit.setOnClickListener(v -> handleNextQuestion());
        btnPrev.setOnClickListener(v -> handlePreviousQuestion());
    }

    private void fetchTeacherDetails() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            teacherEmail = auth.getCurrentUser().getEmail();

            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teacher").child(uid);
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        teacherName = snapshot.child("name").getValue(String.class);
                        teacherID = snapshot.child("teacherID").getValue(String.class);
                        teacherPicture = snapshot.child("profilePicture").getValue(String.class);

                        if (teacherName == null || teacherName.isEmpty()) teacherName = "Unknown Teacher";
                        if (teacherPicture == null) teacherPicture = "";
                    } else {
                        teacherName = "Unknown Teacher";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error fetching teacher details: " + error.getMessage());
                    teacherName = "Unknown Teacher";
                }
            });
        }
    }

    private void handleNextQuestion() {

        // Get the current question inputs
        String question = edtQuestion.getText().toString();
        String optionOne = edtQuestionOne.getText().toString();
        String optionTwo = edtQuestionTwo.getText().toString();
        String optionThree = edtQuestionThree.getText().toString();
        String optionFour = edtQuestionFour.getText().toString();
        String optionFive = edtQuestionFive.getText().toString();
        String answer = edtAnswer.getText().toString();

        // Validation: Ensure all fields are filled
        if (question.isEmpty() || optionOne.isEmpty() || optionTwo.isEmpty() || optionThree.isEmpty() ||
                optionFour.isEmpty() || optionFive.isEmpty() || answer.isEmpty()) {
            Toast.makeText(CreateAmericanQuestionActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        edtNumberOfQuestion.setVisibility(View.GONE);

        // Save or update the current question
        if (count <= americanQuestionsList.size()) {
            AmericanQuestion existingQuestion = americanQuestionsList.get(count - 1);
            existingQuestion.setQuestion(question);
            existingQuestion.setOptionOne(optionOne);
            existingQuestion.setOptionTwo(optionTwo);
            existingQuestion.setOptionThree(optionThree);
            existingQuestion.setOptionFour(optionFour);
            existingQuestion.setOptionFive(optionFive);
            existingQuestion.setAnswer(answer);
        } else {
            questionId = String.valueOf(count - 1);
            AmericanQuestion newQuestion = new AmericanQuestion(question, optionOne, optionTwo, optionThree, optionFour, optionFive, answer, questionId);
            americanQuestionsList.add(newQuestion);
        }

        // Move to the next question or load an existing one
        count++;
        String amountOfQuestions = edtNumberOfQuestion.getText().toString();
        try {
            int numberOfQuestions = Integer.parseInt(amountOfQuestions);

            if (count <= numberOfQuestions) {
                // Update the question number display
                tvNumberOfQuestion.setText(count + "/" + numberOfQuestions);

                // If the question already exists, display it in the text fields
                if (count <= americanQuestionsList.size()) {
                    AmericanQuestion nextQuestion = americanQuestionsList.get(count - 1);
                    edtQuestion.setText(nextQuestion.getQuestion());
                    edtQuestionOne.setText(nextQuestion.getOptionOne());
                    edtQuestionTwo.setText(nextQuestion.getOptionTwo());
                    edtQuestionThree.setText(nextQuestion.getOptionThree());
                    edtQuestionFour.setText(nextQuestion.getOptionFour());
                    edtQuestionFive.setText(nextQuestion.getOptionFive());
                    edtAnswer.setText(nextQuestion.getAnswer());
                } else {
                    // Clear input fields for a new question
                    clearInputFields();
                }
            } else {
                // If all questions are done, save the quiz
                createQuiz(numberOfQuestions);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(CreateAmericanQuestionActivity.this, "Invalid number of questions", Toast.LENGTH_SHORT).show();
            edtNumberOfQuestion.setVisibility(View.VISIBLE);

        }
    }

    private void clearInputFields() {
        edtQuestion.setText("");
        edtQuestionOne.setText("");
        edtQuestionTwo.setText("");
        edtQuestionThree.setText("");
        edtQuestionFour.setText("");
        edtQuestionFive.setText("");
        edtAnswer.setText("");
    }


    private void handlePreviousQuestion() {
        // Save the current question
        saveCurrentQuestion();

        // Go back to the previous question
        if (count > 1) {
            count--;
            tvNumberOfQuestion.setText(count + "/" + edtNumberOfQuestion.getText().toString());

            // Load the previous question data into the fields
            AmericanQuestion prevQuestion = americanQuestionsList.get(count - 1);
            edtQuestion.setText(prevQuestion.getQuestion());
            edtQuestionOne.setText(prevQuestion.getOptionOne());
            edtQuestionTwo.setText(prevQuestion.getOptionTwo());
            edtQuestionThree.setText(prevQuestion.getOptionThree());
            edtQuestionFour.setText(prevQuestion.getOptionFour());
            edtQuestionFive.setText(prevQuestion.getOptionFive());
            edtAnswer.setText(prevQuestion.getAnswer());
        } else {
            Toast.makeText(CreateAmericanQuestionActivity.this, "No previous question", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCurrentQuestion() {
        String question = edtQuestion.getText().toString();
        String optionOne = edtQuestionOne.getText().toString();
        String optionTwo = edtQuestionTwo.getText().toString();
        String optionThree = edtQuestionThree.getText().toString();
        String optionFour = edtQuestionFour.getText().toString();
        String optionFive = edtQuestionFive.getText().toString();
        String answer = edtAnswer.getText().toString();

        if (!question.isEmpty() && !optionOne.isEmpty() && !optionTwo.isEmpty() && !optionThree.isEmpty() &&
                !optionFour.isEmpty() && !optionFive.isEmpty() && !answer.isEmpty()) {
            // Check if it's an update or a new question
            if (count <= americanQuestionsList.size()) {
                AmericanQuestion existingQuestion = americanQuestionsList.get(count - 1);
                existingQuestion.setQuestion(question);
                existingQuestion.setOptionOne(optionOne);
                existingQuestion.setOptionTwo(optionTwo);
                existingQuestion.setOptionThree(optionThree);
                existingQuestion.setOptionFour(optionFour);
                existingQuestion.setOptionFive(optionFive);
                existingQuestion.setAnswer(answer);
            } else {
                AmericanQuestion newQuestion = new AmericanQuestion(question, optionOne, optionTwo, optionThree, optionFour, optionFive, answer, String.valueOf(count));
                americanQuestionsList.add(newQuestion);
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit without completing the quiz? The questions you added will not be saved.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Exit the activity without saving
                    if (quizResultId != null) {
                        // Delete the quiz result from Firebase
                        FirebaseDatabase.getInstance().getReference("teacher/quizResults").child(quizResultId).removeValue();
                    }
                    Intent intent = new Intent(CreateAmericanQuestionActivity.this, TeacherActivity.class);
                    intent.putExtra("roll", "teacher");
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void createQuiz(int numberOfQuestion) {
        HashMap<String, Double> completedBy = new HashMap<>();
        Quiz newQuiz = new Quiz(numberOfQuestion, americanQuestionsList, teacherName, teacherEmail, quizId, teacherID, completedBy,teacherPicture);

        DatabaseReference quizReference = FirebaseDatabase.getInstance().getReference("teacher")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("quiz")
                .child(quizId);

        // Store the entire quiz including all questions at once
        quizReference.setValue(newQuiz).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateAmericanQuestionActivity.this, "Quiz created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateAmericanQuestionActivity.this, TeacherActivity.class);
                intent.putExtra("roll", "teacher");
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CreateAmericanQuestionActivity.this, "Failed to create quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
