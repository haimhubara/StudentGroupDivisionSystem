package com.haim.studentapp;

public class Teacher extends User {
    private String teacherID;
    private String employedNumber;
    private String roll = "teacher";

    public Teacher(String email, String password, String employedNumber, String name, String profilePicture, String teacherID) {
        super(email, password, name, profilePicture);
        this.employedNumber = employedNumber;
        this.teacherID = teacherID;
    }

    public Teacher() {

    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getEmployedNumber() {
        return employedNumber;
    }

    public void setEmployedNumber(String employedNumber) {
        this.employedNumber = employedNumber;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }


}

