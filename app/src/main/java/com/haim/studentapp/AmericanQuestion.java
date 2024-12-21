package com.haim.studentapp;

public class AmericanQuestion {
    private String question;
    private String optionOne;
    private String optionTwo;
    private String optionThree;
    private String optionFour;
    private String optionFive;
    private String answer;
    private String questionID;
    //to follow stats
    private int optionOneStats;
    private int optionTwoStats;
    private int optionThreeStats;
    private int optionFourStats;
    private int optionFiveStats;
    private int correctAnswerCount;



    public AmericanQuestion(String question, String optionOne, String optionTwo, String optionThree, String optionFour, String optionFive, String answer, String questionID) {
        this.question = question;
        this.optionOne = optionOne;
        this.optionTwo = optionTwo;
        this.optionThree = optionThree;
        this.optionFour = optionFour;
        this.optionFive = optionFive;
        this.answer = answer;
        this.questionID = questionID;

        optionOneStats = 0;
        optionTwoStats = 0;
        optionThreeStats = 0;
        optionFourStats = 0;
        optionFiveStats = 0;
        correctAnswerCount = 0;


    }
    public AmericanQuestion(){

    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionOne() {
        return optionOne;
    }

    public void setOptionOne(String optionOne) {
        this.optionOne = optionOne;
    }

    public String getOptionTwo() {
        return optionTwo;
    }

    public void setOptionTwo(String optionTwo) {
        this.optionTwo = optionTwo;
    }

    public String getOptionThree() {
        return optionThree;
    }

    public void setOptionThree(String optionThree) {
        this.optionThree = optionThree;
    }

    public String getOptionFour() {
        return optionFour;
    }

    public void setOptionFour(String optionFour) {
        this.optionFour = optionFour;
    }

    public String getOptionFive() {
        return optionFive;
    }

    public void setOptionFive(String optionFive) {
        this.optionFive = optionFive;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public int getOptionOneStats() {
        return optionOneStats;
    }

    public void setOptionOneStats(int optionOneStats) {
        this.optionOneStats = optionOneStats;
    }

    public int getOptionTwoStats() {
        return optionTwoStats;
    }

    public void setOptionTwoStats(int optionTwoStats) {
        this.optionTwoStats = optionTwoStats;
    }

    public int getOptionThreeStats() {
        return optionThreeStats;
    }

    public void setOptionThreeStats(int optionThreeStats) {
        this.optionThreeStats = optionThreeStats;
    }

    public int getOptionFourStats() {
        return optionFourStats;
    }

    public void setOptionFourStats(int optionFourStats) {
        this.optionFourStats = optionFourStats;
    }

    public int getOptionFiveStats() {
        return optionFiveStats;
    }

    public void setOptionFiveStats(int optionFiveStats) {
        this.optionFiveStats = optionFiveStats;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void setCorrectAnswerCount(int correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }
}
