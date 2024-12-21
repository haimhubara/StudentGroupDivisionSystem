package com.haim.studentapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    String quizResultId;
    String quizId;
    String teacherId;
    TextView txtNumOfQuestion, txtTheQuestion,
            txtOptionOne, txtOptionTwo, txtOptionThree, txtOptionFour, txtOptionFive, txtAnswer,
            txtOptionOneStats, txtOptionTwoStats, txtOptionThreeStats, txtOptionFourStats,
            txtOptionFiveStats, txtAnswerStats;
    Button btnNext, btnPrev;
    ArrayList<AmericanQuestion> questionsList = new ArrayList<>();
    int currentQuestionIndex = 0;
    DatabaseReference quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        teacherId = getIntent().getStringExtra("teacherId");
        quizId = getIntent().getStringExtra("quizId");
        quizResultId = getIntent().getStringExtra("quizResultId");

        // Initialize views
        txtNumOfQuestion = findViewById(R.id.tv_num_of_question);
        txtTheQuestion = findViewById(R.id.tv_question_qua);
        txtOptionOne = findViewById(R.id.tv_option_1);
        txtOptionTwo = findViewById(R.id.tv_option_2);
        txtOptionThree = findViewById(R.id.tv_option_3);
        txtOptionFour = findViewById(R.id.tv_option_4);
        txtOptionFive = findViewById(R.id.tv_option_5);
        txtAnswer = findViewById(R.id.tv_answer);
        txtOptionOneStats = findViewById(R.id.tv_option_1_stats);
        txtOptionTwoStats = findViewById(R.id.tv_option_2_stats);
        txtOptionThreeStats = findViewById(R.id.tv_option_3_stats);
        txtOptionFourStats = findViewById(R.id.tv_option_4_stats);
        txtOptionFiveStats = findViewById(R.id.tv_option_5_stats);
        txtAnswerStats = findViewById(R.id.tv_answer_stats);

        btnNext = findViewById(R.id.btn_next_statistics);
        btnPrev = findViewById(R.id.btn_prev_statistics);

        // Reference to the actual quiz data
        quizRef = FirebaseDatabase.getInstance()
                .getReference("teacher")
                .child(teacherId)
                .child("quiz")
                .child(quizId);

        // Load the quiz questions and their statistics
        loadQuizData();

        // Button click listeners
        btnNext.setOnClickListener(view -> {
            if (currentQuestionIndex < questionsList.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                Toast.makeText(this, "No more questions", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrev.setOnClickListener(view -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
            } else {
                Toast.makeText(this, "This is the first question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuizData() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot questionSnapshot : dataSnapshot.child("americanQuestionData").getChildren()) {
                    AmericanQuestion question = questionSnapshot.getValue(AmericanQuestion.class);
                    questionsList.add(question);
                }
                if (!questionsList.isEmpty()) {
                    displayQuestion();
                } else {
                    Toast.makeText(StatisticsActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StatisticsActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            AmericanQuestion currentQuestion = questionsList.get(currentQuestionIndex);

            txtNumOfQuestion.setText(currentQuestionIndex+1+"/"+questionsList.size());
            txtTheQuestion.setText("The question is :" +currentQuestion.getQuestion());
            txtOptionOne.setText("Option 1: "+currentQuestion.getOptionOne());
            txtOptionTwo.setText("Option 2: "+currentQuestion.getOptionTwo());
            txtOptionThree.setText("Option 3: "+currentQuestion.getOptionThree());
            txtOptionFour.setText("Option 4: "+currentQuestion.getOptionFour());
            txtOptionFive.setText("Option 5: "+currentQuestion.getOptionFive());

            txtOptionOneStats.setText("Stats: "+ currentQuestion.getOptionOneStats());
            txtOptionTwoStats.setText("Stats: "+ currentQuestion.getOptionTwoStats());
            txtOptionThreeStats.setText("Stats: "+ currentQuestion.getOptionThreeStats());
            txtOptionFourStats.setText("Stats: "+ currentQuestion.getOptionFourStats());
            txtOptionFiveStats.setText("Stats: "+ currentQuestion.getOptionFiveStats());
            txtAnswer.setText("The correct answer is : "+currentQuestion.getAnswer());
            txtAnswerStats.setText(String.valueOf("Stats: "+currentQuestion.getCorrectAnswerCount()));
        }
    }
}
