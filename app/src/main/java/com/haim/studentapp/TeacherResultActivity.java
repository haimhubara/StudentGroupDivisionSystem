package com.haim.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TeacherResultActivity extends AppCompatActivity {
    String quizResultId;
    String quizId;
    String teacherId;
    CardView cardGroupOne,cardGroupTwo,cardGroupThree,cardStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_result);

        teacherId = getIntent().getStringExtra("teacherId");
        quizId = getIntent().getStringExtra("quizId");
        quizResultId = getIntent().getStringExtra("quizResultId");

        cardGroupOne = findViewById(R.id.cardGroup1);
        cardGroupTwo = findViewById(R.id.cardGroup2);
        cardGroupThree = findViewById(R.id.cardGroup3);
        cardStatistics = findViewById(R.id.cardStatistics);


        cardGroupOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherResultActivity.this,GroupActivity.class);
                intent.putExtra("upperBarrier","56");
                intent.putExtra("quizResultId",quizResultId);
                intent.putExtra("quizId",quizId);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);

            }
        });

        cardGroupTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherResultActivity.this,GroupActivity.class);
                intent.putExtra("lowerBarrier","56");
                intent.putExtra("upperBarrier","85");
                intent.putExtra("quizResultId",quizResultId);
                intent.putExtra("quizId",quizId);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });

        cardGroupThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherResultActivity.this,GroupActivity.class);
                intent.putExtra("lowerBarrier","85");
                intent.putExtra("quizResultId",quizResultId);
                intent.putExtra("quizId",quizId);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);


            }
        });

        cardStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherResultActivity.this,StatisticsActivity.class);
                intent.putExtra("quizResultId",quizResultId);
                intent.putExtra("quizId",quizId);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);

            }
        });

    }
}