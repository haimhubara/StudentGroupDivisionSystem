package com.haim.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class IsStudentOrTeacherActivity extends AppCompatActivity {

    Button btnStudent,btnTeacher;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_student_or_teacher);

        intent = new Intent(this,MainActivity.class);

        btnStudent = findViewById(R.id.btnStudent);

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("isStudent",true);
                intent.putExtra("isTeacher",false);
                startActivity(intent);


            }
        });

        btnTeacher = findViewById(R.id.btnTeacher);

        btnTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("isStudent",false);
                intent.putExtra("isTeacher",true);
                startActivity(intent);



            }
        });



    }
}