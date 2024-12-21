package com.haim.studentapp;

public class QuizResult {
    private String teacherId;
    private String quizId;
    private String quizResultId;
    private String text = "Click to see result for quiz number";


    public QuizResult(String teacherId, String quizId,String quizResultId ) {
        this.teacherId = teacherId;
        this.quizId = quizId;
        this.quizResultId = quizResultId;
    }

    // Empty constructor (for Firebase)
    public QuizResult() {
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getQuizResultId() {
        return quizResultId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getText() {
        return text;
    }

    public String getQuizId() {
        return quizId;
    }
}


