package com.haim.studentapp;

import java.util.ArrayList;
import java.util.HashMap;

public class Quiz {
    private String teacherName;
    private String teacherEmail;
    private String quizPicture;
    private String teacherID;
    private int numberOfQuestion;
    private String quizId;
    private ArrayList<AmericanQuestion> americanQuestionData;
    //private ArrayList<String> completedBy;
    private HashMap<String, Double> completedBy;

    // Default constructor
    public Quiz() {
        completedBy = new HashMap<>();
    }



    // Constructor to initialize quiz with a question and number of questions
    public Quiz(int numberOfQuestion, ArrayList<AmericanQuestion> americanQuestionData,String teacherName,String teacherEmail,String quizId,String teacherID, HashMap<String, Double> completedBy,String quizPicture) {
        this.numberOfQuestion = numberOfQuestion;
        this.americanQuestionData = americanQuestionData;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.quizId = quizId;
        this.teacherID = teacherID;
        this.completedBy = completedBy;
        this.quizPicture = quizPicture;

    }

    public HashMap<String, Double> getCompletedBy() {
        return completedBy;
    }


    public String getQuizPicture() {
        return quizPicture;
    }



    public String getTeacherID() {
        return teacherID;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    // Getter and Setter methods
    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public void setNumberOfQuestion(int numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public ArrayList<AmericanQuestion> getAmericanQuestionData() {
        return americanQuestionData;
    }

    public void setAmericanQuestionData(ArrayList<AmericanQuestion> americanQuestionData) {
        this.americanQuestionData = americanQuestionData;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
