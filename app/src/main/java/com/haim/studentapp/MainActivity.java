package com.haim.studentapp;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText edtUsername, edtEmail, edtPassword, edtEmployeeNum;
    Button btnSignUp;
    TextView txtLoginInfo , txtTitle;
    boolean isSigningUp = true;
    boolean isTeacher = false;
    boolean isStudent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize views
        edtUsername = findViewById(R.id.edtUsername);
        edtEmployeeNum = findViewById(R.id.edtEmployeeNum);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.texLoginInfo);
        txtTitle = findViewById(R.id.textView);


        // Get Intent and its extras
        Intent intent = getIntent();
        if (intent != null) {
            isStudent = intent.getBooleanExtra("isStudent", false);
            isTeacher = intent.getBooleanExtra("isTeacher", false);
        }
        isSigningUp = false;

        // Configure UI based on the role
        if (isStudent) {
            student();
        } else if (isTeacher) {
            teacher();
        }


    }

    @SuppressLint("SetTextI18n")
    private void teacher() {
        txtTitle.setText("Teacher Log In");
        edtEmployeeNum.setVisibility(View.INVISIBLE);
        edtUsername.setVisibility(View.INVISIBLE);  // Hide Username and EmployeeNum inputs initially

        btnSignUp.setText("Log In");
        txtLoginInfo.setText("Don't have an account? Sign Up");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().isEmpty()||edtPassword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Invalid input",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isSigningUp){
                    handleSignUp();
                }
                else{
                    handleLogin();
                }
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (isSigningUp) {
                    isSigningUp = false;
                    edtEmployeeNum.setVisibility(View.INVISIBLE);
                    edtUsername.setVisibility(View.INVISIBLE);
                    btnSignUp.setText("Log In");
                    txtLoginInfo.setText("Don't have an account? Sign Up");
                    txtTitle.setText("Teacher Log In");
                } else {
                    isSigningUp = true;
                    edtEmployeeNum.setVisibility(View.VISIBLE);
                    edtUsername.setVisibility(View.VISIBLE);
                    btnSignUp.setText("Sign Up");
                    txtLoginInfo.setText("Already have an account? Log In");
                    txtTitle.setText("Teacher Sign Up");
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void student() {
        txtTitle.setText("Student Log In");
        edtEmployeeNum.setVisibility(View.INVISIBLE);
        edtUsername.setVisibility(View.INVISIBLE);  // Hide Username input initially

        btnSignUp.setText("Log In");
        txtLoginInfo.setText("Don't have an account? Sign Up");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().isEmpty()||edtPassword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Invalid input",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isSigningUp){
                    handleSignUp();
                }
                else{
                    handleLogin();
                }
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (isSigningUp) {
                    isSigningUp = false;
                    edtUsername.setVisibility(View.INVISIBLE);
                    btnSignUp.setText("Log In");
                    txtLoginInfo.setText("Don't have an account? Sign Up");
                    txtTitle.setText("Student Log In");
                } else {
                    isSigningUp = true;
                    edtUsername.setVisibility(View.VISIBLE);
                    btnSignUp.setText("Sign Up");
                    txtLoginInfo.setText("Already have an account? Log In");
                    txtTitle.setText("Student Sign Up");
                }
            }
        });
    }

    private void handleLogin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get the current user's UID
                    String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    // Determine if the user is a student or teacher based on their role
                    if (isStudent) {
                        FirebaseDatabase.getInstance().getReference("student/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                                    intent.putExtra("roll", "student");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "No student account found with this UID", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (isTeacher) {
                        FirebaseDatabase.getInstance().getReference("teacher/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                                    intent.putExtra("roll", "teacher");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "No teacher account found with this UID", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Signed Up successfully",Toast.LENGTH_SHORT).show();
                    if(isTeacher){
                        String email = edtEmail.getText().toString();
                        String password = edtPassword.getText().toString();
                        String name = edtUsername.getText().toString();
                        String employedNumber= edtEmployeeNum.getText().toString();
                        String teacherID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // Get the UID as teacherID
                        Teacher teacher = new Teacher(email, password, employedNumber, name,"", teacherID);

                        FirebaseDatabase.getInstance().getReference("teacher/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(teacher);
                        Intent intent = new Intent(MainActivity.this,TeacherActivity.class);
                        intent.putExtra("roll","teacher");
                        startActivity(intent);
                        finish();

                    }
                    else if(isStudent){
                        String email = edtEmail.getText().toString();
                        String password = edtPassword.getText().toString();
                        String name = edtUsername.getText().toString();

                        FirebaseDatabase.getInstance().getReference("student/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(new Student(email,password,name,""));
                        Intent intent = new Intent(MainActivity.this,StudentActivity.class);
                        intent.putExtra("roll","student");
                        startActivity(intent);
                        finish();

                    }
                }
                else{
                    Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}

